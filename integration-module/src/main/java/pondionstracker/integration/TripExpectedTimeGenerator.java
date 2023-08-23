package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;
import pondionstracker.base.model.TripScheduleEntry;

public interface TripExpectedTimeGenerator {

	List<TripScheduleEntry> generate(Trip trip, List<StopPointsInterval> stopsIntervals);
	
}
