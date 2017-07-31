package FTPCrawler;
use strict;
use warnings FATAL => 'all';
use Net::FTP;

sub new {
    my $class = shift;
    my ($host) = @_;
    my $ftp = Net::FTP->new($host) or die "Cannot connect to host {$host}";
    return bless { ftpclient => $ftp }, $class;
}

sub walk {
    my $self = shift;
    my $entrypath = @_;
    my $ftpclient = $self->{ftpclient};
    my @filenames = ();
    
    $ftpclient->cwd($entrypath);
    
    foreach my $filename ($ftpclient->ls()) {
        if ($ftpclient->isfile($filename)) {
            # put the full URL of the file to array
            push @filenames, $ftpclient->pwd() . $filename;
        }
        else {
            # put all the files yielded from recursive call to array
            push @filenames, $self->walk($filename);
        }
    }

    return @filenames;
}

1;