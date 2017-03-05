package swingPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import enums.DataType;
import enums.LoadingNotification;
import enums.ShowStatus;
import enums.UpdateNameNotification;
import gui.BackgroundPanel;
import logic.Episode;
import logic.Series;
import model.InfoModel;
import panel.SwingPanel;
import panel.UpdatePanel;

public class SwingUpdatePanel extends SwingPanel implements UpdatePanel {

	private BackgroundPanel container;
	private JPanel panel;
	private JScrollPane mainScrollPane;
	private JPanel mainView;
	private ArrayList<ShowFields> fieldList;
	private JPanel buttonPanel;
	private InfoModel model;
	private final static int OG = 0;
	private final static int LS = 1;
	private final static int FN = 2;

	public SwingUpdatePanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		container.setStatusPanelVisibility(false);
		// statusPanel = new JPanel();
		// panel.add(statusPanel, BorderLayout.NORTH);
		// initStatusPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		mainView = new JPanel();
		mainView.setOpaque(false);
		mainScrollPane = new JScrollPane(mainView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		fieldList = new ArrayList<ShowFields>();
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
		// mainScrollPane.getVerticalScrollBar().setPreferredSize(new
		// Dimension(0, 0));
		panel.add(mainScrollPane);
		mainView.setLayout(new BoxLayout(mainView, BoxLayout.Y_AXIS));
		mainView.setPreferredSize(new Dimension(400, 10000));
		mainScrollPane.getViewport().setOpaque(false);
		mainScrollPane.setBorder(null);

		buttonPanel = new JPanel();
		panel.add(buttonPanel);
		initButtonPanel();
		initFocusListeners();

	}

	private void initButtonPanel() {
		JPanel flow = new JPanel();
		buttonPanel.add(flow);
		buttonPanel.setOpaque(false);
		flow.setPreferredSize(new Dimension(800, 200));
		flow.setLayout(new FlowLayout());
		flow.setOpaque(false);
		JButton mainMenu = new JButton("Main Menu");
		JButton showAll = new JButton("Show All");
		JButton showHiatus = new JButton("Show Hiatus");

		mainMenu.addActionListener(l -> {
			setChanged();
			notifyObservers(UpdateNameNotification.MAINMENU);
		});

		showHiatus.addActionListener(l -> {
			saveChangesAndClear();
			fillInData(1);
		});

		showAll.addActionListener(l -> {
			saveChangesAndClear();
			fillInData(2);
		});

		flow.add(mainMenu);
		flow.add(showHiatus);
		flow.add(showAll);
	}

	private void addBar() {
		JPanel bar = new JPanel();
		bar.setMaximumSize(new Dimension(800, 5));
		bar.setBackground(new Color(255, 255, 255, 70));
		mainView.add(bar);
	}

	private void initFocusListeners() {

	}

	private JPanel initRadioButtons(ShowStatus showStatus) {
		JPanel flowRadioPanel = new JPanel();
		flowRadioPanel.setOpaque(false);
		JRadioButton ongoing = new JRadioButton("In Season");
		JRadioButton hiatus = new JRadioButton("Hiatus");
		JRadioButton ended = new JRadioButton("Ended");
		flowRadioPanel.add(ongoing);
		flowRadioPanel.add(hiatus);
		flowRadioPanel.add(ended);

		ongoing.addActionListener(e -> {
			hiatus.setSelected(false);
			ended.setSelected(false);
		});

		hiatus.addActionListener(e -> {
			ongoing.setSelected(false);
			ended.setSelected(false);
		});

		ended.addActionListener(e -> {
			hiatus.setSelected(false);
			ongoing.setSelected(false);
		});

		switch (showStatus) {
		case ENDED:
			ended.setSelected(true);
			break;
		case HIATUS:
			hiatus.setSelected(true);
			break;
		case INSEASON:
			ongoing.setSelected(true);
			break;
		case NONE:
			break;
		default:
			break;

		}
		return flowRadioPanel;
	}

