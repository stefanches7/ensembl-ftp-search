=head1 NAME

UpdateSearchDB.pl

=head1 SYNOPSIS

perl UpdateSearchDB.pl --dbname='ensembl_api_test' --dbport=8080 --dbhost='localhost' --truncatetables --dbuser='springtest' --dbpssw='ensemblrules'
--entrypoint='ftp://ftp.ensembl.org/pub/release-89/bed','ftp://ftp.ensembl.org/pub/release-89/bamcov' --entrypointsfile=./entrypoints.list

=head1 DESCRIPTION

Job of updating the FTP search database. Parameters description:

* dbname - name of database _being updated_.
* dbport - port of this database.
* dbhost - IP on which this database is hosted.
* dbuser, dbpssw - this database's access credentials.
* entrypoint - entrypoint(s) to start recursive crawling from. Separate with comma ",".
* entrypointsfile - file that contains crawling entrypoints either separated with comma or with newline. _Overwrites_
entrypoints specified with the "entrypoint" parameter.

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
my @entrypoints = ();
my $entrypointsfile = '';

# search database credentials
my $dbname = "ensembl_api_test";
my $dbhost = "localhost";
my $dbport = "3306";
my $searchdbuser = "springtest";
my $searchdbpssw = "ensemblrules";
my $truncatetables = 0;

#parse cmd params
GetOptions ("dbname=s" => \$dbname,
            "dbhost=s" => \$dbhost,
            "dbport=s" => \$dbport,
            "dbuser=s" => \$searchdbuser,
            "dbpssw=s" => \$searchdbpssw,
            "entrypoint=s" => \@entrypoints,
            "entrypointsfile=s" => \$entrypointsfile,
            "truncatetables" => \$truncatetables);

@entrypoints = split(/,/,join(',',@entrypoints));

my $searchdbdsn = "DBI:mysql:database=$dbname;host=$dbhost;port=$dbport";

if ($entrypointsfile) { #load entrypoints from file
    open my $fh, $entrypointsfile or die "Couldn't open $entrypointsfile: $!";
    @entrypoints = ();
    while (my $line = <$fh>) {
        my @separatedvalues = split ',', $line; #allow user to separate entrypoints with comma
        for my $ep (@separatedvalues) {
            say "Got entrypoint: $ep";
            push @entrypoints, $ep;
        }
    }
    close $fh;
}

my @filelinks = ();

my $searchdbop = SearchDBOperator->new($searchdbdsn, $searchdbuser, $searchdbpssw);
my $dbh = $searchdbop->{dbh};
my $starttime = localtime();
say "Starting update job at $starttime";

#clean tables before updating if specified
if ($truncatetables) {
    $searchdbop->truncatetables();
}

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


