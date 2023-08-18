package pondionstracker.data.constants;

import static pondionstracker.data.constants.Query.Parameter.*;

import java.util.List;

import lombok.Getter;

@Getter
public enum Query {
	
	GET_CALENDAR_BY_DATE(DATE),
	GET_ENTRIES,
	GET_ROUTE_BY_ROUTE_SHORT_NAME(ROUTE_SHORT_NAME),
	GET_TRIPS_BY_ROUTE_ID;
	
	private final List<Parameter> parameters;
	
	private Query(Parameter... parameters) {
		this.parameters = List.of(parameters);
	}
	
	public enum Parameter {
		DATE, ROUTE_ID, ROUTE_SHORT_NAME, SERVCICE_IDS;
	}
	
}
