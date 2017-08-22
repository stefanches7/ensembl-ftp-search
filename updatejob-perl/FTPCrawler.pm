=head1 NAME

FTPCrawler.pm

=head1 DESCRIPTION

Module used to recursively crawl the FTP sites of Ensembl updating search indices.

=cut
package FTPCrawler;
use strict;
use warnings FATAL => 'all';
use DBI;
use SearchDBOperator;
use FTPFilenameUtil;
use Net::FTP;
use Net::FTP::File;
use feature qw/say/;

sub initiate {
    my $class = shift;
    my ($hostname, $entrypoint, $searchdbop) = @_;
    my $ftp = Net::FTP->new($hostname) or die "Cannot connect to host {$hostname}";
    $ftp->login or die "Unable to login to the FTP site with blank credentials!";
    my $self = bless { ftpclient => $ftp, searchdbop => $searchdbop }, $class;
    return $self->walk($hostname, $entrypoint);
}

sub walk {
    my $self = shift;
    my ($hostname, $entrypoint) = @_;
    say "Walking " . $entrypoint;
    my $searchdbop = $self->{searchdbop};
    my $ftpclient = $self->{ftpclient};
    my @collectedlinks = ();
    
    $ftpclient->cwd($entrypoint);
    my @cwdlisting = $ftpclient->ls(); #only file names! (no top dirs)

    FILENAMES:
    foreach my $filename (@cwdlisting) {
        my $fullurl = $hostname . $entrypoint . "/" .  $filename;
        for my $regex (@FTPFilenameUtil::voidedregex) {
            if ($fullurl =~ /$regex/) {
                next FILENAMES;
            }
        }
        if ($ftpclient->isfile($filename)) {
            #write to the DB
            $searchdbop->insertlinkrow(FTPFilenameUtil->parsefileinfos($fullurl));
            # put the full URL of the file to array
            push @collectedlinks, $fullurl;
        }
        else {
            # put all the files yielded from recursive call to array
            push @collectedlinks, $self->walk($hostname, $entrypoint . "/" . $filename);
        }
    }

    return @collectedlinks;
}

1;