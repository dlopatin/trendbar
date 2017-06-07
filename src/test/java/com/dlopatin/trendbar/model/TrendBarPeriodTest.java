package com.dlopatin.trendbar.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class TrendBarPeriodTest {

	@Test
	public void testDelaySec_range() {
		for (TrendBarPeriod period : TrendBarPeriod.values()) {
			assertThat(period.getDelaySec(), is(both(lessThan(period.getPeriodSec())).and(greaterThanOrEqualTo(0L))));
		}
	}

	@Test
	public void testDelaySec_M1_accurateValue() {
		LocalDateTime now = LocalDateTime.now();
		long delay = now.getSecond() == 0 ? 0 : 60 - now.getSecond();
		assertThat(TrendBarPeriod.M1.getDelaySec(),
				is(both(lessThanOrEqualTo(delay + 1)).and(greaterThanOrEqualTo(delay))));
	}

	@Test
	public void testDelaySec_H1_accurateValue() {
		LocalDateTime now = LocalDateTime.now();
		long delay = (now.getSecond() == 0 ? 0 : 60 - now.getSecond())
			+ (now.getMinute() == 0 ? 0 : (59 - now.getMinute()) * 60);
		assertThat(TrendBarPeriod.H1.getDelaySec(),
				is(both(lessThanOrEqualTo(delay + 1)).and(greaterThanOrEqualTo(delay - 1))));
	}

}
