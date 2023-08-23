package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;

public interface TripExpectedTimeGenerator {

	List<BusStopTrip> generate(Trip trip, List<StopPointsInterval> stopsIntervals);
	
}
