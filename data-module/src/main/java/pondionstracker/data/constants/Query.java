package pondionstracker.data.constants;

import static pondionstracker.data.constants.Query.Parameter.*;

import java.util.List;

import lombok.Getter;

@Getter
public enum Query {
	
	GET_CALENDAR_BY_DATE(DATE),
	GET_ENTRIES,
	GET_ROUTE_BY_ROUTE_SHORT_NAME(ROUTE_SHORT_NAME),
	GET_STOP_SEQUENCE_BY_TRIP_ID(TRIP_ID),
	GET_STOP_TIME_INVERVAL_BY_TRIP_ID(TRIP_ID),
	GET_TRIPS_BY_ROUTE_ID_AND_SERVICE_IDS(ROUTE_ID, SERVCICE_IDS);
	
	private final List<Parameter> parameters;
	
	private Query(Parameter... parameters) {
		this.parameters = List.of(parameters);
	}
	
	public enum Parameter {
		DATE, DATE_END, DATE_START, LINE_ID, ROUTE_ID, ROUTE_SHORT_NAME, TRIP_ID, SERVCICE_IDS;
	}
	
}
