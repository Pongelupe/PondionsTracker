package pondionstracker.data.providers.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.Point;
import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.Route;
import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;
import pondionstracker.data.components.QueryExecutor;
import pondionstracker.data.constants.Query;
import pondionstracker.data.constants.Query.Parameter;
import pondionstracker.data.providers.GTFSService;
import pondionstracker.dto.DayOfWeekServiceDTO;
import pondionstracker.utils.DateUtils;

@RequiredArgsConstructor
public class DefaultGTFSService implements GTFSService {
	
	protected record RouteTripRecord(String routeId, List<String> serviceIds) {}
	
	protected final QueryExecutor queryExecutor;

	@Override
	public Optional<Route> getRouteByRouteShortName(String routeShortName, Date date) {
		return Optional.ofNullable(queryExecutor.queryFirst(Query.GET_ROUTE_BY_ROUTE_SHORT_NAME, 
				rs -> rs.getString(1), Map.of(Parameter.ROUTE_SHORT_NAME, routeShortName)))
				.map(routeId -> new RouteTripRecord(routeId, getServiceId(date)))
				.map(routeTripRecord -> new Route(routeTripRecord.routeId, date,
						getTripsByRouteIdAndServiceIds(routeTripRecord)))
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
		return queryExecutor.queryAll(Query.GET_TRIPS_BY_ROUTE_ID_AND_SERVICE_IDS, rs -> Trip.builder()
				.tripId(rs.getString("trip_id"))
				.shapeId(rs.getString("shape_id"))
				.serviceId(rs.getString("service_id"))
				.tripHeadsign(rs.getString("trip_headsign"))
				.geom((LineString) ((PGgeometry) rs.getObject("shape_ls")).getGeometry())
				.length(rs.getDouble("length"))
				.build(), Map.of(Parameter.ROUTE_ID, routeId,
						Parameter.SERVCICE_IDS, serviceIds))
				.stream()
				.parallel()
				.map(trip -> {
					var stops = queryExecutor.queryAll(Query.GET_STOP_SEQUENCE_BY_TRIP_ID, rs -> 
					BusStopTrip.builder()
					.stopSequence(rs.getInt("stop_sequence"))
					.idStop(rs.getString("stop_id"))
					.expectedTime(rs.getTime("arrival_time"))
					.coord((Point) ((PGgeometry) rs.getObject("stop_loc")).getGeometry())
					.build()
					, Map.of(Parameter.TRIP_ID, trip.getTripId()));
					
					trip.setBusStopsSequence(stops);
					
					return trip;
				})
				.sequential()
				.sorted((o1, o2) -> o1.getTripDepartureTime().compareTo(o2.getTripDepartureTime()))
				.toList();
	}
	
	public List<StopPointsInterval> getStopPointsInterval(String tripId) {
		return queryExecutor.queryAll(Query.GET_STOP_TIME_INVERVAL_BY_TRIP_ID, 
				rs -> StopPointsInterval.builder()
			.stopSequence1(rs.getInt(1))
			.stopSequence2(rs.getInt(2))
			.length(rs.getDouble(3))
		.build(), Map.of(Parameter.TRIP_ID, tripId));
	}
	
	private List<Trip> getTripsByRouteIdAndServiceIds(RouteTripRecord r) {
		return getTripsByRouteIdAndServiceIds(r.routeId, r.serviceIds);
	}

}
