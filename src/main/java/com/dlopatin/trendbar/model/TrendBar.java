package com.dlopatin.trendbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrendBar {

	private static Logger logger = LoggerFactory.getLogger(TrendBar.class);

	private final Object lock = new Object();

	private final TrendBarPeriod period;
	private final Symbol symbol;
	private LocalDateTime timestamp;

	private BigDecimal openPrice;
	private BigDecimal closePrice;
	private BigDecimal highPrice;
	private BigDecimal lowPrice;

	public TrendBar(TrendBarPeriod period, Symbol symbol) {
		this.period = period;
		this.symbol = symbol;
		updateTimestamp();
	}

	private TrendBar(TrendBar trendBar) {
		this.period = trendBar.getPeriod();
		this.symbol = trendBar.getSymbol();
		this.timestamp = trendBar.getTimestamp();
		this.openPrice = trendBar.getOpenPrice();
		this.closePrice = trendBar.getClosePrice();
		this.highPrice = trendBar.getHighPrice();
		this.lowPrice = trendBar.getLowPrice();
	}

	public void update(Quote quote) {
		synchronized (lock) {
			if (quote.getTimestamp().isBefore(getTimestamp())) {
				logger.warn("late quote, skipping");
				return;
			}
			BigDecimal price = quote.getPrice();
			if (getOpenPrice() == null) {
				setOpenPrice(price);
			}
			setClosePrice(price);
			if (getHighPrice() == null || getHighPrice().compareTo(price) < 0) {
				setHighPrice(price);
			}
			if (getLowPrice() == null || getLowPrice().compareTo(price) > 0) {
				setLowPrice(price);
			}
		}
	}

	public TrendBar getSnapshotAndReset() {
		synchronized (lock) {
			TrendBar snapshot = new TrendBar(this);
			clearState();
			return snapshot;
		}
	}

	private void updateTimestamp() {
		timestamp = LocalDateTime.now();
	}

	private void clearState() {
		synchronized (lock) {
			updateTimestamp();
			setOpenPrice(null);
			setClosePrice(null);
			setHighPrice(null);
			setLowPrice(null);
		}
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	private void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	private void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public BigDecimal getHighPrice() {
		return highPrice;
	}

	private void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}

	public BigDecimal getLowPrice() {
		return lowPrice;
	}

	private void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}

	public TrendBarPeriod getPeriod() {
		return period;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrendBar")
				.append(" period=").append(period)
				.append(", symbol=").append(symbol)
				.append(", timestamp=").append(timestamp)
				.append(", openPrice=").append(openPrice)
				.append(", closePrice=").append(closePrice)
				.append(", highPrice=").append(highPrice)
				.append(", lowPrice=").append(lowPrice).append("]");
		return builder.toString();
	}

}
