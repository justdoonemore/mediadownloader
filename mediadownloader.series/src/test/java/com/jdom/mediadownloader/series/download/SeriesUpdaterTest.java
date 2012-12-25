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
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesBuilder;
import com.jdom.mediadownloader.services.SeriesDASService;

/**
 * @author djohnson
 * 
 */
public class SeriesUpdaterTest {

	private final SeriesDASService seriesDas = mock(SeriesDASService.class);

	private final SeriesUpdater seriesUpdater = new SeriesUpdater(seriesDas);

	private final SeriesBuilder dbEntity = new SeriesBuilder()
			.withName("Test").withSeason(2).withEpisode(5);

	@Before
	public void setUp() {
		returnSeriesEntityWhenSearchedByName(dbEntity.build());
	}

	@Test
	public void newerSeasonTakesPrecedence() {
		// Newer season
		SeriesBuilder download = seriesLike(dbEntity).withSeason(3)
				.withEpisode(2);

		seriesUpdater.downloadComplete(download.build());

		verify(seriesDas).updateObject(
				argThat(is(equalTo(seriesLike(download)
						.withIncrementedEpisode().build()))));
	}

	@Test
	public void newerEpisodeTakesPrecedence() {
		// Newer episode
		SeriesBuilder download = seriesLike(dbEntity).withEpisode(6);

		seriesUpdater.downloadComplete(download.build());

		verify(seriesDas).updateObject(
				argThat(is(equalTo(seriesLike(download)
						.withIncrementedEpisode().build()))));
	}

	@Test
	public void olderEpisodeDoesntUpdateDatabase() {
		// Older episode
		SeriesBuilder downloadBuilder = seriesLike(dbEntity)
				.withEpisode(4);
		Series download = downloadBuilder.build();

		seriesUpdater.downloadComplete(download);

		verify(seriesDas, never()).updateObject(any(Series.class));
	}

	@Test
	public void equalIncrementsEpisode() {
		// Equal season and episode
		SeriesBuilder download = seriesLike(dbEntity);

		seriesUpdater.downloadComplete(download.build());

		verify(seriesDas).updateObject(
				argThat(is(equalTo(seriesLike(download)
						.withIncrementedEpisode().build()))));
	}

	private void returnSeriesEntityWhenSearchedByName(Series seriesObj) {
		when(seriesDas.getSeriesByName(seriesObj.getName())).thenReturn(
				seriesObj);
	}

	private static SeriesBuilder seriesLike(SeriesBuilder builder) {
		return new SeriesBuilder(builder);
	}
}
