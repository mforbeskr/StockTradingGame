package business.dto;

import domain.State;

public record StockDTO(String symbol, String name, double currentPrice, State currentState){}

