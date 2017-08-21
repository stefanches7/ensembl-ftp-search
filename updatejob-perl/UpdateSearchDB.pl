=head1 NAME

UpdateSearchDB.pl

=head1 SYNOPSIS

perl UpdateSearchDB.pl --dbdsn=localhost:3306/ensembl_api_test --dbuser='springtest' --dbpssw='ensemblrules'
--entrypoint='ftp://ftp.ensembl.org/pub/release-89/bed','ftp://ftp.ensembl.org/pub/release-89/bamcov' --entrypointsfile=./entrypoints.list

=head1 DESCRIPTION

Job of updating the FTP search database.

=cut

use DBI;
use strict;
use warnings FATAL => 'all';
use FTPFilenameUtil;
use FTPCrawler;
use Getopt::Long;
use feature qw/say/;

# full-length urls
# NB! Links are supposed to be leafs of current release endpoints (e.g. ftp.ensembl.org/pub/release-XX)!
my @entrypoints = ("ftp://ftp.ensembl.org/pub/release-89/");
my $entrypointsfile = '';

# search database credentials
my $searchdbdsn = "DBI:mysql:database=ensembl_api_test;host=localhost;port=3306";
my $searchdbuser = "springtest";
my $searchdbpssw = "ensemblrules";

#parse cmd params
GetOptions ("dbdsn=s" => \$searchdbdsn,
            "dbuser=s" => \$searchdbuser,
            "dbpssw=s" => \$searchdbpssw,
            "entrypoint=s" => \@entrypoints,
            "entrypointsfile=s" => \$entrypointsfile);
@entrypoints = split(/,/,join(',',@entrypoints));

if ( defined $entrypointsfile) { #load entrypoints from file
    open my $fh, $entrypointsfile or die "Couldn't open $entrypointsfile: $!";
    @entrypoints = ();
    while (my $line = $fh) {
        my @separatedvalues = split ',', $line; #allow user to separate entrypoints with comma
        push @entrypoints, @separatedvalues;
    }
    close $fh;
}

my @filelinks = ();

my $searchdbop = SearchDBOperator->new($searchdbdsn, $searchdbuser, $searchdbpssw);
my $dbh = $searchdbop->{dbh};
my $starttime = localtime();
say "Starting update job at $starttime";

for my $entrypoint (@entrypoints) {
    $entrypoint =~ s/\/$//; #remove trailing fwd slash in entrypoints
    if ($entrypoint =~ /($FTPFilenameUtil::ftpensembladdr|$FTPFilenameUtil::ftpensemblgenomesaddr)(.*)/i) {
        push @filelinks, FTPCrawler->initiate($1, $2, $searchdbop);
    } else {
        die "Specified entrypoint is not an FTP site of Ensembl!";
    }
}

$searchdbop->updatesuggs(); #update suggestion tables using the new link rows
$dbh->disconnect();

my $finishtime = localtime();
say "Finished update job at $finishtime";


