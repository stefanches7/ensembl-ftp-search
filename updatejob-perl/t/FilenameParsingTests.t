#!/usr/bin/perl
use strict;
use warnings;
use Test::More tests => 9;
use lib '..';
use FTPFilenameUtil;

my %fastafi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensemblgenomes.org/pub/release-36/metazoa/fasta/" .
"acyrthosiphon_pisum/cdna/Acyrthosiphon_pisum.GCA_000142985.2.cdna.all.fa.gz");
is($fastafi{"file_type"}, "fasta_cdna", "parse fasta file type with subtype");
is($fastafi{"organism_name"}, "acyrthosiphon_pisum", "parse fasta file organism name");
my %vepfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensemblgenomes.org/pub/release-36/metazoa/vep/rhodnius_prolixus_vep_36_RproC1.tar.gz");
is($vepfi{"file_type"}, "vep", "parse vep file format");
is($vepfi{"organism_name"}, "rhodnius_prolixus", "parse vep organism name");
my %usualwhitelfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensembl.org/pub/release-89/assembly_chain/bos_taurus/Btau_4.0_to_UMD3.1.chain.gz");
is($usualwhitelfi{"file_type"}, "assembly_chain", "parse normal whitelist datatype");
is($usualwhitelfi{"organism_name"}, "bos_taurus", "parse normal whitelist datatype's organism name");
my %blacklistfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensembl.org/pub/release-89/mysql/"
    . "ailuropoda_melanoleuca_core_89_1/ailuropoda_melanoleuca_core_89_1.sql.gz");
is($blacklistfi{"link_url"}, "ftp://ftp.ensembl.org/pub/release-89/mysql/"
        . "ailuropoda_melanoleuca_core_89_1/ailuropoda_melanoleuca_core_89_1.sql.gz", "parse full-url (even) for blacklist links" );
ok(!exists $blacklistfi{"file_type"}, "skip parsing file type of a blacklist file");
ok(!exists $blacklistfi{"organism_name"}, "skip parsing organism name of a blacklist file");

done_testing();

