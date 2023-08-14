package pondionstracker.base.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RealTimeTrip {

	private RealTimeBusEntry departure;

	private RealTimeBusEntry arrival;

	private List<RealTimeBusEntry> entries;
	
	
	public RealTimeTrip() {
		this.entries = new ArrayList<>();
	}
	
	
	public String getIdVehicle() {
		return departure.getIdVehicle();
	}
	
	public String getIdLine() {
		return departure.getIdLine();
	}
	
	public boolean hasTripFinished() {
		return arrival != null;
	}
	
	public Date getDepatureTime() {
		return departure.getDtEntry();
	}
	
	public Date getArrivalTime() {
		return hasTripFinished() ? arrival.getDtEntry() : null;
	}
	
	public int getCurrentDistanceTraveled() {
		return getLastEntry().getCurrentDistanceTraveled();
	}
	
	private RealTimeBusEntry getLastEntry() {
		return Optional.ofNullable(arrival)
				.orElseGet(() -> entries.get(entries.size() -1));
	}

}
