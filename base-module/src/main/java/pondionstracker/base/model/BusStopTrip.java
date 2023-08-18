package pondionstracker.base.model;

import java.util.ArrayList;
import java.util.Date;
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
	
	private Date expectedTime;
	
	private Point coord;
	
	@Builder.Default
	private List<RealTimeBusEntry> entries = new ArrayList<>();
	
	private boolean calculated;
	
	private Double distance;
	
	public BusStopTrip(BusStopTrip ponto) {
		this.stopSequence = ponto.getStopSequence();
		this.idStop = ponto.getIdStop();
		this.expectedTime = ponto.getExpectedTime();
		this.coord = ponto.getCoord();
		this.entries = new ArrayList<>(ponto.getEntries());
		this.calculated = ponto.calculated;
		this.distance = ponto.getDistance();
	}
	
}
