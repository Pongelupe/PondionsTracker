package pondionstracker.data.providers.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.data.components.QueryExecutor;
import pondionstracker.data.constants.Query;
import pondionstracker.data.providers.RealTimeService;

@RequiredArgsConstructor
public class DefaultRealTimeService implements RealTimeService {
	
	private final QueryExecutor queryExecutor;
	
	@Override
	public Map<String, List<RealTimeBusEntry>> getEntriesByIdLine(Integer idLine, Date date) {
		return getEntriesByIdLine(idLine, date, date);
	}

	@Override
	public Map<String, List<RealTimeBusEntry>> getEntriesByIdLine(Integer idLine, Date startDate, Date endDate) {
		return queryExecutor.queryAll(Query.GET_ENTRIES, rs -> RealTimeBusEntry.builder()
				.build(), Map.of())
				.stream()
				.collect(Collectors.groupingBy(RealTimeBusEntry::getIdVehicle));
	}

	@Override
	public List<Integer> getIdsLineByRouteId(String routeId) {
		return List.of();
	}

}
