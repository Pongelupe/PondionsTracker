with pontos (shape_id, stop_sequence, stop_id, ll) as (		
			select t.shape_id, st.stop_sequence, s.stop_id, 
			s.stop_loc::geometry as h
			from stop_times st 
			join stops s on s.stop_id = st.stop_id
			join trips t  on st.trip_id = t.trip_id
			where t.trip_id = :TRIP_ID
			)
select p1.stop_sequence  as stop_sequence_1, p2.stop_sequence as stop_sequence2,
ST_Length(ST_LineSubstring(sg.shape_ls , ST_LineLocatePoint(sg.shape_ls, p1.ll), ST_LineLocatePoint(sg.shape_ls, p2.ll)))
from pontos p2
join shapes_summarized sg  on sg.shape_id = p2.shape_id
join pontos p1 on p1.stop_sequence = p2.stop_sequence -1
where p1.stop_sequence > 0
and ST_LineLocatePoint(sg.shape_ls, p1.ll) < ST_LineLocatePoint(sg.shape_ls, p2.ll)
order by 1;