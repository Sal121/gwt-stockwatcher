package com.jsvest.crm.acl.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AjaxLoader;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.AreaChart;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.ColumnChart;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.OrgChart;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.google.gwt.visualization.client.visualizations.Table.Options.Policy;

public class StockWatcher implements EntryPoint {

	private static final int REFRESH_INTERVAL = 5000; // ms

	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addButton = new Button();
	private Label lastUpdatedLabel = new Label();

	private Label errorMsgLabel = new Label();

	private VerticalPanel chartPanel = new VerticalPanel();
	
	private ArrayList<String> stocks = new ArrayList<String>();

	StockPriceServiceAsync stockPriceSvc;

	private StockWatcherConstants constants;
	private StockWatcherMessages messages;

	public void onModuleLoad() {
		constants = GWT.create(StockWatcherConstants.class);
		messages = GWT.create(StockWatcherMessages.class);

		Window.setTitle(constants.stockWatcher());
		RootPanel.get("appTitle").add(new Label(constants.stockWatcher()));

		RootPanel.get("menu").add(createMenu());
		//RootPanel.get("menu").add(createTree());

		// add the main panel to the HTML element with the id "stockList"
		buildStockPanel();
		//RootPanel.get("stockList").add(mainPanel);

		//buildChart();
		RootPanel.get("chart").add(chartPanel);
	}

	private MenuBar createMenu() {
		// Make a command that we will execute from all leaves.
		Command cmd1 = new Command() {
			public void execute() {
				RootPanel.get("stockList").clear();
				//chartPanel.clear();
				buildChart();
			}
		};

		Command cmd2 = new Command() {
			public void execute() {
				RootPanel.get("stockList").add(mainPanel);
				chartPanel.clear();
			}
		};

		// Make some sub-menus that we will cascade from the top menu.
		MenuBar fooMenu = new MenuBar(true);
		fooMenu.addItem("Chart", cmd1);
		fooMenu.addSeparator();
		fooMenu.addItem("Watcher", cmd2);

		MenuBar barMenu = new MenuBar(true);
		barMenu.addItem("the", cmd1);
		barMenu.addItem("bar", cmd1);
		barMenu.addItem("menu", cmd1);

		MenuBar bazMenu = new MenuBar(true);
		bazMenu.addItem("the", cmd1);
		bazMenu.addItem("baz", cmd1);
		bazMenu.addItem("menu", cmd1);

		// Make a new menu bar, adding a few cascading menus to it.
		MenuBar menu = new MenuBar();
		menu.addItem("CRM", fooMenu);
		menu.addSeparator();
		menu.addItem("bar", barMenu);
		menu.addSeparator();
		menu.addItem("baz", bazMenu);
		return menu;

	}

	private Tree createTree() {
		// Create a tree with a few items in it.
		TreeItem root = new TreeItem("root");
		root.addItem("item0");
		root.addItem("item1");
		root.addItem("item2");

		// Add a CheckBox to the tree
		TreeItem item = new TreeItem(new CheckBox("item3"));
		root.addItem(item);

		Tree t = new Tree();
		t.addItem(root);

		return t;

	}

	private void buildStockPanel() {
		// set up stock list table
		stocksFlexTable.setCellPadding(5);
		stocksFlexTable.setText(0, 0, constants.symbol());
		stocksFlexTable.setText(0, 1, constants.price());
		stocksFlexTable.setText(0, 2, constants.change());
		stocksFlexTable.setText(0, 3, constants.remove());

		stocksFlexTable.setCellPadding(5);
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.getCellFormatter().addStyleName(0, 1,
				"watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 2,
				"watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 3,
				"watchListRemoveColumn");

		addButton.setText(constants.add());
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
		mainPanel.add(errorMsgLabel);
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);


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
			Window.alert(messages.invalidSymbol(symbol));
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
		// lazy initialization of service proxy
		if (stockPriceSvc == null) {
			stockPriceSvc = GWT.create(StockPriceService.class);
		}

		AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
			public void onFailure(Throwable caught) {
				// display the error message above the watch list
				String details = caught.getMessage();
				if (caught instanceof DelistedException) {
					details = messages
							.delistedError(((DelistedException) caught)
									.getSymbol());
				}

				errorMsgLabel.setText(messages.errorMsg(details));
				errorMsgLabel.setVisible(true);

			}

