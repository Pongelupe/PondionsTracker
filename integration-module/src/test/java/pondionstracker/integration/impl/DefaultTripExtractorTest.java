package pondionstracker.integration.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.IntFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pondionstracker.base.model.RealTimeBusEntry;

/**
 * 
 * @author ppongelupe
 *
 */
class DefaultTripExtractorTest {
	
	private DefaultTripExtractor tripExtractor;
	
	private List<RealTimeBusEntry> entries;

	@BeforeEach
	void setUp() throws Exception {
		tripExtractor = new DefaultTripExtractor();
		
		IntFunction<RealTimeBusEntry> entryProducer = currentDistanceTraveled -> RealTimeBusEntry
				.builder()
				.currentDistanceTraveled(currentDistanceTraveled)
				.build();
		
		entries = List.of(entryProducer.apply(0), entryProducer.apply(200), entryProducer.apply(220),
				entryProducer.apply(50), entryProducer.apply(200), entryProducer.apply(290), entryProducer.apply(590),
				entryProducer.apply(0), entryProducer.apply(0), entryProducer.apply(20), entryProducer.apply(290),
				entryProducer.apply(420));
	}

	@Test
	void testExtract() {
		var trips = tripExtractor.extract(entries);
		
		assertEquals(3, trips.size());
		
		var t1 = trips.get(0);
		assertEquals(0, t1.getDeparture().getCurrentDistanceTraveled());
		assertEquals(220, t1.getArrival().getCurrentDistanceTraveled());
		assertEquals(3, t1.getEntries().size());

		var t2 = trips.get(1);
		assertEquals(50, t2.getDeparture().getCurrentDistanceTraveled());
		assertEquals(590, t2.getArrival().getCurrentDistanceTraveled());
		assertEquals(4, t2.getEntries().size());
		
		var t3 = trips.get(2);
		assertEquals(0, t3.getDeparture().getCurrentDistanceTraveled());
		assertEquals(420, t3.getArrival().getCurrentDistanceTraveled());
		assertEquals(5, t3.getEntries().size());
	}

}
