package pondionstracker.data.providers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import pondionstracker.base.model.RealTimeBusEntry;

public interface RealTimeService {
	
	public List<String> getIdsLineByRouteId(String routeId);

	public Map<String, List<RealTimeBusEntry>> getEntriesByDtEntryAndLineIds(Date date, String... idLines);

	public Map<String, List<RealTimeBusEntry>> getEntriesByDtEntryAndLineIds(Date startDate, Date endDate, String... idLines);
	
}
