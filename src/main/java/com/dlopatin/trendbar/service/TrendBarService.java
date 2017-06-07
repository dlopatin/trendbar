package com.dlopatin.trendbar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dlopatin.trendbar.dao.InMemmory;
import com.dlopatin.trendbar.model.Quote;
import com.dlopatin.trendbar.model.Symbol;
import com.dlopatin.trendbar.model.TrendBar;
import com.dlopatin.trendbar.model.TrendBarPeriod;

public class TrendBarService {

	private static final int WORKES_NUM = 5;
	private static final int EXECUTOR_TIMEOUT_MS = 2000;

	private static Logger logger = LoggerFactory.getLogger(TrendBarService.class);

	// TODO: DI candidate
	private final InMemmory dao = new InMemmory();

	private final BlockingQueue<Quote> quotes = new LinkedBlockingQueue<>();

	private final Map<Symbol, List<TrendBar>> trendBarsMap = new ConcurrentHashMap<>();
	private final List<QuoteObserver> workersList = new ArrayList<>(WORKES_NUM);

	private final ScheduledThreadPoolExecutor barSavers = new ScheduledThreadPoolExecutor(
			TrendBarPeriod.values().length);
	private final ExecutorService workersServer = Executors.newFixedThreadPool(WORKES_NUM);

	public TrendBarService() {
		barSavers.setRemoveOnCancelPolicy(true);
		for (int i = 0; i < WORKES_NUM; i++) {
			QuoteObserver observer = new QuoteObserver(trendBarsMap, quotes);
			workersList.add(observer);
			workersServer.execute(observer);
		}
		for (TrendBarPeriod period : TrendBarPeriod.values()) {
			barSavers.scheduleAtFixedRate(
					() -> trendBarsMap.values().stream()
							.flatMap(List::stream)
							.filter(trendBar -> trendBar.getPeriod() == period)
							.forEach(this::save),
					period.getDelaySec(),
					period.getPeriodSec(), TimeUnit.SECONDS);
		}
	}

	public void addQuote(Quote quote) {
		quotes.add(quote);
	}

	public BlockingQueue<Quote> getQuotes() {
		return quotes;
	}

	public void registerSymbol(Symbol symbol) {
		List<TrendBar> trendBars = new CopyOnWriteArrayList<>();
		if (trendBarsMap.putIfAbsent(symbol, trendBars) == null) {
			for (TrendBarPeriod period : TrendBarPeriod.values()) {
				trendBars.add(new TrendBar(period, symbol));
			}
		} else {
			logger.error("Symbol already registered {} ", symbol);
		}
	}

	public void unregisterSymbol(Symbol symbol) {
		trendBarsMap.remove(symbol);
	}

	public void save(TrendBar trendBar) {
		dao.save(trendBar.getSnapshotAndReset());
	}

	public List<TrendBar> list(Symbol symbol, TrendBarPeriod period, LocalDateTime from, LocalDateTime to) {
		return dao.list(symbol, period, from, to);
	}

	public void dispose() throws InterruptedException {
		workersList.forEach(worker -> worker.terminate());
		shutdownExecutor(workersServer);
		shutdownExecutor(barSavers);
	}

	private void shutdownExecutor(ExecutorService executor) throws InterruptedException {
		logger.info("Shutting down executor {}", executor);
		executor.shutdown();
		if (!executor.awaitTermination(EXECUTOR_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
			executor.shutdownNow();
		}
		logger.info("Executor shut down: {}", executor.isShutdown());
	}

}
