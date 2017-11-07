package swingPanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import enums.Constants;
import enums.RenameNotification;
import gui.BackgroundPanel;
import gui.NavButton;
import model.RenameModel;
import panel.RenameInfoPanel;
import panel.SwingPanel;

public class SwingRenameInfoPanel extends SwingPanel implements RenameInfoPanel {

	private BackgroundPanel container;
	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel currentShow;
	private JPanel panel;
	private JPanel renamePanel;
	private JTextArea renameDataField;
	private JPanel buttonPanel;
	private NavButton left;

	public SwingRenameInfoPanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		container.setStatusPanelVisibility(true);
		statusPanel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(statusPanel);
		initStatusPanel();
		renamePanel = new JPanel();

		panel.add( Box.createVerticalStrut((int) (Constants.FRAMEHEIGHT/10)));
		
		panel.add(renamePanel);
		renamePanel.setOpaque(false);
		initRenamePanel();
		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/20));

		JPanel flowButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowButton.setOpaque(false);
		buttonPanel = new JPanel();
		panel.add(flowButton);
		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/10));

		flowButton.add(buttonPanel);
		initButtonPanel();

	}

	private void initButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/15));

		left = new NavButton("Confirm");
		NavButton right = new NavButton("Quit");
		NavButton[] navs = new NavButton[2];
		navs[0] = left;
		navs[1] = right;
		NavButton.evenButtonWidth(navs);

		buttonPanel.add(left);
		buttonPanel.add(right);
		left.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.CONFIRM_RENAME);
		});

		right.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.QUIT);
		});

	}

	public void setOverview(String overview) {
		renameDataField.setText(overview);
		renameDataField.setCaretPosition(0);
	}

	private void initRenamePanel() {
		renameDataField = new JTextArea();
		renameDataField.setFont(Constants.BOXFONT);
		renameDataField.setEditable(false);
		JScrollPane scroll = new JScrollPane(renameDataField);
		scroll.setPreferredSize(new Dimension((int) (Constants.FRAMEWIDTH/1.05), (int) (Constants.FRAMEHEIGHT/1.7)));

		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		renamePanel.add(scroll);
		renamePanel.setVisible(false);
	}

	
	
	private void initStatusPanel() {
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, (int) (Constants.FRAMEHEIGHT/5.7)));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel("Batch Rename");
		currentShow = new JLabel();
		statusLabel.setOpaque(false);
		currentShow.setOpaque(false);
		statusLabel.setFont(Constants.BIGFONT);
		statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		currentShow.setFont(Constants.SMALLFONT);
		currentShow.setAlignmentX(Component.CENTER_ALIGNMENT);

		statusPanel.add(statusLabel);
		statusPanel.add(currentShow);

	}

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof RenameNotification) {
			RenameModel model = (RenameModel) o;
			switch ((RenameNotification) arg) {
			case DATA_COMPLIED:
				updateStatus(model);
			case CONFIRM_ENTRY:
				break;
			case CONFIRM_RENAME:
				break;
			case FINISHED:
				statusLabel.setText((model.getRenameSuccessful()) ? "Batch Rename Successful" : "Batch Rename Failed");
				changeLeftButton();
				left.setEnabled(model.getRenameSuccessful());
				break;
			case QUIT:
				break;
			default:
				break;

			}
		}

	}

	private void changeLeftButton() {

		for (ActionListener al : left.getActionListeners()) {
			left.removeActionListener(al);
		}
		left.setText("Sync");
		left.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.SYNC);
		});
	}

	private void updateStatus(RenameModel model) {
		statusLabel.setText(model.getStatusText());
		currentShow.setText(model.getSeriesName());
		if (model.getRenameOverview() != null &&  model.getRenameOverview().length() > 0){
			renamePanel.setVisible(true);
		}
			setOverview(model.getRenameOverview());

		container.revalidate();
		container.repaint();
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
