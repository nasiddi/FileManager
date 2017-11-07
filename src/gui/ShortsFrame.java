package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JTable;

import enums.Constants;
import logic.InfoIO;
import model.Series;

public class ShortsFrame {
	
	private JFrame frame;
	private Container panel;

	public ShortsFrame(){
	frame = new JFrame("Shorts");

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
    Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
	frame.setSize(new Dimension(550, 2090));

    int x = (int) rect.getMaxX() - frame.getWidth()+10;
    int y = 0;
    frame.setLocation(x, y);
	frame.setIconImage(Toolkit.getDefaultToolkit().getImage("assets/iconblue.png"));
	frame.setResizable(true);
	frame.setVisible(true);
	panel = frame.getContentPane();
	panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
	panel.setBackground(Color.WHITE);
	initTable();
	}

	public void initTable() {
		panel.removeAll();
		ArrayList<Series> series = new ArrayList<Series>(InfoIO.loadNamesInHashMap().values());
		
		Collections.sort(series, new Comparator<Series>() {
			public int compare(Series i, Series j) {
				String s1 = i.getSeriesName();
				String s2 = j.getSeriesName();
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		});
		String[] column1 = {"Short"};
		String[] column2 = {"Series"};

		Object[][] shortTable = new Object[series.size()][1];
		Object[][] nameTable = new Object[series.size()][1];

		int i = 0;
		for(Series s : series){
			if(s.getShort().equals(""))
				continue;
			shortTable[i][0] = s.getShort();
			nameTable[i][0] = s.getSeriesName();
			i++;
		}
		
		JTable table1 = new JTable(shortTable, column1);
		JTable table2 = new JTable(nameTable, column2);
		table1.setFont(Constants.BOXFONT.deriveFont(Font.BOLD, 22));
		table2.setFont(Constants.BOXFONT.deriveFont(Font.PLAIN, 22));
		table1.setEnabled(false);
		table2.setEnabled(false);
		table1.setRowHeight(25);
		table2.setRowHeight(25);

		table1.setPreferredSize(new Dimension(100, table1.getPreferredSize().height));

		table2.setPreferredSize(new Dimension(400, table2.getPreferredSize().height));
		
		panel.add(table1);
		panel.add(table2);

		
		frame.revalidate();
		frame.repaint();
	}
}
