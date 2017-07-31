=head1 NAME

UpdateSearchDB.pl

=head1 SYNOPSIS

=head1 DESCRIPTION

Job of updating the FTP search database.

=cut

use DBI;
use strict;
use warnings FATAL => 'all';
use FTPFilenameUtil;
use FTPCrawler;
use feature qw/say/;

# full-length urls
my @entrypoints = ("ftp://ftp.ensemblgenomes.org/pub/release-36/metazoa/embl/acyrthosiphon_pisum/");

# search database credentials
my $searchdbdsn = "DBI:mysql:database=ensembl_api_test;host=localhost;port=3306";
my $searchdbuser = "springtest";
my $searchdbpssw = "ensemblrules";

our $ftpensembladdr = "ftp.ensembl.org";
our $ftpensemblgenomesaddr = "ftp.ensemblgenomes.org";

my $ftpclient = FTPCrawler->new($ftpensembladdr);
my $genomesftpclient = FTPCrawler->new($ftpensemblgenomesaddr);

say "Crawlers successfully created.";

my @filelinks = ();

for my $entrypoint (@entrypoints) {
    if ($entrypoint =~ /$ftpensembladdr/) {
        push @filelinks, $ftpclient->walk($entrypoint =~ m/$ftpensembladdr(.*)/);
    } elsif ($entrypoint =~ /$ftpensemblgenomesaddr/) {
        push @filelinks, $genomesftpclient->walk($entrypoint =~ m/$ftpensemblgenomesaddr(.*)/);
    } else {
        die "Specified entrypoint is not on the Ensembl site!";
    }
}

say "Entrypoints walked.";

my @dbreadyrows = map {FTPFilenameUtil->parsefileinfos} @filelinks;

say "DBrows looked like " . map {say} @dbreadyrows;

my $searchdbh = DBI->connect($searchdbdsn, $searchdbuser, $searchdbpssw);
# do not commit the changes after each statement
$searchdbh->{AutoCommit} = 0;

my $updatesql = 'INSERT INTO link (organism_name, file_type, link_url)';

my $updatesth = $searchdbh->prepare_cached($updatesql);

foreach my $rowinfo (@dbreadyrows) {
    $updatesth->execute($rowinfo->{organism_name}, $rowinfo->{file_type}, $rowinfo->{link_url});
}

$updatesth->finish();
# commit all the changes
$searchdbh->{AutoCommit} = 1;

$searchdbh->disconnect();


