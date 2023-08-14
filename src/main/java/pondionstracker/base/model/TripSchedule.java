package pondionstracker.base.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripSchedule {

	private Trip trip;
	
	private List<TripScheduleEntry> expected;
	
	private List<TripScheduleEntry> real;
	
	public TripSchedule(Trip trip) {
		this.trip = trip;
		this.expected = new ArrayList<>();
	}
}
