package swingPanel;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import enums.Constants;
import enums.ErrorNotification;
import gui.BackgroundPanel;
import model.InfoModel;
import panel.ErrorPanel;
import panel.SwingPanel;

public class SwingErrorPanel extends SwingPanel implements ErrorPanel {

	private BackgroundPanel container;
	private JPanel statusPanel;
	private Color panelColor;
	private JLabel statusLabel;
	private JLabel currentShow;
	private JPanel panel;
	private JPanel errorPanel;
	private JPanel buttonPanel;
	private JTextField[] textFields;
	private JButton[] buttons;
	private JLabel errorText;
	private JButton add;

	public SwingErrorPanel() {
		container = new BackgroundPanel();
		container.setStatusPanelVisibility(true);
		panel = container.getPanel();
		JPanel flowStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowStatus.setOpaque(false);
		JPanel flowError = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowError.setOpaque(false);

		JPanel flowButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		flowButton.setOpaque(false);
		statusPanel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(flowStatus);
		flowStatus.add(statusPanel);
		initStatusPanel();

		panel.add(Box.createVerticalStrut(20));

		errorPanel = new JPanel();
		panel.add(flowError);
		flowError.add(errorPanel);
		initErrorPanel();

		buttonPanel = new JPanel();
		panel.add(flowButton);
		flowButton.add(buttonPanel);
		initButtonPanel();

		panel.add(Box.createVerticalStrut(30));
		
		errorPanel.setVisible(false);
		buttonPanel.setVisible(false);
		
		container.repaint();
		container.revalidate();
	}

	private void initButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setPreferredSize(new Dimension(800, 30));

		JButton cont = new JButton("Continue");
		JButton quit = new JButton("Quit");
		add = new JButton("Add Word");

		double width = Math.max(Math.max(cont.getMinimumSize().getWidth(), quit.getMinimumSize().getWidth()),
				add.getMinimumSize().getWidth());

		buttonPanel.add(adjustWidth(cont, width));
		buttonPanel.add(adjustWidth(quit, width));
		buttonPanel.add(adjustWidth(add, width));
		
		cont.addActionListener(e -> {
			setChanged();
			notifyObservers(ErrorNotification.CONTINUE);
		});
		
		quit.addActionListener(e -> {
			setChanged();
			notifyObservers(ErrorNotification.QUIT);
		});
		

