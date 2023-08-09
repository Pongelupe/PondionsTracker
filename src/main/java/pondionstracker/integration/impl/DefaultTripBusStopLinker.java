package pondionstracker.integration.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.TripBusStopLinker;

@RequiredArgsConstructor
public class DefaultTripBusStopLinker implements TripBusStopLinker {

	private final double distanceThreshold;

	@Override
	public void link(Trip trip, List<BusStopTrip> busStopSequence) {
		var currentEntryIndex = 0;

		for (var s = 0; s < busStopSequence.size(); s++) {
			var currentStop = busStopSequence.get(s);

			var lastStop = s > 0 ? busStopSequence.get(s - 1) : null;
			//menorMaiorDistancia
			var smallestBiggerDistance = lastStop != null
					? lastStop.getCoord().distance(currentStop.getCoord())
					: 0d;
			var searching = true;
			var currentStopDirection = busStopSequence.size() / 2 >= currentStop.getStopSequence();

			var distanceTraveledTrip = trip.getCurrentDistanceTraveled();

			var previousCurrentEntryIndex = currentEntryIndex;

			for (var i = currentEntryIndex; i < trip.getEntries().size() - 1; i++) {
				var entry = trip.getEntries().get(i);
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
					currentEntryIndex = i - 1;
					i = trip.getEntries().size(); //NOSONAR
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

			trip.getBusStopsSequence().add(new BusStopTrip(currentStop));
		}
	}

}
