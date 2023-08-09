package pondionstracker.integration.impl;

import java.util.ArrayList;
import java.util.List;

import pondionstracker.base.model.RealTimeBus;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripExtractor;

/**
 * 
 * @author ppongelupe
 *
 */
public class DefaultTripExtractor implements TripExtractor {

	@Override
	public List<Trip> extract(List<RealTimeBus> entries) {
		var trips = new ArrayList<Trip>();
		var trip = new Trip();
		
		int currentDistanceTraveled = 0;
		RealTimeBus departure = null;
		RealTimeBus arrival = null;
		RealTimeBus previousEntry = null;
		
		for (RealTimeBus entry : entries) {
			
			if (entry.getCurrentDistanceTraveled() >= currentDistanceTraveled) {
				
				if (arrival != null) {
					// a new trip has just started
					trip.setDeparture(departure);
					trip.setArrival(arrival);
					trips.add(trip);
					
					
					trip = new Trip();
					currentDistanceTraveled = previousEntry.getCurrentDistanceTraveled();
					departure = previousEntry;
					trip.getEntries().add(previousEntry);
					arrival = null;
				} else {
					currentDistanceTraveled = entry.getCurrentDistanceTraveled();
				}
				
				if (departure == null || 
						entry.getCurrentDistanceTraveled() == departure.getCurrentDistanceTraveled()) {
					departure = entry;
				}
				
				trip.getEntries().add(entry);
				
			} else {
				// the trip has ended
				arrival = previousEntry;
				currentDistanceTraveled = entry.getCurrentDistanceTraveled();
			}
			
			previousEntry = entry;
		}
		
		trip.setDeparture(departure);
		trip.setArrival(previousEntry);
		trips.add(trip);
		
		
		return trips;
	}

}
