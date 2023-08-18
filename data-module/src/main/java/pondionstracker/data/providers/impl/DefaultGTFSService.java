package pondionstracker.data.providers.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.Route;
import pondionstracker.base.model.Trip;
import pondionstracker.data.components.QueryExecutor;
import pondionstracker.data.constants.Query;
import pondionstracker.data.constants.Query.Parameter;
import pondionstracker.data.providers.GTFSService;
import pondionstracker.dto.DayOfWeekServiceDTO;
import pondionstracker.utils.DateUtils;

@RequiredArgsConstructor
public class DefaultGTFSService implements GTFSService {
	
	private record RouteTripRecord(String routeId, List<String> serviceIds) {}

	private final QueryExecutor queryExecutor;

	@Override
	public Optional<Route> getRouteByRouteShortName(String routeShortName, Date date) {
		return Optional.ofNullable(queryExecutor.queryFirst(Query.GET_ROUTE_BY_ROUTE_SHORT_NAME, 
				rs -> rs.getString(1), Map.of(Parameter.ROUTE_SHORT_NAME, routeShortName)))
				.map(routeId -> new RouteTripRecord(routeId, getServiceId(date)))
				.map(routeTripRecord -> new Route(routeTripRecord.routeId, getTripsByRouteIdAndServiceIds(routeTripRecord)))
		;
	}

	public List<String> getServiceId(Date date) {
		var dayOfWeek = DateUtils.getDayOfWeekFromDate(date)
				.name().toLowerCase();
		
		return queryExecutor.queryAll(Query.GET_CALENDAR_BY_DATE, rs -> 
			new DayOfWeekServiceDTO(rs.getString("service_id"),
				rs.getString(dayOfWeek)), 
			Map.of(Parameter.DATE, DateUtils.getSqlDate(date)))
			.stream()
			.filter(dow -> "available".equals(dow.getDayOfWeek()))
			.map(DayOfWeekServiceDTO::getServiceId)
			.toList();
	}

	public List<Trip> getTripsByRouteIdAndServiceIds(String routeId, List<String> serviceIds) {
		return queryExecutor.queryAll(Query.GET_TRIPS_BY_ROUTE_ID, rs -> Trip.builder()
				.tripId(rs.getString("trip_id"))
				.shapeId(rs.getString("shape_id"))
				.serviceId(rs.getString("service_id"))
				.tripHeadsign(rs.getString("trip_headsign"))
				.build(), Map.of(Parameter.ROUTE_ID, routeId,
						Parameter.SERVCICE_IDS, serviceIds));
	}
	
	private List<Trip> getTripsByRouteIdAndServiceIds(RouteTripRecord r) {
		return getTripsByRouteIdAndServiceIds(r.routeId, r.serviceIds);
	}

}
