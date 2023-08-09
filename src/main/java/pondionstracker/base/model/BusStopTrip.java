package pondionstracker.base.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.postgis.jdbc.geometry.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusStopTrip {

	private int stopSequence;
	
	private String idStop;
	
	private Point coord;
	
	@Builder.Default
	private List<RealTimeBusEntry> entries =  new ArrayList<>();
	
}
