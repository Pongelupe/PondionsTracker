package pondionstracker.base.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripScheduleEntry {

	private BusStopTrip stop;
	
	private Date date;
	
}
