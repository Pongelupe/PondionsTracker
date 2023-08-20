package pondionstracker.integration.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

class DefaultTripMatcherTest {
	
	private static final int DELAY = 5;
	
	private DefaultTripMatcher matcher;
	
	private List<Trip> trips; 
	private List<RealTimeTrip> realTimeTrips;
	
	private SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");

	@BeforeEach
	void setUp() throws Exception {
		matcher = new DefaultTripMatcher(DELAY);
		
		Function<String, Trip> tripSupplier = d -> Trip.builder()
				.tripDepartureTime(parseDate(d))
				.build();
		Function<String, RealTimeTrip> rtTripSupplier = d -> RealTimeTrip.builder()
				.departure(RealTimeBusEntry.builder().dtEntry(parseDate(d)).build())
				.build();
		
		trips = List.of(tripSupplier.apply("07:00:00"),
				tripSupplier.apply("07:10:00"),
				tripSupplier.apply("07:20:00"),
				tripSupplier.apply("07:30:00"),
				tripSupplier.apply("07:40:00"),
				tripSupplier.apply("07:50:00"));
		
		realTimeTrips = List.of(rtTripSupplier.apply("06:57:18"),
				rtTripSupplier.apply("07:06:00"),
				rtTripSupplier.apply("07:07:00"),
				rtTripSupplier.apply("07:22:00"),
				rtTripSupplier.apply("07:27:00"),
				rtTripSupplier.apply("07:50:00"))
				;
	}

	@Test
	void testMatch() {
		var matchedScheadule = matcher.match(trips, realTimeTrips);
		
		assertEquals(6, matchedScheadule.size());
		for (Trip t : trips) {
			assertTrue(matchedScheadule.containsKey(t));
		}
	
		var entry = matchedScheadule.get(trips.get(0));
		assertEquals(1, entry.size());
		assertEquals("06:57:18", f.format(entry.get(0).getDepartureTime()));
		
		entry = matchedScheadule.get(trips.get(1));
		assertEquals(2, entry.size());
		assertEquals("07:06:00", f.format(entry.get(0).getDepartureTime()));
		assertEquals("07:07:00", f.format(entry.get(1).getDepartureTime()));
		
		entry = matchedScheadule.get(trips.get(2));
		assertEquals(1, entry.size());
		assertEquals("07:22:00", f.format(entry.get(0).getDepartureTime()));
		
		entry = matchedScheadule.get(trips.get(3));
		assertEquals(1, entry.size());
		assertEquals("07:27:00", f.format(entry.get(0).getDepartureTime()));
		
		entry = matchedScheadule.get(trips.get(4));
		assertTrue(entry.isEmpty());
		
		entry = matchedScheadule.get(trips.get(5));
		assertEquals(1, entry.size());
		assertEquals("07:50:00", f.format(entry.get(0).getDepartureTime()));
		
	}
	
	@SneakyThrows
	private Date parseDate(String d) {
		return f.parse(d);
	}

}
