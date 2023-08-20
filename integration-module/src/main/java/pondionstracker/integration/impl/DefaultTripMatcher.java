package pondionstracker.integration.impl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripMatcher;
import pondionstracker.utils.DateUtils;

@RequiredArgsConstructor
public class DefaultTripMatcher implements TripMatcher {

	private final int maxTripInitialDelay;
	
	@Override
	public Map<Trip, List<RealTimeTrip>> match(List<Trip> trips, List<RealTimeTrip> realTimeTrips) {
		var matchedScheadule = new HashMap<Trip, List<RealTimeTrip>>();
		int currentRealTimeTripIndex = 0;		
		
		for (Trip trip : trips) {
			var tripCanditates = new ArrayList<RealTimeTrip>();
			var tripDepartureTime = DateUtils.date2localtime(trip.getTripDepartureTime());
			
			for (int i = currentRealTimeTripIndex; i < realTimeTrips.size(); i++) {
				var currentRealTimeTrip = realTimeTrips.get(i);
				var departureTime = DateUtils.date2localtime(currentRealTimeTrip.getDepartureTime());
				
				long diff = ChronoUnit.MINUTES.between(tripDepartureTime, departureTime);
				
				if (Math.abs(diff) <= maxTripInitialDelay) {
					currentRealTimeTripIndex = i + 1;
					tripCanditates.add(currentRealTimeTrip);
				}
			}
			
			matchedScheadule.put(trip, tripCanditates);
		}
		
		
		return matchedScheadule;
	}

}
