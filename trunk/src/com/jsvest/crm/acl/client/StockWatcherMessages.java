package com.jsvest.crm.acl.client;

import com.google.gwt.i18n.client.Messages;

public interface StockWatcherMessages extends Messages {
	String lastUpdate(String timestamp);

	String invalidSymbol(String symbol);

	String errorMsg(String message);

	String delistedError(String symbol);
}