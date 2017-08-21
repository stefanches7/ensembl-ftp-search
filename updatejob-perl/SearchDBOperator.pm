=head1 NAME

SearchDBOperator.pm

=head1 SYNOPSIS

use SearchDBOperator;
my $dbop = SearchDBOperator->new($dsn, $user, $pssw);
my $dbh = $dbop->{dbh};
$dbh->{AutoCommit} = 0; #update db in transactions
$dbop->insertlinkrow(%rowingfo); #insert db record
$dbh->disconnect(); #finish work

=head1 DESCRIPTION

DBI database handler wrapper with useful functions for indices update job.

=cut

package SearchDBOperator;
use strict;
use warnings FATAL => 'all';
use DBI;

sub new {
    my $class = shift;
    my ($searchdbdsn, $searchdbuser, $searchdbpssw) = @_;
    my $searchdbh = DBI->connect($searchdbdsn, $searchdbuser, $searchdbpssw);
    my $self = bless {dbh => $searchdbh}, $class;
    return $self;
}

sub truncatelinktable {
    my $self = shift;
    my $tablename = shift;
    my $searchdbh = $self->{dbh};
    my $truncatesth = $searchdbh->prepare('TRUNCATE TABLE link;');
    $truncatesth->execute();
    $truncatesth->finish();
}

sub insertlinkrow {
    my $self = shift;
    my (%rowinfo) =@_;
    my $updatesql = 'INSERT INTO link (organism_name, file_type, link_url) values (?, ?, ?);';
    my $searchdbh = $self->{dbh};
    my $updatesth = $searchdbh->prepare_cached($updatesql);
    $updatesth->execute($rowinfo{"organism_name"}, $rowinfo{"file_type"}, $rowinfo{"link_url"})
        if exists $rowinfo{"organism_name"} && exists $rowinfo{"file_type"} && exists $rowinfo{"link_url"};
    $updatesth->finish();
}

sub updatesuggs {
    my $self = shift;
    my $searchdbh = $self->{dbh};
    #update organism_name suggestion table
    my $updatesuggsth = $searchdbh->prepare('INSERT INTO organism_name_suggestion (organism_name) SELECT DISTINCT organism_name FROM link;');
    $updatesuggsth->execute(); $updatesuggsth->finish();

    #update file_type suggestion table
    $updatesuggsth = $searchdbh->prepare('INSERT INTO file_type_suggestion (file_type) SELECT DISTINCT file_type FROM link;');
    $updatesuggsth->execute(); $updatesuggsth->finish();
    $updatesuggsth->finish();
}
1;