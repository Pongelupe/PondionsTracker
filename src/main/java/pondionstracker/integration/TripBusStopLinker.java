package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.Trip;

public interface TripBusStopLinker {

	void link(Trip trip, List<BusStopTrip> busStopsSequence);
	
}
