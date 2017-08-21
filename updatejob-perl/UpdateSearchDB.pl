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
# NB! Links are supposed to be leafs of current release endpoints (e.g. ftp.ensembl.org/pub/release-XX)!
my @entrypoints = ("ftp://ftp.ensembl.org/pub/release-89/");

# search database credentials
my $searchdbdsn = "DBI:mysql:database=ensembl_api_test;host=localhost;port=3306";
my $searchdbuser = "springtest";
my $searchdbpssw = "ensemblrules";

my @filelinks = ();

my $searchdbop = SearchDBOperator->new($searchdbdsn, $searchdbuser, $searchdbpssw);
my $dbh = $searchdbop->{dbh};
my $starttime = localtime();
say "Starting update job at $starttime";

for my $entrypoint (@entrypoints) {
    $entrypoint =~ s/\/$//;
    if ($entrypoint =~ /($FTPFilenameUtil::ftpensembladdr|$FTPFilenameUtil::ftpensemblgenomesaddr)(.*)/i) {
        push @filelinks, FTPCrawler->initiate($1, $2, $searchdbop);
    } else {
        die "Specified entrypoint is not an FTP site of Ensembl!";
    }
}

$searchdbop->updatesuggs();
$dbh->disconnect();
my $finishtime = localtime();
say "Finished update job at $finishtime";


