package swingPanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import enums.Constants;
import enums.ShowStatus;
import enums.UpdateNameNotification;
import gui.BackgroundPanel;
import gui.NavButton;
import model.ShowInfoFields;
import panel.SwingPanel;
import panel.UpdatePanel;

public class SwingUpdatePanel extends SwingPanel implements UpdatePanel {

	private BackgroundPanel container;
	private JPanel panel;
	private JScrollPane mainScrollPane;
	private JPanel mainView;
	private ArrayList<ShowInfoFields> fieldList;
	private JPanel buttonPanel;
	private ArrayList<ShowInfoFields> displayedList;

	public SwingUpdatePanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		container.setStatusPanelVisibility(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		mainView = new JPanel();
		mainView.setOpaque(false);
		mainScrollPane = new JScrollPane(mainView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		panel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isShiftDown()) {
					mainScrollPane.setWheelScrollingEnabled(true);

				} else
					mainScrollPane.setWheelScrollingEnabled(false);

			}
		});
		mainScrollPane.setOpaque(false);
		panel.add(mainScrollPane);
		mainView.setLayout(new BoxLayout(mainView, BoxLayout.Y_AXIS));
		mainScrollPane.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/5*4));
		mainScrollPane.getViewport().setOpaque(false);
		mainScrollPane.setBorder(null);
		buttonPanel = new JPanel();
		
		initButtonPanel();
		
		panel.add(buttonPanel);
	}

	private void initButtonPanel() {
		JPanel flow = new JPanel();
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		box.setOpaque(false);
		buttonPanel.setOpaque(false);
		box.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/5));
		flow.setLayout(new FlowLayout());
		((FlowLayout) flow.getLayout()).setHgap(Constants.FRAMEWIDTH / 25);
		flow.setOpaque(false);
		
		NavButton mainMenu = new NavButton("Main Menu");
		NavButton inseason = new NavButton("In Season");
		NavButton showAll = new NavButton("Show All");
		NavButton showHiatus = new NavButton("Show Hiatus");

		ArrayList<NavButton> buttons = new ArrayList<NavButton>();
		buttons.add(inseason);
		buttons.add(mainMenu);
		buttons.add(showHiatus);
		buttons.add(showAll);
		
		NavButton.evenButtonWidth(buttons);
		mainMenu.addActionListener(l -> {
			setChanged();
			notifyObservers(UpdateNameNotification.MAINMENU);
		});

		inseason.addActionListener(l -> {
			setChanged();
			notifyObservers(ShowStatus.INSEASON);

		});
		
		showHiatus.addActionListener(l -> {
			setChanged();
			notifyObservers(ShowStatus.HIATUS);

		});

		showAll.addActionListener(l -> {
			setChanged();
			notifyObservers(ShowStatus.ENDED);
		});

		flow.add(mainMenu);
		flow.add(inseason);
		flow.add(showHiatus);
		flow.add(showAll);
		box.add(Box.createVerticalGlue());
		box.add(flow);
		box.add(Box.createVerticalGlue());
		buttonPanel.add(box);
	}

	void scroll(int direction) {
		Component focused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		Container showPanel = focused.getParent().getParent().getParent().getParent();
		String name = showPanel.getName();
		int index = Integer.parseInt(name);
		ShowInfoFields fields = displayedList.get(index);
		if (index + 1 * direction < 0 || index + 1 * direction > displayedList.size() - 1)
			return;
		ShowInfoFields sF = displayedList.get(displayedList.indexOf(fields) + 1 * direction);
		sF.requestFocus();
		int position = mainScrollPane.getVerticalScrollBar().getValue();

		fields.getAllData();
		mainScrollPane.getVerticalScrollBar()
				.setValue(position + fields.getShowPanel().getHeight() * direction);
	}

	public ArrayList<ShowInfoFields> getNewFieldList(int count) {
		fieldList = new ArrayList<ShowInfoFields>();
		fieldList.clear();

		for (int i = 0; i < count; i++) {
			ShowInfoFields showPanel = ShowJPanelFactory.createShowJPanel(new ShowInfoFields(), this);
			fieldList.add(showPanel);
			mainView.add(showPanel.getShowPanel());

		}
		return fieldList;
	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof UpdateNameNotification) {
			switch ((UpdateNameNotification) arg) {
			case UPDATE:
				display();
				break;
			case MAINMENU:
				mainView.removeAll();
				mainScrollPane.getVerticalScrollBar().setValue(0);
				fieldList.clear();
				displayedList.clear();
			default:
				break;

			}
		}
		container.revalidate();
		container.repaint();
	}

	private void display() {

		displayedList = new ArrayList<ShowInfoFields>();
		displayedList.clear();
		int i = 0;
		for (ShowInfoFields fields : fieldList) {
			fields.getShowPanel().setVisible(fields.isVisible());
			if (fields.isVisible()){
				fields.setIndex(i++);
				displayedList.add(fields);
			}
		}
		mainScrollPane.getVerticalScrollBar().setValue(0);
	}
	
	public ArrayList<ShowInfoFields> getShowFieldList() {
		return fieldList;
	}


	@Override
	public void addToContainer(Container component) {
		component.add(container);

	}

	@Override
	public void removeFromContainer(Container component) {
		component.remove(container);
	}

	@Override
	public void reset() {
	}

}