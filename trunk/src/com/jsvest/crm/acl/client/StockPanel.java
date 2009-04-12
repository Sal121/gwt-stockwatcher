package com.jsvest.crm.acl.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StockPanel extends VerticalPanel {

	private static final int REFRESH_INTERVAL = 50000; // ms

	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addButton = new Button();
	private Label lastUpdatedLabel = new Label();

	private Label errorMsgLabel = new Label();

	private ArrayList<String> stocks = new ArrayList<String>();

	private Context ctx;

	public StockPanel(Context ctx) {
		this.ctx = ctx;
		buildStockPanel();
		
	}
	private void buildStockPanel() {
		// set up stock list table

		stocksFlexTable.setCellPadding(5);
		stocksFlexTable.setText(0, 0, ctx.constants().symbol());
		stocksFlexTable.setText(0, 1, ctx.constants().price());
		stocksFlexTable.setText(0, 2, ctx.constants().change());
		stocksFlexTable.setText(0, 3, ctx.constants().remove());

		stocksFlexTable.setCellPadding(5);
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.getCellFormatter().addStyleName(0, 1,
				"watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 2,
				"watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 3,
				"watchListRemoveColumn");

		addButton.setText(ctx.constants().add());
		newSymbolTextBox.addStyleName("invalidEntry");
		// set up event listeners for adding a new stock
		addButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				// showMessage(sender.getTitle());
				addStock();
			}
		});

		newSymbolTextBox.addKeyboardListener(new KeyboardListenerAdapter() {
			@Override
			public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				if (keyCode == KEY_ENTER) {
					addStock();
				}
			}
		});

		// assemble Add Stock panel
		addPanel.add(newSymbolTextBox);
		addPanel.add(addButton);
		addPanel.addStyleName("addPanel");

		// assemble main panel
		errorMsgLabel.setStyleName("errorMessage");
		errorMsgLabel.setVisible(false);

		// assemble main panel
		add(errorMsgLabel);
		add(stocksFlexTable);
		add(addPanel);
		add(lastUpdatedLabel);


		// setup timer to refresh list automatically
		Timer refreshTimer = new Timer() {
			public void run() {
				refreshWatchList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

	}

	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();

		// symbol must be between 1 and 10 chars that are numbers, letters, or
		// dots
		if (!symbol.matches("^[0-9a-zA-Z\\.]{1,10}$")) {
			Window.alert(ctx.messages().invalidSymbol(symbol));
			newSymbolTextBox.selectAll();
			newSymbolTextBox.addStyleName("invalidEntry");
			return;
		}

		newSymbolTextBox.removeStyleName("invalidEntry");
		newSymbolTextBox.setText("");
		// newSymbolTextBox.setFocus(true);
		// don't add the stock if it's already in the watch list
		if (stocks.contains(symbol)) {
			return;
		}

		// add the stock to the list
		int row = stocksFlexTable.getRowCount();
		stocks.add(symbol);
		stocksFlexTable.setText(row, 0, symbol);
		stocksFlexTable.setWidget(row, 2, new Label());
		stocksFlexTable.getCellFormatter().addStyleName(row, 1,
				"watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 2,
				"watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 3,
				"watchListRemoveColumn");

		// add button to remove this stock from the list
		Button removeStock = new Button("X");
		removeStock.addStyleDependentName("remove");

		removeStock.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				int removedIndex = stocks.indexOf(symbol);
				stocks.remove(removedIndex);
				stocksFlexTable.removeRow(removedIndex + 1);
			}
		});
		stocksFlexTable.setWidget(row, 3, removeStock);
	}

	private void refreshWatchList() {
		AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
			public void onFailure(Throwable caught) {
				// display the error message above the watch list
				String details = caught.getMessage();
				if (caught instanceof DelistedException) {
					details = ctx.messages()
							.delistedError(((DelistedException) caught)
									.getSymbol());
				}

				errorMsgLabel.setText(ctx.messages().errorMsg(details));
				errorMsgLabel.setVisible(true);

			}

			public void onSuccess(StockPrice[] result) {
				updateTable(result);
			}
		};

		// make the call to the stock price service
		ctx.stockService().getPrices(stocks.toArray(new String[0]), callback);

	}

	private void updateTable(StockPrice[] prices) {
		for (int i = 0; i < prices.length; i++) {
			updateTable(prices[i]);
		}

		// change the last update timestamp
		String timestamp = DateTimeFormat.getMediumDateTimeFormat().format(
				new Date());
		lastUpdatedLabel.setText(ctx.messages().lastUpdate(timestamp));

		// clear any errors
		errorMsgLabel.setVisible(false);

	}

	private void updateTable(StockPrice price) {
		// make sure the stock is still in our watch list
		if (!stocks.contains(price.getSymbol())) {
			return;
		}

		int row = stocks.indexOf(price.getSymbol()) + 1;

		// apply nice formatting to price and change
		String priceText = NumberFormat.getFormat("#,##0.00").format(
				price.getPrice());
		NumberFormat changeFormat = NumberFormat
				.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(price.getChange());
		String changePercentText = changeFormat
				.format(price.getChangePercent());

		// update the watch list with the new values
		stocksFlexTable.setText(row, 1, priceText);
		Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
		changeWidget.setText(changeText + " (" + changePercentText + "%)");

		String changeStyleName = "noChange";
		if (price.getChangePercent() < -0.1f) {
			changeStyleName = "negativeChange";
		} else if (price.getChangePercent() > 0.1f) {
			changeStyleName = "positiveChange";
		}

		changeWidget.setStyleName(changeStyleName);

	}

}
