package pondionstracker.base.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.postgis.jdbc.geometry.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RealTimeBus {

	private String id;
	
	private Date dtEntry;
	
	private Point coord;
	
	private String idVehicle;
	
	private String idLine;
	
	private int currentDistanceTraveled;
	
}
