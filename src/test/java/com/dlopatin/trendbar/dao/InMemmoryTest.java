package com.dlopatin.trendbar.dao;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dlopatin.trendbar.model.Symbol;
import com.dlopatin.trendbar.model.TrendBar;
import com.dlopatin.trendbar.model.TrendBarPeriod;

public class InMemmoryTest {

	private static final LocalDateTime TIME = LocalDateTime.of(2017, 6, 5, 1, 1, 1);
	private InMemmory service;

	@Before
	public void setUp() {
		service = new InMemmory();
	}

	@Test
	public void testSave() {
		TrendBar trendBar = createTrendBar(TIME);
		service.save(trendBar);

		@SuppressWarnings("unchecked")
		List<TrendBar> storage = (List<TrendBar>) ReflectionTestUtils.getField(service, "storage");
		assertThat(storage, contains(trendBar));
	}

	@Test
	public void testList_filterByDate() {
		TrendBar trendBarToBeReturned = createTrendBar(TIME);
		ReflectionTestUtils.setField(
				service,
				"storage",
				Arrays.asList(trendBarToBeReturned,
						createTrendBar(TIME.minus(1, ChronoUnit.DAYS)),
						createTrendBar(TIME.plus(1, ChronoUnit.DAYS))));
		List<TrendBar> result = service.list(Symbol.EURJPY, TrendBarPeriod.D1,
				TIME.minus(1, ChronoUnit.MINUTES),
				TIME.plus(1, ChronoUnit.MINUTES));

		assertThat(result, hasSize(1));
		assertThat(result, contains(trendBarToBeReturned));
	}

	@Test
	public void testList_filterBySymbol() {
		ReflectionTestUtils.setField(service, "storage", Arrays.asList(createTrendBar(TIME)));
		List<TrendBar> result = service.list(Symbol.EURUSD, TrendBarPeriod.D1,
				TIME.minus(1, ChronoUnit.MINUTES),
				TIME.plus(1, ChronoUnit.MINUTES));

		assertThat(result, is(empty()));
	}

	@Test
	public void testList_filterByPeriod() {
		ReflectionTestUtils.setField(service, "storage", Arrays.asList(createTrendBar(TIME)));
		List<TrendBar> result = service.list(Symbol.EURUSD, TrendBarPeriod.H1,
				TIME.minus(1, ChronoUnit.MINUTES),
				TIME.plus(1, ChronoUnit.MINUTES));
		assertThat(result, is(empty()));
	}

	private TrendBar createTrendBar(LocalDateTime time) {
		TrendBar trendBar = mock(TrendBar.class);
		when(trendBar.getPeriod()).thenReturn(TrendBarPeriod.D1);
		when(trendBar.getSymbol()).thenReturn(Symbol.EURJPY);
		when(trendBar.getLowPrice()).thenReturn(new BigDecimal("1.1"));
		when(trendBar.getHighPrice()).thenReturn(new BigDecimal("9.1"));
		when(trendBar.getOpenPrice()).thenReturn(new BigDecimal("4.1"));
		when(trendBar.getClosePrice()).thenReturn(new BigDecimal("5.1"));
		when(trendBar.getTimestamp()).thenReturn(time);
		return trendBar;
	}

}
