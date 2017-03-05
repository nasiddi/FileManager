package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import panel.SwingPanel;
import gui.AssetLoader;

public class SwingFrame extends ClientFrame<SwingPanel> {

	private JFrame frame;
	private Container panel;

	public SwingFrame(String name) {
		super(name);
		
		this.frame = new JFrame(name);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setMinimumSize(new Dimension(800, 400));
		frame.setResizable(false);
		panel = frame.getContentPane();
		panel.setLayout(new BorderLayout());


	}

	@Override
	public void requestActivePanel(SwingPanel panel) {
		super.requestActivePanel(panel);

		this.panel.removeAll();
		
		panel.addToContainer(this.panel);

		this.panel.revalidate();
		this.panel.repaint();
	}

	@Override
	public void draw() {
		frame.pack();
		frame.setVisible(true);

	}

}