			public void onSuccess(StockPrice[] result) {
				updateTable(result);
			}
		};

		// make the call to the stock price service
		stockPriceSvc.getPrices(stocks.toArray(new String[0]), callback);

	}

	private void updateTable(StockPrice[] prices) {
		for (int i = 0; i < prices.length; i++) {
			updateTable(prices[i]);
		}

		// change the last update timestamp
		String timestamp = DateTimeFormat.getMediumDateTimeFormat().format(
				new Date());
		lastUpdatedLabel.setText(messages.lastUpdate(timestamp));

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

	private void buildChart() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {

				// Create a pie chart visualization.
				PieChart pie = new PieChart(createTable(), createOptions());
				LineChart line = new LineChart(createTable(),
						createLineOptions());
				Table table = new Table(createTable(), createTableOptions());
				;
				// pie.addSelectHandler(createSelectHandler(pie));
				
				chartPanel.add(pie);
				chartPanel.add(line);
				chartPanel.add(table);

				com.google.gwt.visualization.client.visualizations.ColumnChart.Options options = com.google.gwt.visualization.client.visualizations.ColumnChart.Options
						.create();
				options.setHeight(240);
				options.setTitle("My Daily Activities");
				options.setWidth(400);
				options.set3D(true);

				ColumnChart viz = new ColumnChart(createTable(), options);

				chartPanel.add(viz);

			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		AjaxLoader.loadVisualizationApi(onLoadCallback,
				AnnotatedTimeLine.PACKAGE, AreaChart.PACKAGE, BarChart.PACKAGE,
				ColumnChart.PACKAGE, LineChart.PACKAGE, OrgChart.PACKAGE,
				PieChart.PACKAGE, Table.PACKAGE);
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(400);
		options.setHeight(240);
		options.set3D(true);
		options.setTitle("My Daily Activities");
		return options;
	}

	private com.google.gwt.visualization.client.visualizations.LineChart.Options createLineOptions() {
		com.google.gwt.visualization.client.visualizations.LineChart.Options options = com.google.gwt.visualization.client.visualizations.LineChart.Options
				.create();
		options.setWidth(400);
		options.setHeight(240);
		options.setTitle("My Daily Activities");
		return options;
	}

	private com.google.gwt.visualization.client.visualizations.Table.Options createTableOptions() {
		com.google.gwt.visualization.client.visualizations.Table.Options options = com.google.gwt.visualization.client.visualizations.Table.Options
				.create();
		options.setShowRowNumber(true);
		options.setPageSize(2);
		options.setPage(Policy.ENABLE);
		return options;
	}

	private SelectHandler createSelectHandler(final PieChart chart) {
		return new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				String message = "";

				// May be multiple selections.
				JsArray<Selection> selections = chart.getSelections();

				for (int i = 0; i < selections.length(); i++) {
					// add a new line for each selection
					message += i == 0 ? "" : "\n";

					Selection selection = selections.get(i);

					if (selection.isCell()) {
						// isCell() returns true if a cell has been selected.

						// getRow() returns the row number of the selected cell.
						int row = selection.getRow();
						// getColumn() returns the column number of the selected
						// cell.
						int column = selection.getColumn();
						message += "cell " + row + ":" + column + " selected";
					} else if (selection.isRow()) {
						// isRow() returns true if an entire row has been
						// selected.

						// getRow() returns the row number of the selected row.
						int row = selection.getRow();
						message += "row " + row + " selected";
					} else {
						// unreachable
						message += "Pie chart selections should be either row selections or cell selections.";
						message += "  Other visualizations support column selections as well.";
					}
				}

				Window.alert(message);
			}
		};
	}

	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Task");
		data.addColumn(ColumnType.NUMBER, "Hours per Day");
		data.addColumn(ColumnType.NUMBER, "Days per Month");
		data.addRows(6);
		data.setValue(0, 0, "Work");
		data.setValue(0, 1, 14);
		data.setValue(0, 2, 3);
		data.setValue(1, 0, "Sleep");
		data.setValue(1, 1, 10);
		data.setValue(1, 2, 7);
		data.setValue(2, 0, "Play");
		data.setValue(2, 1, 5);
		data.setValue(2, 2, 13);
		data.setValue(3, 0, "AA");
		data.setValue(3, 1, 17);
		data.setValue(3, 2, 9);
		data.setValue(4, 0, "BB");
		data.setValue(4, 1, 20);
		data.setValue(4, 2, 13);
		data.setValue(5, 0, "CC");
		data.setValue(5, 1, 3);
		data.setValue(5, 2, 20);
		return data;
	}

}
