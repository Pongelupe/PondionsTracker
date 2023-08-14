package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.RealTimeTrip;

@FunctionalInterface
public interface TripExtractor {

	List<RealTimeTrip> extract(List<RealTimeBusEntry> entries);
	
}
