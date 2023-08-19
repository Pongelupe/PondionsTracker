SELECT DISTINCT dt_entry, coord, id_vehicle, id_line, current_distance_traveled
FROM real_time_bus
WHERE id_line in (:LINE_ID)
AND dt_entry between :DATE_START AND :DATE_END
order by 1