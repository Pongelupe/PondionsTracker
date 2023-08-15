package pondionstracker.data.providers.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.Route;
import pondionstracker.base.model.Trip;
import pondionstracker.data.components.Loadfile;
import pondionstracker.data.components.QueryExecutor;
import pondionstracker.data.constants.Query;
import pondionstracker.data.providers.GTFSService;

@RequiredArgsConstructor
public class DefaultGTFSService implements GTFSService {

	private final QueryExecutor queryExecutor;

	private final Loadfile loadFile;

	@Override
	public Optional<Route> getRouteByRouteShortName(String routeShortName) {
		var query = loadFile.loadQuery(Query.GET_ROUTE_BY_ROUTE_SHORT_NAME);
		return Optional.ofNullable(queryExecutor.queryFirst(query, rs -> rs.getString(1), routeShortName))
				.map(routeId -> new Route(routeId, getTripsByRouteId(routeId)))
		;
	}

	private List<Trip> getTripsByRouteId(String routeId) {
		var query = loadFile.loadQuery(Query.GET_TRIPS_BY_ROUTE_ID);
		
		queryExecutor.queryAll(routeId, rs -> Trip.builder()
				.tripId(rs.getString(1))
				.build());
		
		return null;
	}

}
