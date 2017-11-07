package model;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import enums.ShowStatus;

public class ShowInfoFields {
	private boolean isVisible = false;
	private JPanel showPanel;
	private JTextField seriesNameField;
	private JTextField shortNameField;
	private JTextField seasonField;
	private JComboBox<ShowStatus> statusBox;
	private JComboBox<String> noNameBox;
	private ArrayList<JTextField> episodes = new ArrayList<JTextField>();
	private String oldShort;
	private static ArrayList<String> shorts = new ArrayList<String>();

	public void requestFocus() {
		episodes.get(1).requestFocusInWindow();
	}

	public JPanel getShowPanel() {
		return showPanel;
	}

	public void setIndex(int index) {
		showPanel.setName(index + "");
	}

	public void setShowPanel(JPanel show) {
		this.showPanel = show;
	}

	public String getSeriesName() {
		return seriesNameField.getText();
	}

	public void setSeriesName(String seriesName) {
		seriesNameField.setText(seriesName);
	}

	public void setSeriesNameField(JTextField seriesName) {
		this.seriesNameField = seriesName;
	}

	public String getShortName() {
		return shortNameField.getText();
	}

	public void setShortNameField(JTextField shortName) {
		this.shortNameField = shortName;

	}

	public void setShortName(String shortName) {
		oldShort = shortName;
		shortNameField.setText(shortName);
		if (!shortName.equals(""))
			shorts.add(shortName);
		shortNameField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (getShortName().equals(""))
					return;
				if (getShortName().equals(oldShort))
					return;
				shorts.remove(oldShort);

				if (shorts.contains(getShortName()))
					shortNameField.setBackground(Color.RED);
				else
					shortNameField.setBackground(Color.WHITE);
				shorts.add(getShortName());
				oldShort = getShortName();
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public String getSeason() {
		return seasonField.getText();
	}

	public void setSeason(String season) {
		seasonField.setText(season);
	}

	public void setSeasonField(JTextField season) {
		this.seasonField = season;
	}

	public ShowStatus getStatus() {
		return (ShowStatus) statusBox.getSelectedItem();
	}

	public void setStatus(ShowStatus status) {
		statusBox.setSelectedItem(status);
	}

	public void setStatusBox(JComboBox<ShowStatus> statusBox) {
		this.statusBox = statusBox;
	}

	private boolean needsEpisodeName() {
		if (noNameBox.getSelectedIndex() == 1)
			return true;
		else
			return false;
	}

	public void setEpisodeNameNeeded(boolean noName) {
		noNameBox.setSelectedIndex((noName) ? 1 : 0);
	}

	public void setNoNameBox(JComboBox<String> noNameBox) {
		this.noNameBox = noNameBox;
	}

	public ArrayList<String> getEpisodes() {
		ArrayList<String> names = new ArrayList<String>();
		for (JTextField e : episodes) {
			if (e.getText().contains("nullEpisode"))
				continue;
			names.add(e.getText());
		}
		return names;
	}

	public void setEpisode(String name, int index) {
		episodes.get(index).setText(name);
	}

	public void addEpisodeField(JTextField episode) {
		episodes.add(episode);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	// SeriesName#short#Season#noName#Episodes
	public String getAllData() {
		int seasonNR = Integer.parseInt(seasonField.getText());
		if (!statusBox.getSelectedItem().equals(ShowStatus.INSEASON))
			seasonNR--;
		
		String info = seriesNameField.getText() + "#" + shortNameField.getText() + "#" + ((seasonNR < 10) ? "0" : "")
				+ seasonNR + "#";

		info += (needsEpisodeName()) ? "Y" : "N";
		for (String e : getEpisodes()) {
			info += "#" + e;
		}
		return info;
	}
}