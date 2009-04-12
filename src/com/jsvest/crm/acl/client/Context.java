package com.jsvest.crm.acl.client;

public interface Context {
	
	StockPriceServiceAsync stockService();
	StockWatcherConstants constants();
	StockWatcherMessages messages();

}
