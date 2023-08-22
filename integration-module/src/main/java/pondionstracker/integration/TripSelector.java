package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

public interface TripSelector {

	RealTimeTrip select(Trip trip, List<RealTimeTrip> realTimeCandidateTrips);
	
}
