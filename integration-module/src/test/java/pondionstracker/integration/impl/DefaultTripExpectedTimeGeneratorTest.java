package pondionstracker.integration.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;

/**
 * 
 * @author ppongelupe
 *
 */
class DefaultTripExpectedTimeGeneratorTest {
	
	private DefaultTripExpectedTimeGenerator generator;
	
	private Trip trip;
	private List<StopPointsInterval> stopsIntervals;
	
	private SimpleDateFormat f;

	@BeforeEach
	void setUp() throws Exception {
		generator = new DefaultTripExpectedTimeGenerator();
		
		f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		
		trip = Trip.builder()
				.tripDate(f.parse("11/08/2023 10:00:00"))
				.tripDepartureTime(f.parse("11/08/2023 10:00:00"))
				.tripArrivalTime(f.parse("11/08/2023 10:15:00"))
				.length(3600)
				.busStopsSequence(new ArrayList<>())
				.build();
		stopsIntervals = List.of(new StopPointsInterval(1, 2, 0.005),
				new StopPointsInterval(2, 3, 0.009),
				new StopPointsInterval(3, 4, 0.004),
				new StopPointsInterval(4, 5, 0.012),
				new StopPointsInterval(5, 6, 0.006)
				);
		
		for (int i = 0; i <= stopsIntervals.size(); i++) {
			trip.getBusStopsSequence()
				.add(BusStopTrip.builder()
					.stopSequence(i + 1)
					.build());
		}
		
		
	}

	@Test
	void test() {
		var schedule = generator.generate(trip, stopsIntervals);
		
		assertEquals(trip.getBusStopsSequence().size(), schedule.size());
		
		assertEquals("11/08/2023 10:00:00", f.format(schedule.get(0).getExpectedTime()));
		assertEquals("11/08/2023 10:02:05", f.format(schedule.get(1).getExpectedTime()));
		assertEquals("11/08/2023 10:05:50", f.format(schedule.get(2).getExpectedTime()));
		assertEquals("11/08/2023 10:07:30", f.format(schedule.get(3).getExpectedTime()));
		assertEquals("11/08/2023 10:12:30", f.format(schedule.get(4).getExpectedTime()));
		assertEquals("11/08/2023 10:15:00", f.format(schedule.get(5).getExpectedTime()));
	}

}
