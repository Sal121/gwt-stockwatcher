package com.jsvest.crm.acl.client;

public class ContextImpl implements Context {

	private StockPriceServiceAsync stockService;

	private StockWatcherConstants constants;
	private StockWatcherMessages messages;

	public ContextImpl() {
	}
	
	public ContextImpl(StockPriceServiceAsync service, StockWatcherConstants constants, StockWatcherMessages messages) {
		this.stockService = service;
		this.constants = constants;
		this.messages = messages;
	}
	
	@Override
	public StockWatcherConstants constants() {
		return constants;
	}

	@Override
	public StockWatcherMessages messages() {
		return messages;
	}

	@Override
	public StockPriceServiceAsync stockService() {
		return stockService;
	}

	public void setStockService(StockPriceServiceAsync stockPriceSvc) {
		this.stockService = stockPriceSvc;
	}

	public void setConstants(StockWatcherConstants constants) {
		this.constants = constants;
	}

	public void setMessages(StockWatcherMessages messages) {
		this.messages = messages;
	}

}
