package com.dlopatin.trendbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Quote {

    private final Symbol symbol;
    private final LocalDateTime timestamp;
    private final BigDecimal price;

    public Quote(Symbol symbol, BigDecimal price, LocalDateTime timestamp) {
	this.symbol = symbol;
	this.price = price;
	this.timestamp = timestamp;
    }

    public Symbol getSymbol() {
	return symbol;
    }

    public LocalDateTime getTimestamp() {
	return timestamp;
    }

    public BigDecimal getPrice() {
	return price;
    }

}
