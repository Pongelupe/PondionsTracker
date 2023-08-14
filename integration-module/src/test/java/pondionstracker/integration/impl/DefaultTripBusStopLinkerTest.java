package pondionstracker.integration.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

class DefaultTripBusStopLinkerTest {
	
	private static final double DISTANCE_THRESHOLD = 0.0005d; // 50m

	private DefaultTripBusStopLinker linker;
	
	private Trip trip; 
	private RealTimeTrip realTimeTrip;

	@BeforeEach
	void setUp() throws Exception {
		linker = new DefaultTripBusStopLinker(DISTANCE_THRESHOLD);
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
