package pondionstracker.base.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	private Date tripDate;
	
	private Date tripDepartureTime;
	
	private Date tripArrivalTime;
	
	private double length;
	
	private LineString geom;
	
	private RealTimeTrip realTimeTrip;
	
	@Builder.Default
	private List<BusStopTrip> busStopsSequence = new ArrayList<>();
}