		add.addActionListener(e -> {
			add.setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.ADD);
		});
	}

	private JComponent adjustWidth(JComponent component, double width) {
		component.setMinimumSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		component.setPreferredSize(new Dimension((int) width, (int) component.getPreferredSize().getHeight()));
		return component;
	}

	private void initErrorPanel() {
		errorPanel.setPreferredSize(new Dimension(800, 250));
		errorPanel.setOpaque(false);
		errorPanel.setBackground(panelColor);
		errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));

		errorText = new JLabel("Error Message");
		errorText.setOpaque(false);
		errorText.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 20));
		errorText.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorPanel.add(errorText);

		textFields = new JTextField[3];
		buttons = new JButton[6];
		for (int i = 0; i < 3; i++)
			initFieldAndButtons(i);

		buttons[Constants.BEFORE].addActionListener(e -> {
			buttons[Constants.BEFORE].setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.RENAME_BEFORE);
		});
		
		buttons[Constants.BEFORE+3].addActionListener(e -> {
			buttons[Constants.BEFORE+3].setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.DELETE_BEFORE);
		});
		
		buttons[Constants.CURRENT].addActionListener(e -> {
			buttons[Constants.CURRENT].setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.RENAME_CURRENT);
		});
		
		buttons[Constants.CURRENT+3].addActionListener(e -> {
			buttons[Constants.CURRENT+3].setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.DELETE_CURRENT);
		});
		
		buttons[Constants.AFTER].addActionListener(e -> {
			buttons[Constants.AFTER].setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.RENAME_AFTER);
		});
		
		buttons[Constants.AFTER+3].addActionListener(e -> {
			buttons[Constants.AFTER+3].setForeground(Color.BLUE);
			setChanged();
			notifyObservers(ErrorNotification.DELETE_AFTER);
		});
	}

	private void initFieldAndButtons(int position) {
		JPanel flowTextField = new JPanel(new FlowLayout(FlowLayout.CENTER));
		textFields[position] = new JTextField();
		flowTextField.add(textFields[position]);
		flowTextField.setOpaque(false);
		JPanel flowButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons[position] = new JButton("Rename");
		buttons[position + 3] = new JButton("Delete");
		flowButtons.setOpaque(false);
				
		double width = Math.max(buttons[position].getPreferredSize().getWidth(), buttons[position + 3].getPreferredSize().getWidth());
		flowButtons.add(adjustWidth(buttons[position], width));
		flowButtons.add(adjustWidth(buttons[position + 3], width));
		textFields[Constants.BEFORE].setText("flowTextField");
		errorPanel.add(flowTextField);
		errorPanel.add(flowButtons);
		
		

	}

	private void setButtonColor(JButton button, Color color){
		button.setForeground(color);
	}
	private void initStatusPanel() {
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(800, 70));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel("Error");
		statusLabel.setOpaque(false);
		statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 28));
		statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		statusPanel.add(statusLabel);

		currentShow = new JLabel();
		currentShow.setOpaque(false);
		currentShow.setFont(new Font(statusLabel.getFont().getName(), Font.PLAIN, 20));
		currentShow.setAlignmentX(Component.CENTER_ALIGNMENT);
		statusPanel.add(currentShow);

	}
	
	
	

	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof ErrorNotification) {
			InfoModel model = (InfoModel) o;
			switch ((ErrorNotification) arg) {
			case UPDATE:
				updateStatus(model);
				container.revalidate();
				container.repaint();
				break;
			case ERRORFOUND:
				updateErrorPanel(model);
				container.revalidate();
				container.repaint();
				break;
			case ADD:
				model.addAction(ErrorNotification.ADD);
				break;
			case CONTINUE:
				model.addAction(ErrorNotification.CONTINUE);
				errorPanel.setVisible(false);
				buttonPanel.setVisible(false);
				container.setMiddlePanelBackgroundVisibility(false);
				container.revalidate();
				container.repaint();
				resetButtons();
				break;
			case DELETE_AFTER:
				model.addAction(ErrorNotification.DELETE_AFTER);
				break;
			case DELETE_BEFORE:
				model.addAction(ErrorNotification.DELETE_BEFORE);
				break;
			case DELETE_CURRENT:
				model.addAction(ErrorNotification.DELETE_CURRENT);
				break;
			case RENAME_AFTER:
				model.setAfterName(getAfterName());
				break;
			case RENAME_BEFORE:
				model.setBeforeName(getBeforeName());
				break;
			case RENAME_CURRENT:
				model.setCurrentName(getCurrentName());
				break;
			default:
				break;
				
				
			}
		}
		container.repaint();
		container.revalidate();
	}
	
	private void resetButtons(){
		for(JButton button : buttons)
			setButtonColor(button, Color.BLACK);
		setButtonColor(add, Color.BLACK);
	}

	private void updateErrorPanel(InfoModel model) {
		errorPanel.setVisible(true);
		buttonPanel.setVisible(true);
		container.setMiddlePanelBackgroundVisibility(true);
		errorText.setText(model.getErrorMessage());
		for (int i = 0; i < 3; i++) {
			textFields[i].setText(model.getErrorNames()[i]);
		}
		
		add.setEnabled(model.isNewWord());
		
		double width = Math.max(Math.max(0, textFields[Constants.BEFORE].getText().length()),
				Math.max(textFields[Constants.CURRENT].getText().length(), textFields[Constants.AFTER].getText().length()));
		for (int i = 0; i < 3; i++) {
			adjustWidth(textFields[i], width*8);
		}
		
	}

	public void updateStatus(InfoModel model) {
		currentShow.setText(model.getShowText());
		statusLabel.setText(model.getStatusText());
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

	@Override
	public String getBeforeName() {
		return textFields[Constants.BEFORE].getText();
	}

	@Override
	public String getCurrentName() {
		return textFields[Constants.CURRENT].getText();
	}

	@Override
	public String getAfterName() {
		return textFields[Constants.AFTER].getText();
	}

}
