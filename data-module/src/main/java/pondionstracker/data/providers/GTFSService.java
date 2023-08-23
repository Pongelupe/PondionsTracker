package pondionstracker.data.providers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import pondionstracker.base.model.Route;
import pondionstracker.base.model.StopPointsInterval;
import pondionstracker.base.model.Trip;

public interface GTFSService {

	default Optional<Route> getRouteByRouteShortName(String routeShortName) {
		return getRouteByRouteShortName(routeShortName, new Date());
	}

	Optional<Route> getRouteByRouteShortName(String routeShortName, Date date);
	
	List<String> getServiceIds(Date date);
	
	List<Trip> getTripsByRouteIdAndServiceIds(String routeId, List<String> serviceIds);
	
	List<StopPointsInterval> getStopPointsInterval(String tripId);
	
}
