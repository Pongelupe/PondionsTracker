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
	
	
	public Route(String routeId, Date date, List<Trip> trips) {
		this.routeId = routeId;
		this.trips = trips;
		
		trips.forEach(t -> t.setTripDate(date));
	}
	
}
