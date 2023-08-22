package pondionstracker.integration.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import pondionstracker.base.model.BusStopTrip;
import pondionstracker.base.model.RealTimeBusEntry;
import pondionstracker.base.model.RealTimeTrip;
import pondionstracker.base.model.Trip;
import pondionstracker.integration.EntryMerger;
import pondionstracker.integration.TripMissingEntriesGenerator;

@RequiredArgsConstructor
public class DefaultTripMissingEntriesGenerator implements TripMissingEntriesGenerator {

	private final EntryMerger merger;
	
	@Override
	public void generateMissingEntries(Trip trip, RealTimeTrip realTimeTrip) {
		var busStopSequence = trip.getBusStopsSequence();
		var missingStops = busStopSequence
		.stream()
		.parallel()
		.filter(e -> e.getEntries() == null)
		.toList();
		var departureEntryIndex = realTimeTrip.getEntries().indexOf(realTimeTrip.getDeparture());
		
		for (var missingStop : missingStops) {
			var indexStop = missingStop.getStopSequence();
			var indexMaxEntryPreviousStop = getIndexMaxEntryPreviousStop(busStopSequence, indexStop, departureEntryIndex);
			var indexMinRegistroPontoSeguinte = getIndexMinEntryNextStop(busStopSequence, indexStop)
					.orElse(indexMaxEntryPreviousStop);
			
			var registrosCandidatos = realTimeTrip
				.getEntries()
				.subList(indexMaxEntryPreviousStop, indexMinRegistroPontoSeguinte + 1)
				.stream()
				.sorted(Comparator.comparingDouble(r -> r.getCoord().distance(missingStop.getCoord())))
				.distinct()
				.toList();
			
			var registroMerged = merger.merge(registrosCandidatos.get(0), 
					registrosCandidatos.get(registrosCandidatos.size() > 1 ? 1 : 0));
			
			missingStop.setEntries(List.of(registroMerged));
			missingStop.setCalculated(true);
		}
		
	}
	
	private Optional<Integer> getIndexMinEntryNextStop(List<BusStopTrip> busStopSequence, int i) {
		return busStopSequence
				.stream()
				.parallel()
				.filter(e -> e.getEntries() != null)
				.filter(e -> e.getStopSequence() > i)
				.min(Comparator.comparingInt(BusStopTrip::getStopSequence))
				.map(p -> p.getEntries()
						.stream()
						.min(Comparator.comparing(RealTimeBusEntry::getDtEntry))
						.map(RealTimeBusEntry::getIndex)
						.get())
				;
	}
	
	private int getIndexMaxEntryPreviousStop(List<BusStopTrip> busStopSequence, int i, int departureEntryIndex) {
		return busStopSequence
				.stream()
				.parallel()
				.filter(e -> e.getEntries() != null)
				.filter(e -> e.getStopSequence() < i)
				.max(Comparator.comparingInt(BusStopTrip::getStopSequence))
				.map(BusStopTrip::getEntries)
				.orElse(List.of())
				.stream()
				.max(Comparator.comparing(RealTimeBusEntry::getDtEntry))
				.map(e -> e.getIndex())
				.orElse(departureEntryIndex);
	}
	
}
