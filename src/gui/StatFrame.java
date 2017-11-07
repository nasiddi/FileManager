package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import enums.Constants;
import enums.DataType;
import logic.InfoIO;
import model.Episode;
import model.InfoModel;
import model.Season;
import model.Series;

public class StatFrame {

	private Collection<Series> series;
	private Container panel;
	private JFrame frame;
	private JTextArea statsArea;
	private static final int FRAMEWIDTH = 3000;
	private static final int FRAMEHEIGHT = 2000;

	public StatFrame(InfoModel model) {
		series = new ArrayList<>();

		for (Series s : model.getSeriesAsSortedList()) {
			if (s.getLastExistingFile() != null) {
				series.add(s);
			}
		}
		frame = new JFrame("Statistics");
		frame.setSize(new Dimension(FRAMEWIDTH, FRAMEHEIGHT));
		frame.setResizable(true);
		frame.setVisible(true);
		panel = frame.getContentPane();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		getTotalStats();
		// findUnlinkedEpisodes();
		printTable();
	//	mostUsedNames();
		initStatistic();
		initTable();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				ArrayList<String> status = new ArrayList<>();
				for (Series s : series) {
					status.add(s.getSeriesName() + ";" + s.getPremiere() + ";" + s.getEnd() + ";" + s.getShowStatus());
				}
				InfoIO.saveAllStatus(status);

			}

		});
	}

	private void initStatistic() {
		statsArea = new JTextArea();
		statsArea.setFont(Constants.BIGFONT);
		statsArea.setEditable(false);
		JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		statPanel.setPreferredSize(new Dimension(1240, FRAMEHEIGHT));
		statPanel.setOpaque(false);
		statPanel.add(statsArea);
		panel.add(statPanel, BorderLayout.EAST);
		updateTotalStats();

	}

	public void updateTotalStats() {
		TotalStats tS = getTotalStats();

		String content = tS.shows + "\tSeries\n" + tS.seasons + "\tSeasons\n" + tS.episodes + "\tEpisodes\n"
				+ (int) tS.size + "\tGigaBytes\n" + tS.inSeason + " (" + (100 * tS.inSeason / tS.shows)
				+ "%)\tIn Season\n" + tS.hiatus + " (" + (100 * tS.hiatus / tS.shows) + "%)\tOn Hiatus\n" + tS.ended
				+ " (" + (100 * tS.ended / tS.shows) + "%)\tEnded\n" + (int) (100 * tS.size / tS.episodes) / 100.0
				+ "\tGB/Episode\n" + tS.episodes / tS.shows + "\tAvg. Episodes/Series\n" + tS.dixSize + "\tWords";
		statsArea.setText(content);
	}

	private void initTable() {
		SeriesTableModel tableModel = new SeriesTableModel((List<Series>) series, this);

		JTable table = new JTable(tableModel);
		table.setFont(Constants.BOXFONT);
		table.getTableHeader().setFont(Constants.SMALLFONT);
		table.setDefaultEditor(Object.class, new MyTableCellEditor());
		table.setIntercellSpacing(new Dimension(40, 1));
		table.setShowGrid(true);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setEnabled(true);
		table.setAutoCreateRowSorter(true);
		JComboBox<String> status = new JComboBox<>(new String[] { "In Season", "Hiatus", "Ended" });
		status.setFont(Constants.BOXFONT);
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(status));
		table.getColumnModel().getColumn(0).setMinWidth(650);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		table.getColumnModel().getColumn(2).setMinWidth(150);
		table.getColumnModel().getColumn(3).setMinWidth(150);
		table.getColumnModel().getColumn(4).setMinWidth(150);
		table.getColumnModel().getColumn(5).setMinWidth(220);
		table.getColumnModel().getColumn(6).setMinWidth(220);

		table.setRowHeight(50);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(1760, 1800));
		panel.add(scroll, BorderLayout.WEST);
		panel.revalidate();
		panel.repaint();

	}

	private TotalStats getTotalStats() {
		TotalStats totalStats = new TotalStats();
		totalStats.shows = series.size();
		totalStats.dixSize = InfoIO.loadInfoFile(Constants.DICTIONARYFILE).size();
		totalStats.dixSize += InfoIO.loadInfoFile(Constants.SMALLEXCEPTIONS).size();
		for (Series s : series) {
			totalStats.episodes += s.getTotalCount();
			totalStats.seasons += s.getSeasonCount();
			totalStats.size += s.getSizeOfFolder();

			switch (s.getShowStatus()) {
			case ENDED:
				totalStats.ended++;
				break;
			case HIATUS:
				totalStats.hiatus++;
				break;
			case INSEASON:
				totalStats.inSeason++;
				break;
			case NONE:
				break;
			default:
				break;

			}
		}

		return totalStats;
	}

	public void mostUsedNames() { // NO_UCD (unused code)
		HashMap<String, Integer> names = new HashMap<>();
		HashMap<String, Integer> words = new HashMap<>();

		for (Series show : series) {
			for (Season season : show.getSeasons()) {
				for (Episode episode : season.getEpisodesAsSortedList()) {
					String name = episode.getEpisodeName();
					String[] split = name.split(" ");
					for (String s : split) {
						if (!words.containsKey(s)) {
							words.put(s, 1);
							continue;
						}
						words.put(s, words.get(s) + 1);
					}

					if (!names.containsKey(name)) {
						names.put(name, 1);
						continue;
					}
					names.put(name, names.get(name) + 1);

				}

			}

		}

		Map<String, Integer> sorted = sortByValue(words);
		Set<Entry<String, Integer>> set = sorted.entrySet();
		int uniqueCounter = 0;
		int totWords = 0;
		for (Entry e : set) {
			System.out.println(e.getKey() + ": " + e.getValue());
			if (!e.getKey().equals(""))
				totWords += (int) e.getValue();
			if ((int) e.getValue() == 1)
				uniqueCounter++;
		}
		System.out.println("Unique Words: " + uniqueCounter);
		System.out.println("Total Words: " + totWords);
	}

	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream()
				.sorted(Map.Entry
						.comparingByValue(/* Collections.reverseOrder() */))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	@SuppressWarnings("unused")
	private void findAVIs() {
		int count = 0;
		for (Series s : series) {
			for (Season season : s.getSeasons()) {
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
	
	private void printTable(){
		for(Series show : series){
			System.out.println(show.getSeriesName()+";"+show.getShowStatus().toString()+";"+show.getTotalCount()+";"+show.getSeasonCount()+";"+show.getPremiere());
		}
	}

	@SuppressWarnings("unused")
	private void findUnlinkedEpisodes() {
		for (Series show : series) {
			for (Season season : show.getSeasons()) {
				for (Episode episode : season.getEpisodesAsSortedList()) {

					if (episode.getAfter().getEpisodeName().equals("nullEpisode")
							&& !episode.equals(show.getLastEpisode())) {
						System.out.println(episode.getFileName());
					}
				}

			}
		}
	}
}

class SeriesTableModel extends AbstractTableModel {
	private String[] columnNames = { "Series", "Status", "#S", "#E", "GB", "Premiere", "Final" };
	private static final int COLUMN_SERIES = 0;
	private static final int COLUMN_STATUS = 1;
	private static final int COLUMN_SEASONS = 2;
	private static final int COLUMN_EPISODES = 3;
	private static final int COLUMN_SPACE = 4;
	private static final int COLUMN_PREMIERE = 5;
	private static final int COLUMN_FINAL = 6;
	private List<Series> listSeries;
	private StatFrame statframe;

	public SeriesTableModel(List<Series> listSeries, StatFrame statFrame) {
		this.listSeries = listSeries;
		this.statframe = statFrame;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 1 || columnIndex == 5 || columnIndex == 6)
			return true;
		return false;
	}

	@Override
	public int getRowCount() {
		return listSeries.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (listSeries.isEmpty())
			return Object.class;
		return getValueAt(1, columnIndex).getClass();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Series series = listSeries.get(rowIndex);
		Object returnValue = null;
		switch (columnIndex) {
		case COLUMN_SERIES:
			returnValue = series.getSeriesName();
			break;
		case COLUMN_STATUS:
			returnValue = series.getShowStatus();
			break;
		case COLUMN_SEASONS:
			returnValue = series.getSeasonCount();
			break;
		case COLUMN_EPISODES:
			returnValue = series.getTotalCount();
			break;
		case COLUMN_SPACE:
			returnValue = series.getSizeOfFolder();
			break;
		case COLUMN_PREMIERE:
			String premiere = series.getPremiere();
			if (premiere.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
				returnValue = series.getPremiere();
			else
				return "";
			break;
		case COLUMN_FINAL:
			String end = series.getEnd();
			if (end.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
				returnValue = series.getEnd();
			else
				return "";
			break;
		default:
			break;
		}
		return returnValue;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int colIndex) {
		String date = "";
		String year = "";
		String month = "";
		String day = "";
		int y = 0, m = 0, d = 0;
		String entry = aValue.toString();
		if (entry.matches("[0-9]{8}")) {
			year = entry.substring(0, 4);
			month = entry.substring(4, 6);
			day = entry.substring(6);
			try {
				y = Integer.parseInt(year);
				m = Integer.parseInt(month);
				d = Integer.parseInt(day);
			} catch (NumberFormatException e) {

			}

			if (1950 < y && y <= Calendar.getInstance().get(Calendar.YEAR) && 0 < m && m < 13 && 0 < d && d < 32) {
				date = year + "-" + month + "-" + day;
			}
		} else if (entry.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
			date = entry;

		Series s = listSeries.get(rowIndex);
		fireTableRowsUpdated(rowIndex, rowIndex);
		switch (colIndex) {
		case COLUMN_PREMIERE:
			s.setPreiere(date);
			break;
		case COLUMN_FINAL:
			s.setEnd(date);
			break;
		case COLUMN_STATUS:
			s.changeStatusData(DataType.STATUS, entry);
			break;
		default:
			break;
		}
		statframe.updateTotalStats();
	}
}

class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JComponent component = new JTextField();

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
			int vColIndex) {
		((JTextField) component).setText((String) value);
		((JTextField) component).setFont(Constants.BOXFONT);
		((JTextField) component).selectAll();
		return component;
	}

	@Override
	public Object getCellEditorValue() {
		return ((JTextField) component).getText();
	}
}

class TotalStats {
	int episodes;
	int seasons;
	int shows;
	double size;
	int inSeason;
	int hiatus;
	int ended;
	int dixSize;
}