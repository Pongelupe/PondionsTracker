#!/bin/bash

export PGUSER=pondionstracker
export PGPASSWORD=pondionstracker
export PGDATABASE=pondionstracker

cat sql/schema.sql | psql -h localhost -b

gtfs-to-sql -u gtfs-bh/*.txt --schema pondionstracker | sponge | psql -h localhost -b

cat sql/shapes_geoms_populate.sql | psql -h localhost -b
