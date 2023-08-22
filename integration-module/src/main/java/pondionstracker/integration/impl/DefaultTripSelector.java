package pondionstracker.integration.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripSelector;

@RequiredArgsConstructor
public class DefaultTripSelector implements TripSelector {
	
	private final double tripMinPercentageTraveled;

	@Override
	public RealTimeTrip select(Trip trip, List<RealTimeTrip> realTimeCandidateTrips) {
		return realTimeCandidateTrips
				.stream()
				.filter(e -> e.getLastEntry().getDtEntry().after(e.getDepartureTime()))
				.filter(e -> (e.getCurrentDistanceTraveled() / trip.getLength()) >= tripMinPercentageTraveled)
				.max((e1, e2) -> e1.getDepartureTime().compareTo(e2.getDepartureTime()))
				.orElse(null);
	}


}
