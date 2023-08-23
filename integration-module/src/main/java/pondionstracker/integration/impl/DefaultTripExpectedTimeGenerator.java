package pondionstracker.integration.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;
import pondionstracker.base.model.TripScheduleEntry;
import pondionstracker.integration.TripExpectedTimeGenerator;
import pondionstracker.utils.DateUtils;

@RequiredArgsConstructor
public class DefaultTripExpectedTimeGenerator implements TripExpectedTimeGenerator {

	@Override
	public List<TripScheduleEntry> generate(Trip trip, List<StopPointsInterval> stopsIntervals) {
		var tripSchedule = new ArrayList<TripScheduleEntry>();

		var departureTime = DateUtils.date2localtime(trip.getTripDepartureTime());
		var arrivalTime = DateUtils.date2localtime(trip.getTripArrivalTime());

		var routeDuration = ChronoUnit.SECONDS.between(departureTime, arrivalTime) / 60d;
		var avgSpeed = (trip.getLength() / 1000d) / (routeDuration / 60d);

		var intervals = stopsIntervals.stream()
				.collect(Collectors.toMap(StopPointsInterval::getStopSequence2, Function.identity()));

		tripSchedule.add(new TripScheduleEntry(trip.getBusStopsSequence().get(0), 
				DateUtils.dateFromLocalTime(trip.getTripDate(), departureTime)));
		
		var expectedTime = departureTime;

		for (int i = 1; i <= trip.getBusStopsSequence().size() - 1; i++) {
			var ponto = trip.getBusStopsSequence().get(i);

			if (intervals.containsKey(ponto.getStopSequence())) {
				var distanceBetweenStops = intervals.get(ponto.getStopSequence()).getLength();

				long expectedTimeDiff = BigDecimal.valueOf(((distanceBetweenStops * 100 / avgSpeed) * 60 * 60))
						.setScale(2, RoundingMode.DOWN).longValue();
				expectedTime = expectedTime.plusSeconds(expectedTimeDiff);
				
				tripSchedule.add(new TripScheduleEntry(ponto, DateUtils.dateFromLocalTime(trip.getTripDate(), expectedTime)));
			}

		}
		
		return tripSchedule;
	}

}
