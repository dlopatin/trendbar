package com.dlopatin.trendbar.model;

import static com.dlopatin.trendbar.model.Currency.*;

public enum Symbol {

    EURUSD(EUR, USD), EURJPY(EUR, JPY);

    private Currency fistCurrency;
    private Currency secondCurrency;

    private Symbol(Currency fistCurrency, Currency secondCurrency) {
	this.fistCurrency = fistCurrency;
	this.secondCurrency = secondCurrency;
    }

    public Currency getFistCurrency() {
	return fistCurrency;
    }

    public Currency getSecondCurrency() {
	return secondCurrency;
    }

}
