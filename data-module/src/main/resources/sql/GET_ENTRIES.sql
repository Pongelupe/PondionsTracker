SELECT DISTINCT dt_entry, coord, id_vehicle, id_line, current_distance_traveled
FROM REAL_TIME_BUS
WHERE id_line = ?
AND dt_entry between ? AND ?