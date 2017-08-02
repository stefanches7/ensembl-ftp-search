package FTPFilenameUtil;
use strict;
use warnings FATAL => 'all';
use feature qw/say/;

our $ftpensembladdr = "ftp.ensembl.org";
our $ftpensemblgenomesaddr = "ftp.ensemblgenomes.org";

our @voidedregex = ("CHECKSUM", "README");

my %regexptocolumn_genomes = (
    qr{(?:ftp:\/{2})?(?:.*?\/){5} #pass the fwd slash 5 times(.*?)\/} => "organism_name",
    qr{(?:ftp:\/{2})?(?:.*?\/){4} #pass the fwd slash 4 times(.*?)\/} => "file_type"
        );

my %regexptocolumn = (
    "" => "organism_name",
    "" => "file_type",
);

sub parsefileinfos {
    my $self = shift;
    my ($fileurl) = @_;
    say "Parsing infos from " . $fileurl;
    my %regexpmap = ();
    if ($fileurl =~ /$ftpensembladdr/) {
        %regexpmap = %regexptocolumn;
    }   elsif ($fileurl =~ /$ftpensemblgenomesaddr/) {
        %regexpmap = %regexptocolumn_genomes;
    }   else {die "Specified URL is not an Ensembl FTP file!";}


    my %fileinfos = ("link_url" => $fileurl);
    while (my ($key, $value) = each (%regexpmap)) {
        say "key: " . $key . ", value: " . $value;
        if ($fileurl =~ /$key/xi) {
            say "Captured info: $1";
            $fileinfos{$value} = $1;
        }
    }
    return %fileinfos;
}

1;