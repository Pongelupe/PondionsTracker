package pondionstracker.integration;

import java.util.List;

import pondionstracker.base.model.RealTimeBus;
import pondionstracker.base.model.Trip;

@FunctionalInterface
public interface TripExtractor {

	List<Trip> extract(List<RealTimeBus> entries);
	
}
