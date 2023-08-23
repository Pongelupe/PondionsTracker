package pondionstracker.base.model;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.postgis.jdbc.geometry.Point;
import pondionstracker.utils.DateUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusStopTrip {
	
	private int stopSequence;
	
	private String idStop;
	
	private Date expectedTime;
	
	private Point coord;
	
	private List<RealTimeBusEntry> entries;
	
	private boolean calculated;
	
	public Date getRealTime() {
		return Optional.ofNullable(entries)
				.map(e ->  e.get(entries.size() - 1).getDtEntry())
				.orElse(null);
	}
	
	
	public Long getExpectedRealTimeDiffInMinutes() {
		BiFunction<Date, Date, Long> calcDiff = (ex, re) -> ChronoUnit.MINUTES.between(
				DateUtils.date2localtime(ex), DateUtils.date2localtime(re));
		
		var realTime = getRealTime();
		
		var dates = Arrays.asList(expectedTime, realTime)
				.stream()
				.filter(Objects::nonNull)
				.toList();
		return dates.size() != 2 ? null : calcDiff.apply(realTime, expectedTime);
	}
	
	public BusStatusAtStop getStatus() {
		return BusStatusAtStop.ofDiffTimestamp(getExpectedRealTimeDiffInMinutes());
	}
	
}
