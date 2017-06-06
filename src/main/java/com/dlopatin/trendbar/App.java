package com.dlopatin.trendbar;

import java.util.concurrent.BlockingQueue;

import com.dlopatin.trendbar.model.Quote;
import com.dlopatin.trendbar.model.Symbol;
import com.dlopatin.trendbar.service.TrendBarService;

public class App {

	public static void main(String[] args) throws InterruptedException {
		TrendBarService service = new TrendBarService();
		BlockingQueue<Quote> quotes = service.getQuotes();
		service.registerSymbol(Symbol.EURUSD);
		service.registerSymbol(Symbol.EURJPY);
		Thread eurJpyProvider = new Thread(new EurJpyQuoteProvider(quotes));
		Thread eurUsdProvider = new Thread(new EurUsdQuoteProvider(quotes));
		eurJpyProvider.start();
		eurUsdProvider.start();
		Thread.sleep(130_000);
		eurJpyProvider.interrupt();
		eurUsdProvider.interrupt();
		service.dispose();
	}
}
