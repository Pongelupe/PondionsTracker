package pondionstracker.integration.impl;

import java.util.ArrayList;
import java.util.List;

import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.integration.TripExtractor;

/**
 * 
 * @author ppongelupe
 *
 */
public class DefaultTripExtractor implements TripExtractor {

	@Override
	public List<RealTimeTrip> extract(List<RealTimeBusEntry> entries) {
		var trips = new ArrayList<RealTimeTrip>();
		var trip = new RealTimeTrip();
		
		int currentDistanceTraveled = 0;
		RealTimeBusEntry departure = null;
		RealTimeBusEntry arrival = null;
		RealTimeBusEntry previousEntry = null;
		
		for (RealTimeBusEntry entry : entries) {
			
			if (entry.getCurrentDistanceTraveled() >= currentDistanceTraveled) {
				
				if (arrival != null) {
					// a new trip has just started
					trip.setDeparture(departure);
					trip.setArrival(arrival);
					trips.add(trip);
					
					
					trip = new RealTimeTrip();
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
				// the trip has finished
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
