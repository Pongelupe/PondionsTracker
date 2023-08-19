package pondionstracker.base.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.postgis.jdbc.geometry.LineString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trip {

	private String tripId;
	
	private String shapeId;
	
	private String tripHeadsign;

	private String serviceId;
	
	private Date tripDepartureTime;
	
	private Date tripArrivalTime;

	private Date tripDate;
	
	private double length;
	
	private LineString geom;
	
	private List<RealTimeTrip> realTimeTrips;
	
	@Builder.Default
	private List<BusStopTrip> busStopsSequence = new ArrayList<>();
	
	public Date getTripDepartureTime() {
		return Optional.ofNullable(tripDepartureTime).orElseGet(() -> getDateAtIndex(0));
	}
	
	public Date getTripArrivalTime() {
		return Optional.ofNullable(tripArrivalTime).orElseGet(() -> getDateAtIndex(busStopsSequence.size() - 1));
	}
	
	private Date getDateAtIndex(int index) {
		try {
			return busStopsSequence.get(index).getExpectedTime();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
