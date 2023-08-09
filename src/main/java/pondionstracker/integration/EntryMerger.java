package pondionstracker.integration;

import pondionstracker.base.model.RealTimeBus;

@FunctionalInterface
public interface EntryMerger {

	RealTimeBus merge(RealTimeBus e1, RealTimeBus e2);
	
}
