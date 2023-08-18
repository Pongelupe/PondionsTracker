package pondionstracker.data.providers;

import java.util.Date;
import java.util.Optional;

import pondionstracker.base.model.Route;

public interface GTFSService {

	default Optional<Route> getRouteByRouteShortName(String routeShortName) {
		return getRouteByRouteShortName(routeShortName, new Date());
	}

	Optional<Route> getRouteByRouteShortName(String routeShortName, Date date);
	
}
