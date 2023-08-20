package pondionstracker.integration;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

public interface TripMissingEntriesGenerator {

	void generateMissingEntries(Trip trip, RealTimeTrip realTimeTrip);
}
