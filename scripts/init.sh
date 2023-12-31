#!/bin/bash

export PGUSER=pondionstracker
export PGPASSWORD=pondionstracker
export PGDATABASE=pondionstracker
export PGHOST=localhost

GTFS=$1
SCHEMA=$2

cat sql/schema.sql | psql -b

gtfs-to-sql -u $GTFS/*.txt --schema $SCHEMA | sponge | psql -b

cat sql/shapes_summarized_populate.sql | psql -b
