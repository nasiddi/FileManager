package swingPanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import enums.RenameNotification;
import gui.BackgroundPanel;
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
	private JButton left;
	private Component box;

	public SwingRenameInfoPanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		container.setStatusPanelVisibility(true);
		statusPanel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(statusPanel);
		initStatusPanel();
		renamePanel = new JPanel();
		panel.add(Box.createVerticalStrut(10));

		box = Box.createVerticalStrut(250);
		panel.add(box);
		
		panel.add(renamePanel);
		renamePanel.setOpaque(false);
		initRenamePanel();

		JPanel flowButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowButton.setOpaque(false);
		buttonPanel = new JPanel();
		panel.add(flowButton);
		panel.add(Box.createVerticalStrut(25));

		flowButton.add(buttonPanel);
		initButtonPanel();

	}

	private void initButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setPreferredSize(new Dimension(800, 30));

		left = new JButton("Confirm");
		JButton right = new JButton("Quit");

		double width = Math.max(left.getMinimumSize().getWidth(), right.getMinimumSize().getWidth());

		buttonPanel.add(adjustWidth(left, width));
		buttonPanel.add(adjustWidth(right, width));

		left.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.CONFIRM_RENAME);
		});

		right.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.QUIT);
		});

	}

	private JComponent adjustWidth(JComponent component, double width) {
		component.setMinimumSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		component.setPreferredSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		return component;
	}

	public void setOverview(String overview) {
		renameDataField.setText(overview);
		renameDataField.setCaretPosition(0);
	}

	private void initRenamePanel() {
		renameDataField = new JTextArea();

		renameDataField.setEditable(false);
		JScrollPane scroll = new JScrollPane(renameDataField);
		scroll.setPreferredSize(new Dimension(760, 250));

		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		renamePanel.add(scroll);
		renamePanel.setVisible(false);
	}

	
	
	private void initStatusPanel() {
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(800, 70));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel("Batch Rename");
		currentShow = new JLabel();
		statusLabel.setOpaque(false);
		currentShow.setOpaque(false);
		statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 28));
		statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		currentShow.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 20));
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
		left.setText("Sync Now");
		left.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.SYNC);
		});
	}

	public void updateStatus(RenameModel model) {
		statusLabel.setText(model.getStatusText());
		currentShow.setText(model.getSeriesName());
		if (model.getRenameOverview() != null &&  model.getRenameOverview().length() > 0){
			renamePanel.setVisible(true);
			box.setVisible(false);
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
