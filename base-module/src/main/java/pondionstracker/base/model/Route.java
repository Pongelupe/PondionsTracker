package pondionstracker.base.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Route {

	private String routeId;
	
	private List<Trip> trips;
	
	private List<RealTimeTrip> rawTrips;
	
	
	public Route(String routeId, Date date, List<Trip> trips) {
		this.routeId = routeId;
		this.trips = trips;
		
		trips.forEach(t -> t.setTripDate(date));
	}
	
	public List<Trip> getTrips() {
		return trips
				.stream()
				.sorted((o1, o2) 
						-> o1.getTripDepartureTime().compareTo(o2.getTripDepartureTime()))
				.toList();
	}
	
	
	public List<Trip> getTripSortedByTheLowerCalculatedEntriesRatio() {
		return trips
				.stream()
				.sorted((o1, o2) 
						-> Double.compare(o2.getCalculatedEntriesPercentage(), o1.getCalculatedEntriesPercentage()))
				.toList();
	}
	
	
}