	private void setShowData(Series series, Series newNames) {
		if (newNames == null) {
			newNames = new Series(series.getSeriesName(), String.valueOf(series.getSeasonCount()), "&");
		}

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		JTextField showName = new JTextField(series.getSeriesName());
		showName.setPreferredSize(new Dimension(200, (int) showName.getMinimumSize().getHeight()));

		JLabel noName = new JLabel("Episodes with no Name");
		JRadioButton yes = new JRadioButton("Yes");
		JRadioButton no = new JRadioButton("No");

		yes.addActionListener(e -> {
			no.setSelected(false);
		});

		no.addActionListener(e -> {
			yes.setSelected(false);
		});

		if (newNames.hasEpisodesWithoutName())
			yes.setSelected(true);
		else
			no.setSelected(true);

		series.setHasEpisodesWithoutName(newNames.hasEpisodesWithoutName());
		ShowFields showFields = new ShowFields(series, showName);
		showFields.addNameRadioButtons(yes);
		JTextField seasonNR = new JTextField(series.getCurrentSeason().getSeasonNRasString());
		showFields.addSeason(seasonNR);
		namePanel.add(showName);
		namePanel.add(seasonNR);
		JPanel flowRadioButtons = initRadioButtons(series.getShowStatus());
		showFields.addRadioButtons(flowRadioButtons.getComponents());
		namePanel.add(flowRadioButtons);
		namePanel.add(noName);
		namePanel.add(yes);
		namePanel.add(no);
		mainView.add(namePanel);
		namePanel.setMinimumSize(new Dimension(800, 30));
		namePanel.setMaximumSize(new Dimension(800, 30));
		namePanel.setOpaque(false);

		JPanel show = new JPanel();
		show.setOpaque(false);
		JScrollPane showScroll = new JScrollPane(show, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		showScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		showScroll.getVerticalScrollBar().setVisible(false);
		mainView.add(showScroll);
		showScroll.getViewport().setOpaque(false);
		showScroll.setOpaque(false);
		showScroll.setBorder(null);
		showScroll.setMinimumSize(new Dimension(800, 30));
		showScroll.setMaximumSize(new Dimension(10000, 30));

		showScroll.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isShiftDown()) {
					showScroll.getParent().dispatchEvent(e);
					showScroll.setWheelScrollingEnabled(false);
				} else {
					showScroll.setWheelScrollingEnabled(true);
				}
			}
		});

		if (series.getSeriesName().equals("New Series")) {
			for (int e = 0; e < 30; e++) {
				showFields.addField(
						(addEpisodeField(showFields, (e < 10) ? "0" + e + " - " : e + " - ", show, Color.BLACK)));
			}
		} else {
			show.setLayout(new FlowLayout(FlowLayout.LEFT));
			addEpisodeField(showFields,
					series.getLastEpisode().getEpisodeNRasString() + " - " + series.getLastEpisode().getEpisodeName(),
					show, Color.DARK_GRAY);
			int eNR = series.getLastEpisode().getEpisodeNR();
			if (series.getShowStatus().equals(ShowStatus.HIATUS)) {
				eNR = -1;
				int sNR = series.getCurrentSeason().getSeasonNR() + 1;
				String stringNR = sNR + "";
				if (sNR < 10)
					stringNR = "0" + stringNR;
				seasonNR.setText(stringNR);
			}
			int i = eNR + 30;

			for (eNR++; eNR <= i; eNR++) {
				boolean found = false;
				if (newNames != null) {
					if (newNames.getCurrentSeason().getEpisdoes().containsKey(eNR)) {
						Episode e = newNames.getCurrentSeason().getEpisdoes().get(eNR);
						showFields.addField((addEpisodeField(showFields,
								e.getEpisodeNRasString() + " - " + e.getEpisodeName(), show, Color.BLUE)));
						found = true;
						continue;
					}

				}
				if (!found)
					showFields.addField((addEpisodeField(showFields, (eNR < 10) ? "0" + eNR + " - " : eNR + " - ", show,
							Color.BLACK)));
			}
		}

		fieldList.add(showFields);

	}

	private JTextField addEpisodeField(ShowFields showFields, String episode, JPanel panel, Color color) {
		JTextField field = new JTextField(episode);
		field.setPreferredSize(new Dimension(260, (int) field.getMinimumSize().getHeight()));
		field.setOpaque(false);
		field.setForeground(color);
		field.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				field.setBackground(Color.WHITE);
			}

			@Override
			public void focusGained(FocusEvent e) {
				Rectangle rect = field.getBounds();
				((JScrollPane) ((JComponent) ((JComponent) field.getParent()).getParent()).getParent())
						.getHorizontalScrollBar().setValue(rect.x - 260);
				field.setCaretPosition(field.getText().length());
				field.setBackground(new Color(186, 239, 252));
				container.revalidate();
				container.repaint();
			}
		});

		MoveDown altDown = new MoveDown();
		MoveUp altUp = new MoveUp();
		altDown.panel = this;
		altUp.panel = this;
		field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke.getKeyStroke("alt DOWN")), "move down");
		field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("alt UP"), "move up");
		field.getActionMap().put("move down", altDown);
		field.getActionMap().put("move up", altUp);

		panel.setName(fieldList.size() + "");
		panel.add(field);

		return field;
	}

	public void scroll(int direction) {
		Component focused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		String name = focused.getParent().getName();
		int index = Integer.parseInt(name);
		ShowFields showFields = fieldList.get(index);
		if (index + 1 * direction < 0 || index + 1 * direction > fieldList.size() - 1)
			return;
		ShowFields sF = fieldList.get(fieldList.indexOf(showFields) + 1 * direction);
		sF.fields.get(0).requestFocusInWindow();
		int position = mainScrollPane.getVerticalScrollBar().getValue();
		mainScrollPane.getVerticalScrollBar().setValue(position + 80 * direction);
	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof UpdateNameNotification) {
			model = (InfoModel) o;
			switch ((UpdateNameNotification) arg) {
			case INIT:
				fillInData(0);
				break;
			case MAINMENU:
				saveChangesAndClear();
				break;
			default:
				break;

			}
		}
		container.revalidate();
		container.repaint();
	}

	private void saveChangesAndClear() {
		saveChanges();
		mainView.removeAll();
		mainScrollPane.getVerticalScrollBar().setValue(0);
		fieldList.clear();
	}

	private void saveChanges() {
		ArrayList<String> newNames = new ArrayList<String>();
		for (ShowFields f : fieldList) {
			if (f.nameField.getText().equals("New Series"))
				continue;
			if (f.statusChanged() && model.getSeries().containsKey(f.seriesName)) {
				model.getSeries().get(f.seriesName).changeStatusData(DataType.STATUS, f.selected);
			}
			if (f.selected.equals("Ended"))
				continue;
			String symbol = (f.hasEpisodesWithNoName()) ? "%" : "&";
			String s = f.nameField.getText() + "#" + f.season.getText() + "#" + symbol;
			for (JTextField field : f.fields) {
				s += "#" + field.getText();
			}

			newNames.add(s);
		}
		Collections.sort(newNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}

		});

		model.saveNameChanges(newNames);

	}

	private void fillInData(int showing) {
		HashMap<String, Series> newNames = model.getNewEpisodeNames();
		for (Series s : model.getSeriesAsSortedList()) {
			ShowStatus status = s.getShowStatus();
			if (status.equals(ShowStatus.INSEASON) || (showing == 1 && status.equals(ShowStatus.HIATUS)
					|| (showing == 2))) {
				setShowData(s, newNames.get(s.getSeriesName()));
				addBar();
				newNames.remove(s.getSeriesName());
			}
		}
		for (Series n : newNames.values()) {
			setShowData(n, n);
		}

		for (int i = 0; i < 3; i++) {
			Series series = new Series("New Series", "01", "&");
			series.changeStatusData(DataType.STATUS, "NONE");
			series.setSeasonCount();
			setShowData(series, null);
		}
		mainView.setPreferredSize(new Dimension(400, fieldList.size() * 80));
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
		// TODO Auto-generated method stub

	}

}

