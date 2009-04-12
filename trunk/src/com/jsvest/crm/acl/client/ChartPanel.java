package com.jsvest.crm.acl.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
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

public class ChartPanel extends Grid {
	
	private Context ctx;
	
	public ChartPanel(Context ctx) {
		super(2, 4);
		this.ctx = ctx;
		buildChart();
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
				
				setWidget(0, 0, pie);
				setWidget(0, 1, line);
				setWidget(1, 0, table);

				com.google.gwt.visualization.client.visualizations.ColumnChart.Options options = com.google.gwt.visualization.client.visualizations.ColumnChart.Options
						.create();
				options.setHeight(240);
				options.setTitle("My Daily Activities");
				options.setWidth(400);
				options.set3D(true);

				ColumnChart viz = new ColumnChart(createTable(), options);

				setWidget(1, 1, viz);

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
