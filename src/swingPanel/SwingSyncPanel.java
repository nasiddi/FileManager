package swingPanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import enums.Constants;
import enums.SyncNotification;
import gui.BackgroundPanel;
import gui.NavButton;
import model.InfoModel;
import panel.SwingPanel;
import panel.SyncPanel;

public class SwingSyncPanel extends SwingPanel implements SyncPanel{

	private BackgroundPanel container;
	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel currentShow;
	private JPanel panel;
	private JLabel progress;
	private JPanel infoPanel;
	private JPanel buttonPanel;
	private JTextArea infoField;
	
	public SwingSyncPanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		container.setSyncPanelVisibility(true);
		statusPanel = new JPanel();
		panel.add(statusPanel);
		initStatusPanel();
		infoPanel = new JPanel();
		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/40));

		
		panel.add(infoPanel);
		infoPanel.setOpaque(false);
		initInfoPanel();

		JPanel flowButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowButton.setOpaque(false);
		buttonPanel = new JPanel();
		panel.add(flowButton);
		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/16));

		flowButton.add(buttonPanel);
		initButtonPanel();
		
		setChanged();
		notifyObservers(SyncNotification.UPDATE);
	}
	
	private void initStatusPanel(){
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/4));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel();
		currentShow = new JLabel();
		statusLabel.setOpaque(false);
		currentShow.setOpaque(false);
		statusLabel.setFont(Constants.BIGFONT);
		statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		currentShow.setFont(Constants.SMALLFONT);
		currentShow.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		progress = new JLabel();
		progress.setOpaque(false);
		progress.setFont(Constants.SMALLFONT);
		progress.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		statusPanel.add(statusLabel);
		statusPanel.add(currentShow);
		statusPanel.add(progress);
	}
	
	private void initButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/14));
		buttonPanel.setVisible(false);
		JButton menuButton = new NavButton("Main Menu");
		

		buttonPanel.add(menuButton);
		

		menuButton.addActionListener(e -> {
			setChanged();
			notifyObservers(SyncNotification.MAINMENU);
		});

	}

	

	public void setOverview(String overview) {
		infoField.setText(overview);
		infoField.setCaretPosition(0);
	}

	private void initInfoPanel() {
		infoField = new JTextArea();

		infoField.setEditable(false);
		infoField.setFont(Constants.BOXFONT);
		JScrollPane scroll = new JScrollPane(infoField);
		scroll.setPreferredSize(new Dimension((int) (Constants.FRAMEWIDTH/1.05), (int) (Constants.FRAMEHEIGHT/2)));

		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		infoPanel.add(scroll);
		infoPanel.setVisible(false);
	}
	
	private void displayInfos(String overview){
		setOverview(overview);
		statusLabel.setText("Sync Complete");
		currentShow.setText("");
		progress.setText("");
		infoPanel.setVisible(true);
		buttonPanel.setVisible(true);
	}
	@SuppressWarnings("incomplete-switch")
	@Override
	public void update(Observable o, Object arg) {

		if(arg instanceof SyncNotification){
			InfoModel model = (InfoModel) o;
			switch((SyncNotification) arg){
			case UPDATE:
				updateStatus(model);
				break;
			case DONE:
				displayInfos(model.getSyncOverView());
				break;
			}
		}

	}

	private void updateStatus(InfoModel model) {
		currentShow.setText(model.getShowText());
		statusLabel.setText(model.getStatusText());
		progress.setText(model.getProgress());
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
		buttonPanel.setVisible(false);
	}
}
