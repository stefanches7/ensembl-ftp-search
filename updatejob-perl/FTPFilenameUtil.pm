=head1 NAME

FTPFilenameUtil.pm

=head1 SYNOPSIS

use FTPFilenameUtil;
my %infoshash = FTPFilenameUtil->parsefileinfos($ensemblftpsitefileurl);

=head1 DESCRIPTION

Used to get information about a file on Ensembl site looking at its URL. Also contains useful public values like white-
and blacklist FTP filename regex and servers' addresses.

=cut

package FTPFilenameUtil;
use strict;
use warnings FATAL => 'all';
use feature qw/say/;

our $ftpensembladdr = "ftp.ensembl.org";
our $ftpensemblgenomesaddr = "ftp.ensemblgenomes.org";

# This regex is being ignored once seen by the crawler. Recursive crawling stops as well.
our @voidedregex = ("CHECKSUM", "README", "\/mysql\/", ".ova", "species_", "_genomes.txt", "uniprot_", "web_","\/xml\/", "\/bamcov\/",
    "\/bed\/","\/blat\/","\/compara\/","\/data_files\/","\/emf\/","\/maf\/","\/ncbi_blast\/","\/regulation\/","\/solr_srch\/",
    "\/assembly_chain\/assembly_chain");#FIXME remove ftp.ensembl.org/assembly_chain/assembly_chain file leading to nowhere
# "Normal" datatypes which share the same directory structure. fasta and vep formats are handled separately.
our @whitelistdatatypes = ("\/assembly_chain\/", "\/embl\/", "\/genbank\/", "\/gff3\/", "\/gtf\/", "\/gvf\/",
                            "\/vcf\/", "\/rdf\/", "\/tsv\/", "\/json\/");

sub parsefileinfos {
    my $self = shift;
    my ($fileurl) = @_;
    say "Parsing infos from " . $fileurl;
    my $whitelistdatatypesunionstr = join "|", @whitelistdatatypes;

    my %fileinfos = ("link_url" => $fileurl);

    if ($fileurl =~ /$whitelistdatatypesunionstr/) {
        #check if the link leads to the whitelist datatype file
        if ($fileurl =~ /($whitelistdatatypesunionstr) #capture one of the "normal" whitelist datatypes
            (?:.*?_collection\/)? # optionally bypass the collection dir without capturing
            (.*?)\/ #capture anything (i.e., organism_name) until the next fwd slash
            /x) {
            $fileinfos{"file_type"} = $1;
            $fileinfos{"organism_name"} = $2;
            $fileinfos{"file_type"} =~ s/\///g; #strip filetype of backslashes
        }
    }
    elsif ($fileurl =~ /\/fasta\//i) {
        if ($fileurl =~ /\/fasta\/(?:.*?_collection\/)?
            (.*?)\/ #capture the organism name non-greedy
            (.*?)\/ #capture the fasta subtype (e.g. cdna) non-greedy
            /xi) {
            $fileinfos{"organism_name"} = $1;
            $fileinfos{"file_type"} = "fasta_" . $2;
        }
    }
    elsif ($fileurl =~ /\/vep\//i) {
        #variation file
        $fileinfos{"organism_name"} = $1 if $fileurl =~ /\/vep\/(?:.*?_collection\/)?(.*?)(?:_merged|_refseq)?_vep_*/i;
        #parse the organism name straight out of filename, anything until "_vep_" prefix
        $fileinfos{"file_type"} = "vep";
    }

    return %fileinfos;
}

1;