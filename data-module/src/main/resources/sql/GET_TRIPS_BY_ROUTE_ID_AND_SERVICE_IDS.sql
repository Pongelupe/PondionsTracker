SELECT t.trip_id, t.shape_id, trip_headsign, service_id, ST_FlipCoordinates(ss.shape_ls) as shape_ls,
ss.length FROM trips t
join shapes_summarized ss on ss.shape_id = t.shape_id 
WHERE route_id = :ROUTE_ID
AND service_id IN (:SERVCICE_IDS);