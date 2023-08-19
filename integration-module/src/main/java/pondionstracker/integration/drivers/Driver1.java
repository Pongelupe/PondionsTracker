package pondionstracker.integration.drivers;

import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.data.providers.GTFSService;
import pondionstracker.data.providers.RealTimeService;
import pondionstracker.integration.TripBusStopLinker;
import pondionstracker.integration.TripExtractor;
import pondionstracker.integration.TripMatcher;

@Slf4j
@RequiredArgsConstructor
public class Driver1 {
	
	private final GTFSService gtfsService;

	private final RealTimeService realTimeService;

	private final TripExtractor tripExtractor;

	private final TripMatcher tripMatcher;
	
	private final TripBusStopLinker tripBusStopLinker;
	
	public void integrate(String routeShortName, Date date) {
		var route = gtfsService.getRouteByRouteShortName(routeShortName, date).orElseThrow();
		var idsLines = realTimeService.getIdsLineByRouteId(route.getRouteId());
		log.info("Route %s has %d idsLines and %s trips at {}"
				.formatted(routeShortName, idsLines.size(), route.getTrips().size()), 
				date);
		
		var entries = realTimeService.getEntriesByIdLine(date, idsLines.toArray(new String[] {}));
		log.info("{} entries were retreived", 
				entries.values().stream().flatMap(List<RealTimeBusEntry>::stream).count());
		var realtimeTrips = entries.values().parallelStream()
			.map(tripExtractor::extract)
			.flatMap(List<RealTimeTrip>::stream)
			.sorted((o1, o2) -> o1.getDepartureTime().compareTo(o2.getDepartureTime()))
			.toList();
		log.info("Summarizing {} raw trips", realtimeTrips.size());
		
		var matchedScheadule = tripMatcher.match(route.getTrips(), realtimeTrips);
		var emptyScheadules = matchedScheadule.values().stream().filter(t -> !t.isEmpty()).count();
		log.info("%d/%d ({}%%) of the scheadule filled!".formatted(matchedScheadule.size(), emptyScheadules), 
				(double) emptyScheadules / matchedScheadule.size());
		route.getTrips().forEach(trip -> trip.setRealTimeTrips(matchedScheadule.get(trip)));
		
	}
	

	
}