class ShowFields {

	String original;
	JTextField nameField;
	ArrayList<JRadioButton> radioButtons;
	String seriesName;
	String selected = "";
	JTextField season;
	ArrayList<JTextField> fields;
	Series series;
	private JRadioButton yes;

	public ShowFields(Series series, JTextField field) {
		this.series = series;
		this.seriesName = series.getSeriesName();
		this.nameField = field;
		fields = new ArrayList<JTextField>();

	}

	public void addSeason(JTextField season) {
		this.season = season;
	}

	public void addField(JTextField textField) {
		fields.add(textField);
	}

	public void addNameRadioButtons(JRadioButton yes) {
		this.yes = yes;
	}

	public boolean hasEpisodesWithNoName() {
		return yes.isSelected();
	}

	public void addRadioButtons(Component[] components) {
		radioButtons = new ArrayList<JRadioButton>();
		for (Component c : components) {
			radioButtons.add((JRadioButton) c);
		}
		original = "";
		for (JRadioButton rb : radioButtons) {
			if (rb.isSelected()) {
				original = rb.getText();
				break;
			}
		}
	}

	public boolean statusChanged() {
		for (JRadioButton rb : radioButtons) {
			if (rb.isSelected()) {
				selected = rb.getText();
				return !original.equals(rb.getText());
			}
		}
		return false;
	}

}

class MoveDown extends AbstractAction {

	SwingUpdatePanel panel;

	@Override
	public void actionPerformed(ActionEvent e) {
		panel.scroll(1);

	}
}

class MoveUp extends AbstractAction {

	SwingUpdatePanel panel;

	@Override
	public void actionPerformed(ActionEvent e) {
		panel.scroll(-1);

	}

}
