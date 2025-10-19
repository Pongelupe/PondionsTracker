package pondionstracker.data.providers.impl;

import static pondionstracker.utils.DateUtils.date2localdatetime;
import static pondionstracker.utils.DateUtils.localdatetime2Date;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Point;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.data.components.QueryExecutor;
import pondionstracker.data.constants.Query;
import pondionstracker.data.constants.Query.Parameter;
import pondionstracker.data.providers.RealTimeService;
import pondionstracker.utils.DateUtils;

@RequiredArgsConstructor
public class DefaultRealTimeService implements RealTimeService {
	
	protected final QueryExecutor queryExecutor;
	
	@Override
	public Map<String, List<RealTimeBusEntry>> getEntriesByDtEntryAndLineIds(Date date, String... idLines) {
		var startOfTheDay = localdatetime2Date(date2localdatetime(date).with(LocalTime.MIN));
		var endOfTheDay = localdatetime2Date(date2localdatetime(date).with(LocalTime.MAX));
		
		return getEntriesByDtEntryAndLineIds(startOfTheDay, endOfTheDay, idLines);
	}

	@Override
	public Map<String, List<RealTimeBusEntry>> getEntriesByDtEntryAndLineIds(Date startDate, Date endDate, String... idLines) {
		var idLinesList = List.of(idLines);
		return idLinesList.isEmpty() ? Map.of() : 
				queryExecutor.queryAll(Query.GET_ENTRIES, rs -> RealTimeBusEntry.builder()
				.dtEntry(rs.getTimestamp("dt_entry"))
				.coord((Point) ((PGgeometry) rs.getObject("coord")).getGeometry())
				.idVehicle(rs.getString("id_vehicle"))
				.idLine(rs.getString("id_line"))
				.currentDistanceTraveled(rs.getInt("current_distance_traveled"))
				.build(), Map.of(Parameter.DATE_START.name(), DateUtils.getSqlTimestamp(startDate),
						Parameter.DATE_END.name(), DateUtils.getSqlTimestamp(endDate),
						Parameter.LINE_ID.name(), idLinesList))
				.stream()
				.collect(Collectors.groupingBy(RealTimeBusEntry::getIdVehicle));
	}

	@Override
	public List<String> getIdsLineByRouteId(String routeId) {
		return List.of(routeId);
	}

}
