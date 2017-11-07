package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import enums.DataType;
import enums.FrameState;
import enums.ShowStatus;
import enums.UpdateNameNotification;
import logic.InfoIO;
import model.Episode;
import model.InfoModel;
import model.Series;
import model.ShowInfoFields;
import swingPanel.SwingUpdatePanel;

public class UpdateController extends Controller {

	private InfoModel infoModel;

	UpdateController(MasterController masterController) {
		super(masterController);
		infoModel = super.masterController.getInfoModel();
	}

	@Override
	public void update(Observable o, Object arg) {
		SwingUpdatePanel panel = (SwingUpdatePanel) o;
		if (arg instanceof UpdateNameNotification) {
			switch ((UpdateNameNotification) arg) {

			case UPDATE:
				infoModel.setShowFieldList(prepareInfoFields(panel));
				setShowsToDisplay(ShowStatus.INSEASON);
				panel.update(super.masterController.getInfoModel(), arg);
				break;
			case SYNCUPDATE:
				infoModel.setShowFieldList(prepareInfoFields(panel));
			case MAINMENU:
				saveUpdate(panel.getShowFieldList());
				infoModel.reloadShortsFrame();
				panel.update(super.masterController.getInfoModel(), arg);
				super.masterController.getFrameStateManager().requestFrameState(FrameState.MAIN_MENU);
				break;
			default:
				break;
			}

		}

		if (arg instanceof ShowStatus) {
			setShowsToDisplay((ShowStatus) arg);
			panel.update(infoModel, UpdateNameNotification.UPDATE);
		}
	}

	// SeriesName#short#Season#noName#Episodes
	private void saveUpdate(ArrayList<ShowInfoFields> showFieldList) {
		ArrayList<String> newNames = new ArrayList<String>();

		for (ShowInfoFields fields : showFieldList) {
			if (fields.getSeriesName().equals("New Series"))
				continue;
			newNames.add(fields.getAllData());

			if (infoModel.getSeries().containsKey(fields.getSeriesName())) {
				if (!fields.getStatus().equals(infoModel.getSeries().get(fields.getSeriesName()).getShowStatus())) {
					infoModel.getSeries().get(fields.getSeriesName()).changeStatusData(DataType.STATUS,
							fields.getStatus().toString());
				}
			}
		}
		InfoIO.saveNameChanges(newNames);

		ArrayList<Series> series = infoModel.getSeriesAsSortedList();
		HashMap<String, Series> names = InfoIO.loadNamesInHashMap();
		for (Series s : series) {
			String seriesName = s.getSeriesName();
			if (names.containsKey(seriesName)) {
				s.addNamesForNewEpisodes(names.get(seriesName));
				names.remove(seriesName);
			}
		}

		if (names.isEmpty())
			return;

		for (Series s : names.values()) {
			infoModel.getSeries().put(s.getSeriesName(), s);
		}
	}

	private void setShowsToDisplay(ShowStatus arg) {
		for (ShowInfoFields fields : infoModel.getShowFieldList()) {

			if (arg.ordinal() >= fields.getStatus().ordinal()) {
				fields.setVisible(true);
			} else {
				fields.setVisible(false);
			}
		}
	}

	private ArrayList<ShowInfoFields> prepareInfoFields(SwingUpdatePanel panel) {
		ArrayList<Series> series = infoModel.getSeriesAsSortedList();
		ArrayList<ShowInfoFields> fieldList = panel.getNewFieldList(infoModel.getSeriesAsSortedList().size() + 3);
		int c = 0;
		for (Series s : series) {
			ShowInfoFields fields = fieldList.get(c++);

			fields.setSeriesName(s.getSeriesName());
			fields.setShortName(s.getShort());
			Episode last = null;
			if (infoModel.areFilesLoaded())
				last = s.getLastExistingFile();
			else {
				last = s.getFirstNameFileEpisode();
			}
			boolean inSeason = s.getShowStatus().equals(ShowStatus.INSEASON);

			int snr = 0;
			if (last == null) {
				last = new Episode();
				snr = s.getCurrentSeason().getSeasonNR();
			} else
				snr = last.getSeasonNR();

			if (!inSeason)
				snr++;

			fields.setSeason((snr < 10) ? "0" + snr : "" + snr);

			fields.setStatus(s.getShowStatus());

			fields.setEpisodeNameNeeded(s.getEpisodeNameNeeded());

			fields.setEpisode(last.getEpisodeNRasString() + " - " + last.getEpisodeName(), 0);

			int enr = last.getEpisodeNR();

			if (!infoModel.areFilesLoaded())
				snr = (inSeason) ? snr : snr - 1;
			enr = (!inSeason) ? -1 : enr;
			HashMap<Integer, Episode> episodes = s.getSeason(snr).getEpisdoes();
			int i = 1;
			int total = 30 + enr;
			for (enr++; enr < total; enr++) {
				if (episodes.containsKey(enr))
					fields.setEpisode(
							episodes.get(enr).getEpisodeNRasString() + " - " + episodes.get(enr).getEpisodeName(), i++);
				else
					fields.setEpisode(((enr < 10) ? "0" + enr : "" + enr) + " - ", i++);
			}
		}

		for (int a = 0; a < 3; a++) {
			ShowInfoFields fields = fieldList.get(c++);
			fields.setSeriesName("New Series");
			fields.setSeason("01");
			fields.setStatus(ShowStatus.INSEASON);
			fields.setEpisodeNameNeeded(false);
			for (int enr = 0; enr < 30; enr++) {
				fields.setEpisode(((enr < 10) ? "0" + enr : "" + enr) + " - ", enr);
			}
		}
		return fieldList;
	}
}
