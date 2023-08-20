package pondionstracker.integration.drivers;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Route;
import pondionstracker.data.providers.GTFSService;
import pondionstracker.data.providers.RealTimeService;
import pondionstracker.integration.TripBusStopLinker;
import pondionstracker.integration.TripExtractor;
import pondionstracker.integration.TripFilter;
import pondionstracker.integration.TripMatcher;
import pondionstracker.integration.TripMissingEntriesGenerator;
import pondionstracker.integration.impl.DefaultEntryMerger;
import pondionstracker.integration.impl.DefaultTripBusStopLinker;
import pondionstracker.integration.impl.DefaultTripExtractor;
import pondionstracker.integration.impl.DefaultTripFilter;
import pondionstracker.integration.impl.DefaultTripMatcher;
import pondionstracker.integration.impl.DefaultTripMissingEntriesGenerator;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class StaticRealTimeIntegrationDriver {
	
	private static final double DEFAULT_DISTANCE_THRESHOLD = 0.0005d; // 50 meters
	
	private static final int DEFAULT_MAX_TRIP_INITIAL_DELAY = 5; // 5 minutes
	
	private final GTFSService gtfsService;

	private final RealTimeService realTimeService;

	@Builder.Default
	private TripExtractor tripExtractor = new DefaultTripExtractor();

	@Builder.Default
	private TripMatcher tripMatcher = new DefaultTripMatcher(DEFAULT_MAX_TRIP_INITIAL_DELAY);
	
	@Builder.Default
	private TripBusStopLinker tripBusStopLinker = new DefaultTripBusStopLinker(DEFAULT_DISTANCE_THRESHOLD);

	@Builder.Default
	private TripMissingEntriesGenerator tripMissingEntriesGenerator = new DefaultTripMissingEntriesGenerator(new DefaultEntryMerger());
	
	@Builder.Default
	private TripFilter tripFilter = new DefaultTripFilter(); 
	
	public Route integrate(String routeShortName, Date date) {
		var route = gtfsService.getRouteByRouteShortName(routeShortName, date)
				.orElseThrow();
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
		
		route.getTrips()
			.stream()
			.parallel()
			.forEach(t -> {
				t.getRealTimeTrips().forEach(rtTrip -> {
					tripBusStopLinker.link(t, rtTrip);
					tripMissingEntriesGenerator.generateMissingEntries(t, rtTrip);
				});
				t.getRealTimeTrips().removeIf(rtTrip -> tripFilter.test(t, rtTrip));
			});
		
		return route;
		
	}
	
}
