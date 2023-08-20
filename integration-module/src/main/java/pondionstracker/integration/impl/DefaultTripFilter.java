package pondionstracker.integration.impl;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripFilter;

public class DefaultTripFilter implements TripFilter {

	@Override
	public boolean test(Trip trip, RealTimeTrip rtTrip) {
		return false;
	}


}
