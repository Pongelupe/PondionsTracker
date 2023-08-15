package pondionstracker.data.providers;

import java.util.Optional;

import pondionstracker.base.model.Route;

public interface GTFSService {

	Optional<Route> getRouteByRouteShortName(String routeShortName);
	
}
