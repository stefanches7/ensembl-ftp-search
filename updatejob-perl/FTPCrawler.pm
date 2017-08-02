package FTPCrawler;
use strict;
use warnings FATAL => 'all';
use Net::FTP;
use Net::FTP::File;
use feature qw/say/;

sub initiate {
    my $class = shift;
    my ($hostname, $entrypoint) = @_;
    my $ftp = Net::FTP->new($hostname) or die "Cannot connect to host {$hostname}";
    $ftp->login or die "Unable to login to the FTP site with blank credentials!";
    my $self = bless { ftpclient => $ftp }, $class;
    return $self->walk($hostname, $entrypoint);
}

sub walk {
    my $self = shift;
    my ($hostname, $entrypoint) = @_;
    say "Walking " . $entrypoint;
    my $ftpclient = $self->{ftpclient};
    my @collectedlinks = ();
    
    $ftpclient->cwd($entrypoint);

    FILENAMES:
    foreach my $filename ($ftpclient->ls()) {
        for my $regex (@FTPFilenameUtil::voidedregex) {
            if ($filename =~ $regex) {
                next FILENAMES;
            }
        }
        if ($ftpclient->isfile($filename)) {
            say "Seeing the file " . $filename;
            # put the full URL of the file to array
            push @collectedlinks, $hostname . $entrypoint . "/" .  $filename;
        }
        else {
            say "$filename was a directory.";
            # put all the files yielded from recursive call to array
            push @collectedlinks, $self->walk($hostname, $entrypoint . "/" . $filename);
        }
    }

    return @collectedlinks;
}

1;