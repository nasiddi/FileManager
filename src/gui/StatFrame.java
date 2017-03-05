package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;

import logic.Episode;
import logic.Season;
import logic.Series;
import model.InfoModel;

public class StatFrame {

	private InfoModel model;
	private Collection<Series> series;
	private Container panel;
	private JFrame frame;
	private JTable table;

	public StatFrame(InfoModel model) {
		this.model = model;
		series = model.getSeriesAsSortedList();
		frame = new JFrame("Statistics");
		frame.setSize(new Dimension(1000, 1000));
		frame.setResizable(true);
		frame.setVisible(true);
		panel = frame.getContentPane();
		
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		getTotalStats();
		findUnlinkedEpisodes();
		showAllExtension();
		initTable();
	}
	
	private void initTable(){
		String[] columnNames = {"Series", "Status", "#S", "#E", "Premiere", "Final" };
		Object[][] data = new Object[series.size()][6];
		int i =0;
		for(Series s : series){
			
			data[i][0] = s.getSeriesName();
			data[i][1] = s.getShowStatus().nameToString();
			data[i][2] = new Integer(s.getSeasonCount());
			data[i][3] = new Integer(s.getTotalCount());
			data[i][4] = s.getPremiere();
			data[i][5] = s.getEnd();
			System.out.println(data[i][0] +";"+data[i][1] +";"+data[i][2] +";"+data[i][3] +";"+data[i][4] +";"+data[i][5]);
			i++;
		}
		table = new JTable(data, columnNames);
		table.setShowGrid(true);
		RowSorter<M>
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		JComboBox<String> status = new JComboBox<String>(new String[]{"In Season", "Hiatus", "Ended"});
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(status));
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(1000, 700));
		panel.add(scroll, BorderLayout.SOUTH);
		panel.revalidate();
		panel.repaint();
		
	}

	private void getTotalStats() {
		int totalEpisodeCount = 0;
		for (Series s : series) {
			totalEpisodeCount += s.getTotalCount();
		}
		System.out.println(totalEpisodeCount);

	}
	
	public ArrayList<String> showAllExtension(){
		ArrayList<String> extension = new ArrayList<String>();
		for (Series series : series) {
			for (Season season : series.getSeasons()) {
				for (Episode episode : season.getEpisodesAsSortedList()) {
					if (!extension.contains(episode.getFileFormat())) {
						System.out.println(episode.getFileFormat());
						extension.add(episode.getFileFormat());
					}

				}

			}
		}
		return extension;
	}

	private void findAVIs() {
		int count = 0;
		for (Series series : series) {
			for (Season season : series.getSeasons()) {
				for (Episode episode : season.getEpisodesAsSortedList()) {
					if (episode.getFileFormat().equals(".avi")) {
						System.out.println(episode.getFileName());
						count++;
					}

				}

			}
		}
		System.out.println(count);
	}

	private void findUnlinkedEpisodes() {
		for (Series series : series) {
			for (Season season : series.getSeasons()) {
				for (Episode episode : season.getEpisodesAsSortedList()) {
					if (episode.getAfter().getEpisodeName().equals("nullEpisode")
							&& !episode.equals(series.getLastEpisode())) {
						System.out.println(episode.getFileName());
					}
				}

			}
		}
	}
}
