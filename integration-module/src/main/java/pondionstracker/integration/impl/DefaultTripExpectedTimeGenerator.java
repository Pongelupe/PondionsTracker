package pondionstracker.integration.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripExpectedTimeGenerator;
import pondionstracker.utils.DateUtils;

@RequiredArgsConstructor
public class DefaultTripExpectedTimeGenerator implements TripExpectedTimeGenerator {

	@Override
	public List<BusStopTrip> generate(Trip trip, List<StopPointsInterval> stopsIntervals) {
		var tripSchedule = new ArrayList<BusStopTrip>();

		var departureTime = DateUtils.date2localtime(trip.getTripDepartureTime());
		var arrivalTime = DateUtils.date2localtime(trip.getTripArrivalTime());

		var routeDuration = ChronoUnit.SECONDS.between(departureTime, arrivalTime) / 60d;
		var avgSpeed = (trip.getLength() / 1000d) / (routeDuration / 60d);

		var intervals = stopsIntervals.stream()
				.collect(Collectors.toMap(StopPointsInterval::getStopSequence2, Function.identity()));

		var firstBusStop = trip.getBusStopsSequence().get(0);
		firstBusStop.setExpectedTime(DateUtils.dateFromLocalTime(trip.getTripDate(), 
				departureTime));
		tripSchedule.add(firstBusStop);
		
		var expectedTime = departureTime;

		for (int i = 1; i <= trip.getBusStopsSequence().size() - 1; i++) {
			var ponto = trip.getBusStopsSequence().get(i);

			if (intervals.containsKey(ponto.getStopSequence())) {
				var distanceBetweenStops = intervals.get(ponto.getStopSequence()).getLength();

				long expectedTimeDiff = BigDecimal.valueOf(((distanceBetweenStops * 100 / avgSpeed) * 60 * 60))
						.setScale(2, RoundingMode.HALF_UP).longValue();
				expectedTime = expectedTime.plusSeconds(expectedTimeDiff);
				
				ponto.setExpectedTime(DateUtils.dateFromLocalTime(trip.getTripDate(), expectedTime));
				tripSchedule.add(ponto);
			}

		}
		
		return tripSchedule;
	}

}
