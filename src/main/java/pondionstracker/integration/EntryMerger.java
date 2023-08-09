package pondionstracker.integration;

import pondionstracker.base.model.RealTimeBusEntry;

@FunctionalInterface
public interface EntryMerger {

	RealTimeBusEntry merge(RealTimeBusEntry e1, RealTimeBusEntry e2);
	
}
