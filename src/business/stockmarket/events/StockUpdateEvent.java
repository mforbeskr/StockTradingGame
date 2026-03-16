package business.stockmarket.events;

import domain.State;

public record StockUpdateEvent(String symbol, String stateName, double price, State currentState, State previousState){}
