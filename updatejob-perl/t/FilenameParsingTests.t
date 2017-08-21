#!/usr/bin/perl
use strict;
use warnings;
use Test::More tests => 15;
use lib '..';
use FTPFilenameUtil;

#test parsing fasta data
my %fastafi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensemblgenomes.org/pub/release-36/metazoa/fasta/" .
"acyrthosiphon_pisum/cdna/Acyrthosiphon_pisum.GCA_000142985.2.cdna.all.fa.gz");
is($fastafi{"file_type"}, "fasta_cdna", "parse fasta file type with subtype");
is($fastafi{"organism_name"}, "acyrthosiphon_pisum", "parse fasta file organism name");
#test parsing vep data
my %vepfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensemblgenomes.org/pub/release-36/metazoa/vep/rhodnius_prolixus_vep_36_RproC1.tar.gz");
is($vepfi{"file_type"}, "vep", "parse vep file format");
is($vepfi{"organism_name"}, "rhodnius_prolixus", "parse vep organism name");
#test normal whitelist datatype
my %usualwhitelfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensembl.org/pub/release-89/assembly_chain/bos_taurus/Btau_4.0_to_UMD3.1.chain.gz");
is($usualwhitelfi{"file_type"}, "assembly_chain", "parse normal whitelist datatype");
is($usualwhitelfi{"organism_name"}, "bos_taurus", "parse normal whitelist datatype's organism name");
#test blacklist datatype
my %blacklistfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensembl.org/pub/release-89/mysql/"
    . "ailuropoda_melanoleuca_core_89_1/ailuropoda_melanoleuca_core_89_1.sql.gz");
is($blacklistfi{"link_url"}, "ftp://ftp.ensembl.org/pub/release-89/mysql/"
        . "ailuropoda_melanoleuca_core_89_1/ailuropoda_melanoleuca_core_89_1.sql.gz", "parse full-url (even) for blacklist links" );
ok(!exists $blacklistfi{"file_type"}, "skip parsing file type of a blacklist file");
ok(!exists $blacklistfi{"organism_name"}, "skip parsing organism name of a blacklist file");
#test protist example of _collection
my %collexfi = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensemblgenomes.org/pub/release-36/protists/fasta/protists_" .
"cryptophyta1_collection/cryptomonas_paramecium/cdna/Cryptomonas_paramecium.ASM19445v1.cdna.all.fa.gz");
is($collexfi{"organism_name"}, "cryptomonas_paramecium", "skip _collection directory in parsing");
is($collexfi{"file_type"}, "fasta_cdna", "parse the right data type from collection member");
#test ftp.ensembl.org
my %vanillaensvep = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensembl.org/pub/release-89/variation/VEP/"
    ."ailuropoda_melanoleuca_vep_89_ailMel1.tar.gz");
is($vanillaensvep{"file_type"}, "vep", "parse vep from ftp.ensembl.org");
is($vanillaensvep{"organism_name"}, "ailuropoda_melanoleuca", "parse vep organism name from ftp.ensembl.org");
my %vanillaensac = FTPFilenameUtil->parsefileinfos("ftp://ftp.ensembl.org/pub/release-89/assembly_chain/"
    . "bos_taurus/Btau_4.0_to_UMD3.1.chain.gz");
is($vanillaensac{"file_type"}, "assembly_chain", "parse normal whitelist datatype from ftp.ensembl.org");
is($vanillaensac{"organism_name"}, "bos_taurus", "parse normal whitelist datatype's organism name from ftp.ensembl.org");

done_testing();

