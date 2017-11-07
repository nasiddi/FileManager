package swingPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicComboBoxUI;

import enums.Constants;
import enums.ShowStatus;
import model.ShowInfoFields;

class ShowJPanelFactory {

	static ShowInfoFields createShowJPanel(ShowInfoFields fields, SwingUpdatePanel updatePanel) {
		JPanel showPanel = new JPanel();
		showPanel.setMaximumSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 5));
		showPanel.setOpaque(false);
		showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.Y_AXIS));
		showPanel.add(Box.createVerticalGlue());
		initShowFields(fields, showPanel, updatePanel);

		showPanel.add(addBar());
		fields.setShowPanel(showPanel);
		showPanel.add(Box.createVerticalGlue());
		return fields;

	}

	private static JPanel addBar() {
		JPanel bar = new JPanel();
		bar.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 80));
		bar.setBackground(new Color(255, 255, 255, 70));
		return bar;
	}

	private static void initShowFields(ShowInfoFields fields, JPanel showPanel, SwingUpdatePanel updatePanel) {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		((FlowLayout) topPanel.getLayout()).setHgap(Constants.FRAMEWIDTH / 60);

		JTextField showName = new JTextField();
		showName.setFont(Constants.BOXFONT);
		showName.setPreferredSize(
				new Dimension((int) (Constants.FRAMEWIDTH / 3.5), (int) showName.getMinimumSize().getHeight()));

		JTextField seasonNR = new JTextField();
		JTextField shortName = new JTextField();
		shortName.setFont(Constants.BOXFONT);
		seasonNR.setFont(Constants.BOXFONT);
		seasonNR.setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH / 32, (int) seasonNR.getMinimumSize().getHeight()));
		shortName.setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH / 32, (int) shortName.getMinimumSize().getHeight()));

		BasicComboBoxUI ui = new BasicComboBoxUI() {
			protected JButton createArrowButton() {
				return new JButton() {
					public int getWidth() {
						return 0;
					}
				};
			}
		};

		JComboBox<ShowStatus> status = new JComboBox<ShowStatus>();
		status.addItem(ShowStatus.INSEASON);
		status.addItem(ShowStatus.HIATUS);
		status.addItem(ShowStatus.ENDED);
		status.addItem(ShowStatus.NONE);
		status.setFont(Constants.BOXFONT);
		status.setUI(new BasicComboBoxUI() {
			protected JButton createArrowButton() {
				return new JButton() {
					public int getWidth() {
						return 0;
					}
				};
			}
		});
		status.setBackground(Color.WHITE);
		JComboBox<String> noName = new JComboBox<String>();
		noName.addItem("Episode Name: No");
		noName.addItem("Episode Name: Yes");
		noName.setSelectedIndex(1);
		noName.setFont(Constants.BOXFONT);
		noName.setBackground(Color.WHITE);
		noName.setUI(ui);
		topPanel.add(showName);
		topPanel.add(shortName);
		topPanel.add(seasonNR);
		topPanel.add(status);
		topPanel.add(noName);

		fields.setSeriesNameField(showName);
		fields.setShortNameField(shortName);
		fields.setSeasonField(seasonNR);
		fields.setStatusBox(status);
		fields.setNoNameBox(noName);

		topPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 14));
		topPanel.setMaximumSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 14));
		topPanel.setMinimumSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 14));

		topPanel.setOpaque(false);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		((FlowLayout) bottomPanel.getLayout()).setHgap(Constants.FRAMEWIDTH / 80);

		JScrollPane bottomScroll = new JScrollPane(bottomPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		bottomScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		bottomScroll.getVerticalScrollBar().setVisible(false);
		bottomScroll.getViewport().setOpaque(false);
		bottomScroll.setOpaque(false);
		bottomScroll.setBorder(null);
		bottomScroll.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 14));
		bottomScroll.setMinimumSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 14));
		bottomScroll.setMaximumSize(new Dimension(100000, Constants.FRAMEHEIGHT / 14));

		bottomScroll.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isShiftDown()) {
					bottomScroll.getParent().dispatchEvent(e);
					bottomScroll.setWheelScrollingEnabled(false);
				} else {
					bottomScroll.setWheelScrollingEnabled(true);
				}
			}
		});

		for (int i = 0; i < 30; i++) {
			JTextField e = initEpisodeField(fields, updatePanel);
			if (i == 0) {
				e.setEditable(false);
				e.setForeground(Color.DARK_GRAY);
			}
			bottomPanel.add(e);
			fields.addEpisodeField(e);
		}
		showPanel.add(topPanel);
		showPanel.add(bottomScroll);
	}

	private static JTextField initEpisodeField(ShowInfoFields fields, SwingUpdatePanel updatePanel) {
		JTextField field = new JTextField();
		field.setFont(Constants.BOXFONT);
		field.setPreferredSize(new Dimension(Constants.FRAMEWIDTH / 3, (int) field.getMinimumSize().getHeight()));

		field.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				field.setBackground(Color.WHITE);
			}

			@Override
			public void focusGained(FocusEvent e) {
				Rectangle rect = field.getBounds();
				((JScrollPane) ((JComponent) ((JComponent) field.getParent()).getParent()).getParent())
						.getHorizontalScrollBar().setValue(rect.x - Constants.FRAMEWIDTH / 3);
				field.setCaretPosition(field.getText().length());
				field.setBackground(new Color(186, 239, 252));
			}
		});

		MoveDown altDown = new MoveDown();
		MoveUp altUp = new MoveUp();
		altDown.panel = updatePanel;
		altUp.panel = updatePanel;

		field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke.getKeyStroke("alt DOWN")), "move down");
		field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("alt UP"), "move up");
		field.getActionMap().put("move down", altDown);
		field.getActionMap().put("move up", altUp);

		return field;
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