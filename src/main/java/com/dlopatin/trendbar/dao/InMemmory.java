package com.dlopatin.trendbar.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dlopatin.trendbar.model.Symbol;
import com.dlopatin.trendbar.model.TrendBar;
import com.dlopatin.trendbar.model.TrendBarPeriod;

public class InMemmory {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final List<TrendBar> storage = new CopyOnWriteArrayList<>();

	public void save(TrendBar trendBar) {
		logger.debug(trendBar.toString());
		storage.add(trendBar);
	}

	public List<TrendBar> list(Symbol symbol, TrendBarPeriod period, LocalDateTime from, LocalDateTime to) {
		if (from.isAfter(to)) {
			throw new IllegalArgumentException("'from' date is after 'to' date");
		}
		return storage
				.stream()
				.filter(bar -> bar.getPeriod() == period && bar.getSymbol() == symbol
					&& from.isBefore(bar.getTimestamp()) && to.isAfter(bar.getTimestamp()))
				.collect(Collectors.toList());
	}

}
