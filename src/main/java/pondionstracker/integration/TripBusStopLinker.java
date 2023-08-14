package pondionstracker.integration;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

public interface TripBusStopLinker {

	void link(Trip trip, RealTimeTrip realTimeTrip);
	
}
