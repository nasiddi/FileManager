package swingPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import enums.Constants;
import enums.LoadingNotification;
import gui.BackgroundPanel;
import gui.NavButton;
import model.LoadingModel;
import panel.LoadingPanel;
import panel.SwingPanel;

public class SwingLoadingPanel extends SwingPanel implements LoadingPanel{

	private BackgroundPanel container;
	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel currentShow;
	private JPanel panel;
	private JPanel buttonPanel;
	
	public SwingLoadingPanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		container.setStatusPanelVisibility(true);
		statusPanel = new JPanel();
		panel.add(statusPanel, BorderLayout.NORTH);
		initStatusPanel();
		buttonPanel = new JPanel();
		panel.add(buttonPanel, BorderLayout.SOUTH);
		initButtonPanel();
		setChanged();
		notifyObservers(LoadingNotification.UPDATE);
	}
	
	private void initStatusPanel(){
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, (int) (Constants.FRAMEHEIGHT/5.7)));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel();
		currentShow = new JLabel();
		currentShow.setText("");
		statusLabel.setOpaque(false);
		currentShow.setOpaque(false);
		statusLabel.setFont(Constants.BIGFONT);
		statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		currentShow.setFont(Constants.SMALLFONT);
		currentShow.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		statusPanel.add(statusLabel);
		statusPanel.add(currentShow);
		
	}
	
	private void initButtonPanel(){
		buttonPanel.setOpaque(false);
		buttonPanel.setVisible(false);
		buttonPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/4));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton retry = new NavButton("Retry");
		JButton menu = new NavButton("Main Menu");
		buttonPanel.add(Box.createHorizontalGlue());
		
		
		retry.addActionListener(e -> {
			setChanged();
			notifyObservers(LoadingNotification.INIT);
		});
		
		menu.addActionListener(e -> {
			setChanged();
			notifyObservers(LoadingNotification.MAINMENU);
		});
		
		double width = Math.max(retry.getMinimumSize().getWidth(), menu.getMinimumSize().getWidth());

		buttonPanel.add(adjustWidth(retry, width));
		buttonPanel.add(adjustWidth(menu, width));
		buttonPanel.add(Box.createHorizontalGlue());

	}
	
	private JComponent adjustWidth(JComponent component, double width) {
		component.setMinimumSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		component.setPreferredSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		return component;
	}
	
	@Override
	public void update(Observable o, Object arg) {

		if(arg instanceof LoadingNotification){
			LoadingModel model = (LoadingModel) o;
			switch((LoadingNotification) arg){
			case ERROR:
				buttonPanel.setVisible(true);
				updateStatus(model);
				break;
			case UPDATE:
				updateStatus(model);
				buttonPanel.setVisible(false);
				break;			
			default:
				break;
			
			}
		}

	}

	private void updateStatus(LoadingModel model) {
		currentShow.setText(model.getShowText());
		statusLabel.setText(model.getStatusText());
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
