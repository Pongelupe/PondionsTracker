package pondionstracker.base.model;


import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Trip {

	private RealTimeBus departure;
	
	private RealTimeBus arrival;
	
	private List<RealTimeBus> entries;
	
	public Trip() {
		this.entries = new ArrayList<>();
	}
	
}
