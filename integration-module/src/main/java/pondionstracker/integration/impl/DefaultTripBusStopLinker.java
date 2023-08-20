package pondionstracker.integration.impl;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripBusStopLinker;

@RequiredArgsConstructor
public class DefaultTripBusStopLinker implements TripBusStopLinker {

	private final double distanceThreshold;

	@Override
	public void link(Trip trip, RealTimeTrip realTimeTrip) {
		var busStopSequence = trip.getBusStopsSequence();
		var currentEntryIndex = 0;
		
		var distanceTraveledTrip = realTimeTrip.getCurrentDistanceTraveled();

		for (var s = 0; s < busStopSequence.size(); s++) {
			var currentStop = busStopSequence.get(s);

			//menorMaiorDistancia
			var smallestBiggerDistance = s > 0
					? busStopSequence.get(s - 1).getCoord().distance(currentStop.getCoord())
					: 0d;
			var searching = true;
			var currentStopDirection = busStopSequence.size() / 2 >= currentStop.getStopSequence();

			var previousCurrentEntryIndex = currentEntryIndex;

			for (var i = currentEntryIndex; i < realTimeTrip.getEntries().size() - 1; i++) {
				var entry = realTimeTrip.getEntries().get(i);
				var distance = entry.getCoord().distance(currentStop.getCoord());

				var entryDirection = distanceTraveledTrip / 2 >= entry.getCurrentDistanceTraveled();

				if (distance <= distanceThreshold
						&& ((currentStopDirection == entryDirection) || (currentStop.getEntries().isEmpty() && !currentStopDirection))) {
					entry.setIndex(i);
					currentStop.getEntries().add(entry);
					currentStop.setDistance(distance);
					currentEntryIndex = i;
					smallestBiggerDistance = distance;
				} else if (!currentStop.getEntries().isEmpty()) {
					currentEntryIndex = i > 0 ? i - 1 : 0;
					break;
				} else if (smallestBiggerDistance < distance && searching) {
					smallestBiggerDistance = distance;
					currentEntryIndex++;
				} else if (smallestBiggerDistance > distance) {
					searching = false;
				}

			}

			if (currentStop.getEntries().isEmpty()) {
				currentEntryIndex = previousCurrentEntryIndex;
			}
		}
	}

}
