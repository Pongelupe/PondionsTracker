package pondionstracker.integration.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.postgis.jdbc.geometry.Point;
import pondionstracker.base.model.RealTimeBus;

/**
 * 
 * @author ppongelupe
 *
 */
class DefaultEntryMergerTest {

	private DefaultEntryMerger merger;
	
	private SimpleDateFormat f;
	
	private RealTimeBus e1; 
	private RealTimeBus e2;
	
	@BeforeEach
	void setUp() throws Exception {
		merger = new DefaultEntryMerger();
		
		f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		e1 = RealTimeBus.builder()
				.currentDistanceTraveled(100)
				.coord(new Point(2d, 4d))
				.dtEntry(f.parse("08/08/2023 20:47:21"))
				.build();
		
		e2 = RealTimeBus.builder()
				.currentDistanceTraveled(200)
				.coord(new Point(6d, 1d))
				.dtEntry(f.parse("08/08/2023 20:51:37"))
				.build();
	}

	@Test
	void testMerge() {
		var mergedEntry = merger.merge(e1, e2);
		
		assertNotEquals(e1, mergedEntry);
		assertNotEquals(e2, mergedEntry);
		
		assertEquals(150, mergedEntry.getCurrentDistanceTraveled());
		
		var coord = mergedEntry.getCoord();
		assertEquals(4d, coord.x);
		assertEquals(2.5d, coord.y);
		
		var diffInSeconds = (e2.getDtEntry().getTime() - e1.getDtEntry().getTime()) / 1000;
		assertEquals("08/08/2023 20:49:29", f.format(mergedEntry.getDtEntry()));
		assertEquals(256, diffInSeconds);
	}

}
