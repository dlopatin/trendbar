package com.dlopatin.trendbar;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

import com.dlopatin.trendbar.model.Quote;
import com.dlopatin.trendbar.model.Symbol;

public class EurUsdQuoteProvider implements QouteProvider, Runnable {

	private final BlockingQueue<Quote> quotes;

	private final SecureRandom rnd = new SecureRandom();

	public EurUsdQuoteProvider(BlockingQueue<Quote> quotes) {
		this.quotes = quotes;
		rnd.setSeed(rnd.generateSeed(32));
	}

	@Override
	public void generate() throws InterruptedException {
		Quote q = new Quote(Symbol.EURUSD, new BigDecimal(rnd.nextDouble() * 10), LocalDateTime.now());
		quotes.put(q);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				generate();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
