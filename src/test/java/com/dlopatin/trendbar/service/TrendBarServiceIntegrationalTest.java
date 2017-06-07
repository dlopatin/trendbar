package com.dlopatin.trendbar.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dlopatin.trendbar.model.Quote;
import com.dlopatin.trendbar.model.Symbol;
import com.dlopatin.trendbar.model.TrendBar;
import com.dlopatin.trendbar.model.TrendBarPeriod;

public class TrendBarServiceIntegrationalTest {

	private TrendBarService service;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(TrendBarPeriod.M1, "period", 1);
		service = new TrendBarService();
	}

	@After
	public void shutDown() throws InterruptedException {
		service.dispose();
	}

	@Test
	public void testSymbolNotRegistered() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		service.addQuote(new Quote(Symbol.EURJPY, new BigDecimal("9.1"), LocalDateTime.now()));
		service.addQuote(new Quote(Symbol.EURUSD, new BigDecimal("8.1"), LocalDateTime.now()));
		Thread.sleep(1500);
		service.dispose();

		assertThat(service.list(Symbol.EURJPY, TrendBarPeriod.M1, start, LocalDateTime.now()), is(empty()));
		assertThat(service.list(Symbol.EURUSD, TrendBarPeriod.M1, start, LocalDateTime.now()), is(empty()));
	}

	@Test
	public void testOneSymbolRegistered() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		service.registerSymbol(Symbol.EURJPY);
		service.addQuote(new Quote(Symbol.EURJPY, new BigDecimal("9.1"), LocalDateTime.now()));
		service.addQuote(new Quote(Symbol.EURUSD, new BigDecimal("8.1"), LocalDateTime.now()));
		Thread.sleep(1100);
		service.dispose();

		assertThat(service.list(Symbol.EURJPY, TrendBarPeriod.M1, start, LocalDateTime.now()), hasSize(1));
		assertThat(service.list(Symbol.EURUSD, TrendBarPeriod.M1, start, LocalDateTime.now()), is(empty()));
	}

	@Test
	public void testTwoSymbolRegistered() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		service.registerSymbol(Symbol.EURJPY);
		service.registerSymbol(Symbol.EURUSD);
		Quote eurJpyQuote = new Quote(Symbol.EURJPY, new BigDecimal("9.1"), LocalDateTime.now());
		service.addQuote(eurJpyQuote);
		Quote eurUsdQuote = new Quote(Symbol.EURUSD, new BigDecimal("8.1"), LocalDateTime.now());
		service.addQuote(eurUsdQuote);
		Thread.sleep(1100);
		service.dispose();

		List<TrendBar> eurjpy = service.list(Symbol.EURJPY, TrendBarPeriod.M1, start, LocalDateTime.now());
		assertThat(eurjpy, hasSize(1));
		assertTrendBar(eurjpy.get(0), eurJpyQuote);

		List<TrendBar> eurusd = service.list(Symbol.EURUSD, TrendBarPeriod.M1, start, LocalDateTime.now());
		assertThat(eurusd, hasSize(1));
		assertTrendBar(eurusd.get(0), eurUsdQuote);
	}

	@Test
	public void testUnregisterSymbol() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		service.registerSymbol(Symbol.EURJPY);
		service.registerSymbol(Symbol.EURUSD);
		service.addQuote(new Quote(Symbol.EURJPY, new BigDecimal("9.1"), LocalDateTime.now()));
		service.addQuote(new Quote(Symbol.EURUSD, new BigDecimal("8.1"), LocalDateTime.now()));
		Thread.sleep(1100);

		service.unregisterSymbol(Symbol.EURJPY);
		service.addQuote(new Quote(Symbol.EURUSD, new BigDecimal("7.1"), LocalDateTime.now()));
		Thread.sleep(1100);
		service.dispose();

		assertThat(service.list(Symbol.EURJPY, TrendBarPeriod.M1, start, LocalDateTime.now()), hasSize(1));

		assertThat(service.list(Symbol.EURUSD, TrendBarPeriod.M1, start, LocalDateTime.now()), hasSize(2));
	}

	@Test
	public void testLateQuote() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		service.registerSymbol(Symbol.EURJPY);
		service.registerSymbol(Symbol.EURUSD);
		LocalDateTime dateInPast = LocalDateTime.now().minus(3, ChronoUnit.SECONDS);
		Quote eurJpyQuote = new Quote(Symbol.EURJPY, new BigDecimal("9.1"), dateInPast);
		service.addQuote(eurJpyQuote);
		Quote eurUsdQuote = new Quote(Symbol.EURUSD, new BigDecimal("8.1"), dateInPast);
		service.addQuote(eurUsdQuote);
		Thread.sleep(1100);
		service.dispose();

		List<TrendBar> eurjpy = service.list(Symbol.EURJPY, TrendBarPeriod.M1, start, LocalDateTime.now());
		assertThat(eurjpy, hasSize(1));
		assertTrendBarIsEmpty(eurjpy.get(0));

		List<TrendBar> eurusd = service.list(Symbol.EURUSD, TrendBarPeriod.M1, start, LocalDateTime.now());
		assertThat(eurusd, hasSize(1));
		assertTrendBarIsEmpty(eurusd.get(0));
	}

	private void assertTrendBar(TrendBar trendBar, Quote quote) {
		assertThat(trendBar.getClosePrice(), is(quote.getPrice()));
		assertThat(trendBar.getOpenPrice(), is(quote.getPrice()));
		assertThat(trendBar.getLowPrice(), is(quote.getPrice()));
		assertThat(trendBar.getHighPrice(), is(quote.getPrice()));
		assertThat(trendBar.getTimestamp(), is(lessThanOrEqualTo(quote.getTimestamp())));
	}

	private void assertTrendBarIsEmpty(TrendBar trendBar) {
		assertThat(trendBar.getClosePrice(), is(nullValue()));
		assertThat(trendBar.getOpenPrice(), is(nullValue()));
		assertThat(trendBar.getLowPrice(), is(nullValue()));
		assertThat(trendBar.getHighPrice(), is(nullValue()));
	}
}
