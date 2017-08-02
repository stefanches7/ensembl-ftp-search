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
my @entrypoints = ("ftp.ensemblgenomes.org/pub/release-36/metazoa/embl/acyrthosiphon_pisum", "ftp://ftp.ensemblgenomes.org/pub/release-36/metazoa/vcf");

# search database credentials
my $searchdbdsn = "DBI:mysql:database=ensembl_api_test;host=localhost;port=3306";
my $searchdbuser = "springtest";
my $searchdbpssw = "ensemblrules";

my @filelinks = ();

for my $entrypoint (@entrypoints) {
    if ($entrypoint =~ /($FTPFilenameUtil::ftpensembladdr|$FTPFilenameUtil::ftpensemblgenomesaddr)(.*)/i) {
        push @filelinks, FTPCrawler->initiate($1, $2);
    } else {
        die "Specified entrypoint is not an FTP site of Ensembl!";
    }
}

say for @filelinks;

my $searchdbh = DBI->connect($searchdbdsn, $searchdbuser, $searchdbpssw);
# do not commit the changes after each statement
$searchdbh->{AutoCommit} = 0;

my $updatesql = 'INSERT INTO link (?, ?, ?)';

my $updatesth = $searchdbh->prepare_cached($updatesql);

foreach my $link (@filelinks) {
    my %rowinfo = FTPFilenameUtil->parsefileinfos($link);
    $updatesth->execute($rowinfo{"organism_name"}, $rowinfo{"file_type"}, $rowinfo{"link_url"});
}

$updatesth->finish();
# commit all the changes
$searchdbh->{AutoCommit} = 1;

$searchdbh->disconnect();


