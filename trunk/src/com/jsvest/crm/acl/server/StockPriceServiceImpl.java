package com.jsvest.crm.acl.server;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.jsvest.crm.acl.client.DelistedException;
import com.jsvest.crm.acl.client.StockPrice;
import com.jsvest.crm.acl.client.StockPriceService;

public class StockPriceServiceImpl extends RemoteServiceServlet implements
		StockPriceService {

	private static final double MAX_PRICE = 100.0; // $100.00
	private static final double MAX_PRICE_CHANGE = 0.02; // +/- 2%


	@Override
	public StockPrice[] getPrices(String[] symbols) throws DelistedException {
		System.out.println("invoke RPC Service!");
		Random rnd = new Random();

		StockPrice[] prices = new StockPrice[symbols.length];
		for (int i = 0; i < symbols.length; i++) {
			if (symbols[i].equals("ERR")) {
				throw new DelistedException("ERR");
			}

			double price = rnd.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE
					* (rnd.nextDouble() * 2f - 1f);

			prices[i] = new StockPrice(symbols[i], price, change);
		}

		return prices;

	}

}
