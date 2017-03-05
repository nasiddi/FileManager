package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import enums.Constants;

public class BackgroundPanel extends JPanel {

	private JPanel panel;
	private JPanel errorPanel;
	private JLabel bg;
	private JPanel statusPanel;
	private JPanel syncPanel;

	public BackgroundPanel(){
		
		this.setLayout(null);
		bg = AssetLoader.getBackground();
		bg.setSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT));

		Color panelColor = new Color(255, 255, 255, 70);
		
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT));
		
		statusPanel = new JPanel();
		statusPanel.setSize(new Dimension(800, 70));
		statusPanel.setLocation(0, 0);
		statusPanel.setOpaque(true);
		statusPanel.setBackground(panelColor);
		statusPanel.setVisible(false);
		
		syncPanel = new JPanel();
		syncPanel.setSize(new Dimension(800, 100));
		syncPanel.setLocation(0, 0);
		syncPanel.setOpaque(true);
		syncPanel.setBackground(panelColor);
		syncPanel.setVisible(false);
		
		errorPanel = new JPanel();
		errorPanel.setSize(new Dimension(800, 250));
		errorPanel.setLocation(0, 90);
		errorPanel.setOpaque(true);
		errorPanel.setBackground(panelColor);
		errorPanel.setVisible(false);
		
		
		
		this.add(panel);
		this.add(statusPanel);
		this.add(syncPanel);
		this.add(errorPanel);
		this.add(bg);
	}
	
	public void setMiddlePanelBackgroundVisibility(boolean visible){
		errorPanel.setVisible(visible);
	}
	
	public void setStatusPanelVisibility(boolean visible){
		statusPanel.setVisible(visible);
	}
	
	public void setSyncPanelVisibility(boolean visible){
		syncPanel.setVisible(visible);
	}
	
	public JPanel getPanel(){
		return panel;
	}
}
