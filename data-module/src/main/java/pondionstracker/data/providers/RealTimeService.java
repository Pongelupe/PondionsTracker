package pondionstracker.data.providers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import pondionstracker.base.model.RealTimeBusEntry;

public interface RealTimeService {
	
	public List<Integer> getIdsLineByRouteId(String routeId);

	public Map<String, List<RealTimeBusEntry>> getEntriesByIdLine(Integer idLine, Date date);

	public Map<String, List<RealTimeBusEntry>> getEntriesByIdLine(Integer idLine, Date startDate, Date endDate);
	
}
