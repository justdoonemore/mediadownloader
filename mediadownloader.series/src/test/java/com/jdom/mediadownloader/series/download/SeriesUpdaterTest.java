/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.mediadownloader.series.download;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;
import org.junit.Test;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.services.SeriesDASService;

/**
 * @author djohnson
 * 
 */
public class SeriesUpdaterTest {

	private final SeriesDASService seriesDas = mock(SeriesDASService.class);

	private final SeriesUpdater seriesUpdater = new SeriesUpdater(seriesDas);

	@Test
	public void newerSeasonTakesPrecedence() {
		Series dbEntity = new Series("Test", 2, 5);
		returnSeriesEntityWhenSearchedByName(dbEntity);

		// Newer season
		Series download = new Series(dbEntity.getName(), 3, 2);

		seriesUpdater.downloadComplete(download);

		verify(seriesDas).updateObject(
				argThat(isEqualTo(new Series(download.getName(), download
						.getSeason(), download.getEpisode() + 1))));
	}

	@Test
	public void newerEpisodeTakesPrecedence() {
		Series dbEntity = new Series("Test", 2, 5);
		returnSeriesEntityWhenSearchedByName(dbEntity);

		// Newer episode
		Series download = new Series(dbEntity.getName(), dbEntity.getSeason(),
				dbEntity.getEpisode() + 1);

		seriesUpdater.downloadComplete(download);

		verify(seriesDas).updateObject(
				argThat(isEqualTo(new Series(download.getName(), download
						.getSeason(), download.getEpisode() + 1))));
	}

	@Test
	public void olderEpisodeDoesntUpdateDatabase() {
		Series dbEntity = new Series("Test", 2, 5);
		returnSeriesEntityWhenSearchedByName(dbEntity);

		// Older episode
		Series download = new Series(dbEntity.getName(), dbEntity.getSeason(),
				dbEntity.getEpisode() - 1);

		seriesUpdater.downloadComplete(download);

		verify(seriesDas, never()).updateObject(any(Series.class));
	}

	@Test
	public void equalIncrementsEpisode() {
		Series dbEntity = new Series("Test", 2, 5);
		returnSeriesEntityWhenSearchedByName(dbEntity);

		// Equal season and episode
		Series download = new Series(dbEntity.getName(), dbEntity.getSeason(),
				dbEntity.getEpisode());

		seriesUpdater.downloadComplete(download);

		verify(seriesDas).updateObject(
				argThat(isEqualTo(new Series(download.getName(), download
						.getSeason(), download.getEpisode() + 1))));
	}

	private void returnSeriesEntityWhenSearchedByName(Series seriesObj) {
		when(seriesDas.getSeriesByName(seriesObj.getName())).thenReturn(
				seriesObj);
	}

	/**
	 * Matcher that verifies the object is equal to the specified {@link Series}
	 * .
	 * 
	 * @param series
	 *            the series to be equal to
	 * @return the matcher
	 */
	private Matcher<Series> isEqualTo(Series series) {
		return equalTo(series);
	}
}
