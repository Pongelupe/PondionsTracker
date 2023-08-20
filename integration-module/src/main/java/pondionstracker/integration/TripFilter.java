package pondionstracker.integration;

import java.util.function.BiPredicate;

import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;

public interface TripFilter extends BiPredicate<Trip, RealTimeTrip> {

}
