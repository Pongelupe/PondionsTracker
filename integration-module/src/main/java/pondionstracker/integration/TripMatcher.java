package pondionstracker.integration;

import java.util.List;
import java.util.Map;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

public interface TripMatcher {

	Map<Trip, List<RealTimeTrip>> match(List<Trip> trips, List<RealTimeTrip> realTimeTrips);
	
}
