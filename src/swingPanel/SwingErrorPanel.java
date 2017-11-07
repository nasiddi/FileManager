package swingPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import enums.Constants;
import enums.ErrorNotification;
import gui.BackgroundPanel;
import gui.NavButton;
import logic.ErrorSearchThread;
import model.Error;
import panel.SwingPanel;

public class SwingErrorPanel extends SwingPanel {

	private BackgroundPanel container;
	private JPanel statusPanel;
	private Color panelColor;
	private JLabel statusLabel;
	private JLabel currentShow;
	private JPanel panel;
	private JPanel errorPanel;
	private JPanel buttonPanel;
	private JTextField current;
	private JTextField before;
	private JTextField after;

	private NavButton[] buttons;
	private JLabel errorText;
	private NavButton add;

	private ErrorSearchThread est;
	private NavButton exception;

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

		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT / 20));

		errorPanel = new JPanel();
		panel.add(flowError);
		flowError.add(errorPanel);
		initErrorPanel();

		buttonPanel = new JPanel();
		panel.add(flowButton);
		flowButton.add(buttonPanel);
		initButtonPanel();

		panel.add(Box.createVerticalStrut(Constants.FRAMEHEIGHT / 13));

		errorPanel.setVisible(false);
		buttonPanel.setVisible(false);

		container.repaint();
		container.revalidate();
	}

	private void initButtonPanel() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT / 13));

		NavButton cont = new NavButton("Continue");
		NavButton quit = new NavButton("Quit");
		add = new NavButton("Add Word");
		exception = new NavButton("Exception");

		ArrayList<NavButton> blist = new ArrayList<NavButton>();
		blist.add(quit);
		blist.add(cont);
		blist.add(add);
		blist.add(exception);

		NavButton.evenButtonWidth(blist);

		buttonPanel.add(cont);
		buttonPanel.add(quit);
		buttonPanel.add(add);
		buttonPanel.add(exception);

		
		cont.addActionListener(e -> {
			est.getError().setNewNames(before.getText(), current.getText(), after.getText());
			setChanged();
			update(null, ErrorNotification.CONTINUE);
			notifyObservers(ErrorNotification.CONTINUE);
		});

		quit.addActionListener(e -> {
			setChanged();
			notifyObservers(ErrorNotification.QUIT);
		});

		add.addActionListener(e -> {
			add.toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.ADD);
		});

		exception.addActionListener(e -> {
			exception.toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.EXCEPTION);

		});
	}

	private void initErrorPanel() {
		errorPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, (int) (Constants.FRAMEHEIGHT / 1.6)));
		errorPanel.setOpaque(false);
		errorPanel.setBackground(panelColor);
		errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));

		errorText = new JLabel("Error Message");
		errorText.setOpaque(false);
		errorText.setFont(Constants.SMALLFONT);
		errorText.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorPanel.add(errorText);

		buttons = new NavButton[6];
		for (int i = 0; i < 3; i++)
			initFieldAndButtons(i);

		NavButton.evenButtonWidth(buttons);
		
		
		buttons[Constants.BEFORE].addActionListener(e -> {
			buttons[Constants.BEFORE].toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.RENAME_BEFORE);
		});

		buttons[Constants.BEFORE + 3].addActionListener(e -> {
			buttons[Constants.BEFORE + 3].toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.DELETE_BEFORE);
		});

		buttons[Constants.CURRENT].addActionListener(e -> {
			buttons[Constants.CURRENT].toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.RENAME_CURRENT);
		});

		buttons[Constants.CURRENT + 3].addActionListener(e -> {
			buttons[Constants.CURRENT + 3].toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.DELETE_CURRENT);
		});

		buttons[Constants.AFTER].addActionListener(e -> {
			buttons[Constants.AFTER].toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.RENAME_AFTER);
		});

		buttons[Constants.AFTER + 3].addActionListener(e -> {
			buttons[Constants.AFTER + 3].toggleColor();
			est.getError().addOrRemoveAction(ErrorNotification.DELETE_AFTER);
		});
	}

	private void initFieldAndButtons(int position) {
		JPanel flowTextField = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JTextField textfield = new JTextField();
		textfield.setFont(Constants.BOXFONT);
		textfield.setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH * 6 / 10, (int) textfield.getPreferredSize().getHeight()));
		switch (position) {
		case Constants.BEFORE:
			before = textfield;
			flowTextField.add(before);
			break;
		case Constants.CURRENT:
			current = textfield;
			flowTextField.add(current);
			break;
		case Constants.AFTER:
			after = textfield;
			flowTextField.add(after);
			break;
		default:
			break;
		}

		flowTextField.setOpaque(false);
		JPanel flowButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons[position] = new NavButton("Rename");
		buttons[position + 3] = new NavButton("Delete");
		flowButtons.add(buttons[position]);
		flowButtons.add(buttons[position + 3]);
		flowButtons.setOpaque(false);

		flowTextField.add(flowButtons);
		errorPanel.add(flowTextField);

	}

	private void setButtonColor(JButton button, Color color) {
		button.setForeground(color);
	}

	private void initStatusPanel() {
		statusPanel.setOpaque(false);
		statusPanel.setPreferredSize(new Dimension(Constants.FRAMEWIDTH, (int) (Constants.FRAMEHEIGHT / 5.7)));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusLabel = new JLabel("Searching for Errors");
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

		if (arg instanceof ErrorNotification) {

			switch ((ErrorNotification) arg) {
			case UPDATE:
				container.revalidate();
				container.repaint();
				break;
			case NEW_WORD:
				add.setEnabled(true);
			case ERRORFOUND:
				est = (ErrorSearchThread) o;
				updateErrorPanel();
				container.revalidate();
				container.repaint();
				break;

			case CONTINUE:
				enableErrorPanel(false);
				container.revalidate();
				container.repaint();
				updateStatus();
				resetButtons();

				break;
			case RENAME_AFTER:
				break;
			case RENAME_BEFORE:
				break;
			case RENAME_CURRENT:
				break;
			default:
				break;

			}
		}
		container.repaint();
		container.revalidate();
	}

	private void resetButtons() {
		for (JButton button : buttons)
			setButtonColor(button, Color.BLACK);
		setButtonColor(add, Color.BLACK);
		setButtonColor(exception, Color.BLACK);
	}

	private void updateErrorPanel() {
		enableErrorPanel(true);
		Error error = est.getError();
		statusLabel.setText("Error Found");
		if(error.getNewWord() != null){
			add.setEnabled(true);
			exception.setEnabled(true);
		}else{
			add.setEnabled(false);
			exception.setEnabled(false);
		}
		currentShow.setText(error.getShow().getSeriesName());
		errorText.setText(error.getMessage());

		current.setText(error.getCurrent());
		before.setText(error.getBefore());
		after.setText(error.getAfter());

		current.setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH * 6 / 10, (int) current.getPreferredSize().getHeight()));
		before.setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH * 6 / 10, (int) before.getPreferredSize().getHeight()));
		after.setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH * 6 / 10, (int) after.getPreferredSize().getHeight()));
	}

	private void enableErrorPanel(boolean enable) {
		errorPanel.setVisible(enable);
		buttonPanel.setVisible(enable);
		container.setMiddlePanelBackgroundVisibility(enable);
	}

	private void updateStatus() {
		currentShow.setText("");
		statusLabel.setText("Searching for Errors");
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

	public ErrorSearchThread getErrorSearchThread() {
		return est;
	}
}
