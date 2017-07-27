package FTPFilenameUtil;
use strict;
use warnings FATAL => 'all';

my $ftpensembladdr = "ftp.ensembl.org";
my $ftpensemblgenomesaddr = "ftp.ensemblgenomes.org";

my %regexptocolumn = {
    "" => organism_name
    "" => file_type
};

my %regexptocolumn_genomes = {
    "" => organism_name
    "" => file_type
};

sub parsefileinfos($url) {
    my %regexpmap = {};
    if ($url =~ /$ftpensembladdr/) {
        %regexpmap = %regexptocolumn;
    }   elsif ($url =~ /$ftpensemblgenomesaddr/) {
        %regexpmap = %regexptocolumn_genomes;
    }   else {die "Specified URL is not an Ensembl FTP file!";}

    my %fileinfos = {};
    while (($key, $value) = each %regexpmap) {
        $fileinfos{$value} = $url =~ m/($key)/;
    }

    return %fileinfos;
}

1;