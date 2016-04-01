#!/usr/bin/perl

if(length($ARGV[0])>1) {
    $temp_dir = $ARGV[0];
    $table_name = $ARGV[1];
} else {
    print "Please specify the directory and table name\n";
    exit 1;
}

$input_schema = "../../core/dal/src/main/resources/sql/schema.sql";

print "Reading in the schema file: $input_schema\n";
open(sqlfile, $input_schema) or die("Unable to open SQL file: $input_schema");
local $/=undef; #makes it possible to slurp up the whole file rather than one line at a time...
$sqltext = <sqlfile>;
close(sqlfile);

$op_commands = add_create_table_command("$table_name");
write_file("generated/$table_name.sql", $op_commands);

sub add_create_table_command() {
    my($db_table_name) = @_;

    print "Adding create table command for table $table_name....\n";
    my $new_content = "\\c $db_table_name morpho\n\n$sqltext";
    return $new_content;
}

sub write_file() {
    my($filename, $sqlContent) = @_;
    print "Writing processed SQL commands out to file $filename\n";
    open(sqlfile, '>', $filename);
    print sqlfile "$sqlContent";
    close(sqlfile);
}