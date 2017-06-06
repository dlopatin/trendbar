package com.dlopatin.trendbar.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public enum TrendBarPeriod {

	M1(60, ChronoUnit.MINUTES),
	H1(M1.getPeriodSec() * 60, ChronoUnit.HOURS),
	D1(H1.getPeriodSec() * 24, ChronoUnit.DAYS);

	private final long period;
	private final ChronoUnit chronoUnit;

	private TrendBarPeriod(long period, ChronoUnit timeUnit) {
		this.period = period;
		this.chronoUnit = timeUnit;
	}

	public long getPeriodSec() {
		return period;
	}

	public long getDelaySec() {
		LocalDateTime now = LocalDateTime.now();
		return getPeriodSec() - ChronoUnit.SECONDS.between(now.truncatedTo(chronoUnit), now);
	}

}
