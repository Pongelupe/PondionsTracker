package pondionstracker.integration.drivers;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
import pondionstracker.integration.TripExpectedTimeGenerator;
import pondionstracker.integration.TripExtractor;
import pondionstracker.integration.TripMatcher;
import pondionstracker.integration.TripMissingEntriesGenerator;
import pondionstracker.integration.TripSelector;
import pondionstracker.integration.impl.DefaultEntryMerger;
import pondionstracker.integration.impl.DefaultTripBusStopLinker;
import pondionstracker.integration.impl.DefaultTripExpectedTimeGenerator;
import pondionstracker.integration.impl.DefaultTripExtractor;
import pondionstracker.integration.impl.DefaultTripMatcher;
import pondionstracker.integration.impl.DefaultTripMissingEntriesGenerator;
import pondionstracker.integration.impl.DefaultTripSelector;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class IntegrationDriver {
	
	private static final int MIN_REQUIRED_EXPECTED_TIME_GTFS = 2;

	private static final int DEFAULT_MAX_TRIP_INITIAL_DELAY = 5; // 5 minutes
	
	private static final double DEFAULT_DISTANCE_THRESHOLD = 0.0005d; // 50 meters

	private static final double DEFAULT_TRIP_MIN_PERCENTAGE_TRAVELED = 0.85; // 85%
	
	private final GTFSService gtfsService;

	private final RealTimeService realTimeService;

	@Builder.Default
	private TripExpectedTimeGenerator tripExpectedTimeGenerator = new DefaultTripExpectedTimeGenerator();
	
	@Builder.Default
	private TripExtractor tripExtractor = new DefaultTripExtractor();

	@Builder.Default
	private TripMatcher tripMatcher = new DefaultTripMatcher(DEFAULT_MAX_TRIP_INITIAL_DELAY);
	
	@Builder.Default
	private TripBusStopLinker tripBusStopLinker = new DefaultTripBusStopLinker(DEFAULT_DISTANCE_THRESHOLD);

	@Builder.Default
	private TripMissingEntriesGenerator tripMissingEntriesGenerator = new DefaultTripMissingEntriesGenerator(new DefaultEntryMerger());
	
	@Builder.Default
	private TripSelector tripSelector = new DefaultTripSelector(DEFAULT_TRIP_MIN_PERCENTAGE_TRAVELED); 
	
	public Route integrate(String routeShortName, Date date) {
		var route = gtfsService.getRouteByRouteShortName(routeShortName, date)
				.orElseThrow();
		if (hasToGenerateExpectedTime(route)) {
			 route.getTrips()
			 	.forEach(trip -> {
			 		var stopsIntervals = gtfsService.getStopPointsInterval(trip.getTripId());
			 		tripExpectedTimeGenerator.generate(trip, stopsIntervals);
			 	});
		}
		
		
		var idsLines = realTimeService.getIdsLineByRouteId(route.getRouteId());
		log.info("Route %s has %d idsLines and %s trips at {}"
				.formatted(routeShortName, idsLines.size(), route.getTrips().size()), 
				date);
		var entries = realTimeService.getEntriesByDtEntryAndLineIds(date, idsLines.toArray(new String[] {}));
		log.info("{} entries were retreived", 
				entries.values().stream().flatMap(List<RealTimeBusEntry>::stream).count());
		var realtimeTrips = entries.values()
				.parallelStream()
				.map(tripExtractor::extract)
				.flatMap(List<RealTimeTrip>::stream)
				.sorted((o1, o2) -> o1.getDepartureTime().compareTo(o2.getDepartureTime()))
				.toList();
		route.setRawTrips(realtimeTrips);
		log.info("Summarizing {} raw trips", realtimeTrips.size());
		
		var matchedSchedule = tripMatcher.match(route.getTrips(), realtimeTrips);
		var emptySchedules = matchedSchedule.values().stream().filter(t -> !t.isEmpty()).count();
		log.info("%d/%d ({}%%) of the schedule filled!".formatted(emptySchedules, matchedSchedule.size()), 
				(double) emptySchedules / matchedSchedule.size());
		
		var trips = route.getTrips();
		trips.forEach(trip -> trip.setRealTimeTrip(tripSelector.select(trip, matchedSchedule.get(trip))));
		
		trips
			.stream()
			.filter(t -> t.getRealTimeTrip() != null)
			.parallel()
			.forEach(t -> {
				tripBusStopLinker.link(t, t.getRealTimeTrip());
				tripMissingEntriesGenerator.generateMissingEntries(t, t.getRealTimeTrip());
			});
		
		var notEmptyScheadules = trips.stream()
				.filter(d -> d.getRealTimeTrip() != null).count();
		log.info("Summarizing {} valid trips", notEmptyScheadules);
		log.info("%d/%d ({}%%) of the schedule filled!".formatted(notEmptyScheadules, trips.size()), 
				(double) notEmptyScheadules / trips.size());
		
		return route;
		
	}

	private boolean hasToGenerateExpectedTime(Route route) {
		return route.getTrips()
			.stream()
			.anyMatch(t -> t.getBusStopsSequence()
					.stream()
					.map(e -> e.getExpectedTime())
					.filter(Objects::nonNull)
					.count() < MIN_REQUIRED_EXPECTED_TIME_GTFS
					);
	}
	
}
