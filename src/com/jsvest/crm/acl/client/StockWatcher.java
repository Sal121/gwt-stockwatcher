package com.jsvest.crm.acl.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StockWatcher implements EntryPoint {

	private ContextImpl ctx = new ContextImpl();

	private SimplePanel mainPanel = new SimplePanel();


	public void onModuleLoad() {
		StockPriceServiceAsync stockService = GWT.create(StockPriceService.class);
		StockWatcherConstants constants = GWT.create(StockWatcherConstants.class);
		StockWatcherMessages messages = GWT.create(StockWatcherMessages.class);
		
		ctx.setStockService(stockService);
		ctx.setConstants(constants);
		ctx.setMessages(messages);

		Window.setTitle(ctx.constants().stockWatcher());

		DockPanel appPanel = new DockPanel();
		appPanel.setSize("100%", "100%");
		appPanel.add(createMenu(), DockPanel.NORTH);
		mainPanel.setSize("100%", "100%");
		appPanel.add(mainPanel, DockPanel.CENTER);
		appPanel.setCellWidth(mainPanel, "100%");
		appPanel.setCellHeight(mainPanel, "100%");
		Label copyright = new Label("@CopyRight guoyou invest company");
		appPanel.add(copyright, DockPanel.SOUTH);
		appPanel.setCellHorizontalAlignment(copyright, HasHorizontalAlignment.ALIGN_CENTER);
		appPanel.setCellVerticalAlignment(copyright, HasVerticalAlignment.ALIGN_BOTTOM);
		appPanel.setCellHeight(copyright, "1%");
		RootPanel.get("app").add(appPanel);

	}

	private MenuBar createMenu() {
		// Make a command that we will execute from all leaves.
		Command cmd1 = new Command() {
			public void execute() {
				mainPanel.clear();
				//chartPanel.clear();
				mainPanel.add(new ChartPanel(ctx));
			}
		};

		Command cmd2 = new Command() {
			public void execute() {
				mainPanel.clear();
				mainPanel.add(new StockPanel(ctx));
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

}
