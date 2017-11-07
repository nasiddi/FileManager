package swingPanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import enums.Constants;
import enums.RenameNotification;
import gui.BackgroundPanel;
import gui.NavButton;
import panel.RenamePanel;
import panel.SwingPanel;

public class SwingRenamePanel extends SwingPanel implements RenamePanel {

	private BackgroundPanel container;
	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel currentShow;
	private JPanel panel;
	private JPanel entryPanel;
	private JPanel buttonPanel;
	private JTextField seriesName;
	private JTextField separationSymbol;
	private String statusTag ="";
	
	public SwingRenamePanel() {
		container = new BackgroundPanel();
		container.setStatusPanelVisibility(true);
		container.setMiddlePanelBackgroundVisibility(true);
		panel = container.getPanel();
		JPanel flowStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowStatus.setOpaque(false);
		JPanel flowEntry = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowEntry.setOpaque(false);

		JPanel flowButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowButton.setOpaque(false);
		statusPanel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(flowStatus);
		flowStatus.add(statusPanel);
		initStatusPanel();

		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/25));

		entryPanel = new JPanel();
		panel.add(flowEntry);
		flowEntry.add(entryPanel);
		initEntryPanel();

		buttonPanel = new JPanel();
		panel.add(flowButton);
		flowButton.add(buttonPanel);
		initButtonPanel();

		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/14));
		
		
		container.repaint();
		container.revalidate();
	}

	private void initButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT/13));

		JButton confirm = new NavButton("Confirm");
		JButton quit = new NavButton("Quit");

		double width = Math.max(confirm.getMinimumSize().getWidth(), quit.getMinimumSize().getWidth());

		buttonPanel.add(adjustWidth(confirm, width));
		buttonPanel.add(adjustWidth(quit, width));
		
		confirm.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.CONFIRM_ENTRY);
		});
		
		quit.addActionListener(e -> {
			setChanged();
			notifyObservers(RenameNotification.QUIT);
		});
		
	}

	private JComponent adjustWidth(JComponent component, double width) {
		component.setMinimumSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		component.setPreferredSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		return component;
	}
	
	public String[] getRenameInfo(){
		String[] info = new String[3];
		info[0] = seriesName.getText();
		info[1] = separationSymbol.getText();
		info[2] = statusTag;
		return info;
		
	}

	private void initEntryPanel() {
		entryPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, (int) (Constants.FRAMEHEIGHT/2)));
		entryPanel.setOpaque(false);
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));

		entryPanel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/5));
		seriesName = new JTextField();
		separationSymbol = new JTextField();
		initTextField(seriesName, "Series Name");
		initTextField(separationSymbol, "Seperation Symbols");
		
		seriesName.addFocusListener(new FocusAll());
		separationSymbol.addFocusListener(new FocusAll());
		initRadioButtons();
		entryPanel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT/5));
		}

	private void initRadioButtons() {
		JPanel flowRadioPanel = new JPanel();
		flowRadioPanel.setOpaque(false);
		JRadioButton ongoing = new JRadioButton("Ongoing");
		JRadioButton hiatus = new JRadioButton("Hiatus");
		JRadioButton ended = new JRadioButton("Ended");
		ongoing.setOpaque(false);
		hiatus.setOpaque(false);
		ended.setOpaque(false);

		ongoing.setFont(Constants.BOXFONT);
		hiatus.setFont(Constants.BOXFONT);
		ended.setFont(Constants.BOXFONT);
		flowRadioPanel.add(ongoing);
		flowRadioPanel.add(hiatus);
		flowRadioPanel.add(ended);

		ongoing.addActionListener(e -> {
			statusTag = "OG";
			hiatus.setSelected(false);
			ended.setSelected(false);
		});
		
		hiatus.addActionListener(e -> {
			statusTag = "LS";
			ongoing.setSelected(false);
			ended.setSelected(false);
		});
		
		ended.addActionListener(e -> {
			statusTag = "FN";
			hiatus.setSelected(false);
			ongoing.setSelected(false);
		});
		entryPanel.add(flowRadioPanel);
	}

	private void initTextField(JTextField textField, String name) {
		JPanel flowTextField = new JPanel(new FlowLayout(FlowLayout.CENTER));
		textField.setText(name);
		textField.setFont(Constants.BOXFONT);
		adjustWidth(textField, Constants.FRAMEHEIGHT);
		flowTextField.add(textField);
		flowTextField.setOpaque(false);
				
		entryPanel.add(flowTextField);
		
		

	}
	
	private void initStatusPanel() {
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, (int) (Constants.FRAMEHEIGHT/5.7)));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel("Batch Rename");
		statusLabel.setOpaque(false);
		statusLabel.setFont(Constants.BIGFONT);
		statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		statusPanel.add(statusLabel);

		currentShow = new JLabel();
		currentShow.setOpaque(false);
		currentShow.setFont(Constants.SMALLFONT);
		currentShow.setAlignmentX(Component.CENTER_ALIGNMENT);
		statusPanel.add(currentShow);

	}
	
	
	

	@Override
	public void update(Observable o, Object arg) {
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
		entryPanel.removeAll();
		initEntryPanel();
	}

}

class FocusAll extends FocusAdapter {
	@Override
	public void focusGained(FocusEvent fEvt) {
		JTextField component = (JTextField) fEvt.getSource();
		component.selectAll();
	}
}
