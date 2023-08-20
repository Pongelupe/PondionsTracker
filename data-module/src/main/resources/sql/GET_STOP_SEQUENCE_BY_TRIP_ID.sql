SELECT st.stop_sequence, s.stop_id, arrival_time,ST_FlipCoordinates(s.stop_loc::geometry) as stop_loc FROM stop_times st 
join stops s on s.stop_id  = st.stop_id 
where trip_id in (:TRIP_ID)