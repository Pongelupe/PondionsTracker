package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.Trip;

@FunctionalInterface
public interface TripExtractor {

	List<Trip> extract(List<RealTimeBusEntry> entries);
	
}
