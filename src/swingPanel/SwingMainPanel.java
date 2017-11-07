package swingPanel;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import enums.MainNotification;
import gui.BackgroundPanel;
import gui.RoundButton;
import panel.MainPanel;
import panel.SwingPanel;

public class SwingMainPanel extends SwingPanel implements MainPanel {

	private BackgroundPanel container;
	private JPanel panel;

	public SwingMainPanel() {
		container = new BackgroundPanel();
		panel = container.getPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		initButtons();
		container.revalidate();
		container.repaint();
	}

	private void initButtons() {

		panel.add(Box.createVerticalGlue());
		RoundButton sync = new RoundButton("Sync");
		sync.transferFocus();
		RoundButton error = new RoundButton("Error Search");
		RoundButton rename = new RoundButton("Batch Rename");
		RoundButton reload = new RoundButton("Reload");
		RoundButton statistic = new RoundButton("Statistic");
		RoundButton update = new RoundButton("Update");

		double width = Math.max(Math.max(sync.getMinimumSize().getWidth(), error.getMinimumSize().getWidth()),
				Math.max(rename.getMinimumSize().getWidth(), Math.max(reload.getMinimumSize().getWidth(), statistic.getMinimumSize().getWidth())));

		addButton(sync, width);
		addButton(error, width);
		addButton(rename, width);
		addButton(reload, width);
		addButton(statistic, width);
		addButton(update, width);
		
		
		panel.add(Box.createVerticalGlue());

		sync.addActionListener(e -> {
			setChanged();
			notifyObservers(MainNotification.SYNC);
		});

		error.addActionListener(e -> {
			setChanged();
			notifyObservers(MainNotification.ERROR);
		});

		rename.addActionListener(e -> {
			setChanged();
			notifyObservers(MainNotification.RENAME);
		});

		reload.addActionListener(e -> {
			setChanged();
			notifyObservers(MainNotification.RELOAD);
		});
		
		statistic.addActionListener(e -> {
			setChanged();
			notifyObservers(MainNotification.STATISTIC);
		});
		
		update.addActionListener(e -> {
			setChanged();
			notifyObservers(MainNotification.UPDATE_NAMES);
		});
	}

	private void addButton(RoundButton button, double width) {
		button.setWidth(width);
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(button);
		p.setOpaque(false);
		panel.add(p);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

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
