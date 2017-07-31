package FTPFilenameUtil;
use strict;
use warnings FATAL => 'all';

my $ftpensembladdr = "ftp.ensembl.org";
my $ftpensemblgenomesaddr = "ftp.ensemblgenomes.org";

my %regexptocolumn = (
    "" => "organism_name",
    "" => "file_type"
        );

my %regexptocolumn_genomes = (
    "" => "organism_name",
    "" => "file_type",
);

sub parsefileinfos {
    my $fileurl = shift;
    my %regexpmap = {};
    if ($fileurl =~ /$ftpensembladdr/) {
        %regexpmap = %regexptocolumn;
    }   elsif ($fileurl =~ /$ftpensemblgenomesaddr/) {
        %regexpmap = %regexptocolumn_genomes;
    }   else {die "Specified URL is not an Ensembl FTP file!";}

    my %fileinfos = {};
    while (my ($key, $value) = each %regexpmap) {
        $fileinfos{$value} = $fileurl =~ m/($key)/;
    }

    return %fileinfos;
}

1;