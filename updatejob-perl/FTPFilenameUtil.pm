package FTPFilenameUtil;
use strict;
use warnings FATAL => 'all';
use feature qw/say/;

our $ftpensembladdr = "ftp.ensembl.org";
our $ftpensemblgenomesaddr = "ftp.ensemblgenomes.org";

our @voidedregex = ("CHECKSUM", "README");

my %regexptocolumn_genomes = (
    "(?:ftp:\/\/)?\/{5}(a-z_)\/" => "organism_name",
    "(?:ftp:\/\/)?\/{4}(a-z_)\/" => "file_type"
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


    my %fileinfos = ();
    while (my ($key, $value) = each (%regexpmap)) {
        say "key: " . $key . ", value: " . $value;
        if ($fileurl =~ /(?:ftp:\/\/)?\/{5}(_a-z)/i) {
            say "Captured info: $1";
            $fileinfos{$value} = $1;
        }
    }
    return %fileinfos;
}

1;