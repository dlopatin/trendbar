package com.dlopatin.trendbar.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.dlopatin.trendbar.model.Quote;
import com.dlopatin.trendbar.model.Symbol;
import com.dlopatin.trendbar.model.TrendBar;

public class QuoteObserver implements Runnable, Terminable {

	private volatile boolean running = true;
	private final BlockingQueue<Quote> quotes;
	private final Map<Symbol, List<TrendBar>> trendBarsMap;

	public QuoteObserver(Map<Symbol, List<TrendBar>> trendBars, BlockingQueue<Quote> quotes) {
		this.trendBarsMap = trendBars;
		this.quotes = quotes;
	}

	@Override
	public void run() {
		while (running) {
			Quote quote;
			try {
				while ((quote = quotes.poll(500, TimeUnit.MILLISECONDS)) != null) {
					List<TrendBar> trendBars = trendBarsMap.get(quote.getSymbol());
					if (null != trendBars) {
						for (TrendBar trendBar : trendBars) {
							trendBar.update(quote);
						}
					}
				}
			} catch (InterruptedException e) {
				terminate();
				Thread.currentThread().interrupt();
			}
		}

	}

	@Override
	public void terminate() {
		running = false;
	}

}
