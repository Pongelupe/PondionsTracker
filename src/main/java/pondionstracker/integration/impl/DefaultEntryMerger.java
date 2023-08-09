package pondionstracker.integration.impl;

import java.util.Date;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongBiFunction;

import net.postgis.jdbc.geometry.Point;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.integration.EntryMerger;
/**
 * 
 * @author ppongelupe
 *
 */
public class DefaultEntryMerger implements EntryMerger {

	/**
	 * The key ideia is to merge two given entries from a same Euclidean Plane
	 */
	public RealTimeBusEntry merge(RealTimeBusEntry e1, RealTimeBusEntry e2) {
		IntBinaryOperator avg = (o1, o2) -> (o1 + o2) / 2;

		ToDoubleFunction<RealTimeBusEntry> getXCoord = o1 -> o1.getCoord().getX();
		ToDoubleFunction<RealTimeBusEntry> getYCoord = o1 -> o1.getCoord().getY();
		DoubleBinaryOperator avgDouble = (o1, o2) -> (o1 + o2) / 2;
		ToLongBiFunction<Date, Date> avgDate = (o1, o2) -> (o1.getTime() + o2.getTime()) / 2;
		
		var point = new Point(avgDouble.applyAsDouble(getXCoord.applyAsDouble(e1), getXCoord.applyAsDouble(e2)), 
				avgDouble.applyAsDouble(getYCoord.applyAsDouble(e1), getYCoord.applyAsDouble(e2)),
				e2.getCoord().getZ());
		point.dimension = e2.getCoord().dimension;
		
		return RealTimeBusEntry.builder()
				.currentDistanceTraveled(avg.applyAsInt(e1.getCurrentDistanceTraveled(), 
									e2.getCurrentDistanceTraveled()))
				.dtEntry(new Date(avgDate.applyAsLong(e1.getDtEntry(), e2.getDtEntry())))
				.coord(point)
				.idVehicle(e2.getIdVehicle())
				.idLine(e2.getIdLine())
				.build();
	}

}
