#!/bin/bash

if [ -z $POSTGRES_HOME ]; then
    echo "System variable POSTGRES_HOME has not been set"
    exit 1
fi

PSQL=$POSTGRES_HOME/bin/psql

$PSQL -U postgres -f ../../core/dal/src/main/resources/sql/drop_create_database.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Re-create Database script unsuccessful: " $result
   exit $result
fi

temp_dir="generated"
if [ ! -d "$temp_dir" ]
then
    mkdir "$temp_dir"
fi

export PGPASSWORD=password

generate-sql.sh $temp_dir op
$PSQL -U postgres -f $temp_dir/op.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Schema recreation script unsuccessful: " $result
   exit $result
fi

rm $temp_dir/op.sql
rmdir "$temp_dir"