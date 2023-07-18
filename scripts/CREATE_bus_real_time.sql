-- bh.real_time_bus definition

-- Drop table

-- DROP TABLE bh.real_time_bus;

CREATE TABLE bh.real_time_bus (
	id serial NOT NULL,
	dt_entry timestamp NOT NULL,
	coord public.geometry NOT NULL,
	id_vehicle int4 NOT NULL,
	id_line int4 NOT NULL,
	current_distance_traveled int4 NOT NULL,
);
CREATE INDEX real_time_bus_dt_entry_idx ON bh.real_time_bus USING btree (dt_entry);
CREATE INDEX real_time_bus_id_vehicle_idx ON bh.real_time_bus USING btree (id_vehicle, dt_entry);
