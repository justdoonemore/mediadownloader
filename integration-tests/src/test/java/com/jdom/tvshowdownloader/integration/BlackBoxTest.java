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
package com.jdom.tvshowdownloader.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jdom.services.series.download.util.SeriesDownloadUtil;
import com.jdom.services.util.ServiceLocator;
import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.tvshowdownloader.domain.SeriesDownload;
import com.jdom.tvshowdownloader.ejb.ConfigurationManager;
import com.jdom.tvshowdownloader.ejb.SeriesDASService;
import com.jdom.tvshowdownloader.ejb.SeriesDownloadDASService;
import com.jdom.tvshowdownloader.sar.MediaDownloader;
import com.jdom.util.properties.PropertiesUtil;
import com.jdom.util.time.TimeUtil;
import com.jdom.util.time.TimeUtilTest;

public class BlackBoxTest {

	private static final String PROPERTIES_FILE_NAME = "integration_test.properties";

	private final File baseTestDir = TestUtil
			.setupTestClassDir(BlackBoxTest.class);

	private final File propertiesFile = new File(baseTestDir,
			PROPERTIES_FILE_NAME);

	private ConfigurationManager configurationManager;

	private MockNzbDownloader nzbDownloader;

	@Before
	public void setUp() {
		createIntegrationTestPropertiesFile();

		System.getProperties().putAll(
				PropertiesUtil.readPropertiesFile(propertiesFile));

		MediaDownloader.initializeContext();

		// Must be after the properties are set
		configurationManager = new ConfigurationManager();

		createRequiredDirectories();

		// Dependent services
		startNzbDownloader();

		loadDatabase();
	}

	@After
	public void tearDown() throws SQLException {
		stopNzbDownloader();

		// TODO: How can cleaning the database be made better?
		SeriesDASService seriesDas = ServiceLocator.getSeriesDAS();
		List<Series> all = seriesDas.getAll();
		for (Series series : all) {
			seriesDas.deleteObject(series);
		}

		SeriesDownloadDASService seriesDownloadDas = ServiceLocator
				.getSeriesDownloadDAS();
		List<SeriesDownload> all2 = seriesDownloadDas.getAll();
		for (SeriesDownload series : all2) {
			seriesDownloadDas.deleteObject(series);
		}
	}

	@Test
	public void updatesSeriesInDatabaseToReflectNewestDownload() {
		startMediaDownloader();

		assertEquals(
				"Expected the database to reflect the new episode to search for!",
				8, getSimpsonsEpisode().getEpisode());
	}

	@Test
	public void movesTvShowDownloadsToTvArchiveDir() {
		startMediaDownloader();

		File archivedTvDirectory = configurationManager
				.getArchivedTvDirectory();
		File simpsonsDir = new File(archivedTvDirectory, "The Simpsons");

		assertTrue("Expected the simpsons directory to exist!",
				simpsonsDir.isDirectory());

		File[] expectedFiles = new File[] {
				new File(simpsonsDir, "The_Simpsons_S24E05.nzb"),
				new File(simpsonsDir, "The_Simpsons_S24E06.nzb"),
				new File(simpsonsDir, "The_Simpsons_S24E07.nzb") };

		for (File expectedFile : expectedFiles) {
			assertTrue(
					"Did not find expected file ["
							+ expectedFile.getAbsolutePath() + "]!",
					expectedFile.isFile());
		}
	}

	@Test
	public void doesNotDownloadOldEpisodes() {
		startMediaDownloader();

		File archivedTvDirectory = configurationManager
				.getArchivedTvDirectory();
		File simpsonsDir = new File(archivedTvDirectory, "The Simpsons");

		assertTrue("Expected the simpsons directory to exist!",
				simpsonsDir.isDirectory());

		File notExpectedFile = new File(simpsonsDir, "The_Simpsons_S15E22.nzb");

		assertFalse(
				"Should not have found unexpected file ["
						+ notExpectedFile.getAbsolutePath() + "]!",
				notExpectedFile.isFile());
	}

	@Test
	public void doesNotDownloadEpisodesAlreadyInDownloadQueue() {
		// TODO: Can we retrieve the implementation service some other way,
		// maybe via ServiceLocator?
		SeriesDownloadUtil.addSeries(new Series("The Simpsons", 24, 7));

		startMediaDownloader();

		File archivedTvDirectory = configurationManager
				.getArchivedTvDirectory();
		File simpsonsDir = new File(archivedTvDirectory, "The Simpsons");

		assertTrue("Expected the simpsons directory to exist!",
				simpsonsDir.isDirectory());

		File notExpectedFile = new File(simpsonsDir, "The_Simpsons_S24E07.nzb");

		assertFalse(
				"An episode already in the download queue should not have been downloaded!!",
				notExpectedFile.isFile());
	}

	@Test
	public void downloadsEpisodesExpiredInDownloadQueue() {
		// Freeze time to three hours ago
		TimeUtilTest
				.freezeTime(TimeUtil.currentTimeMillis()
						- (configurationManager.getSeriesDownloadTimeToLive() * 60000 * 60));
		SeriesDownloadUtil.addSeries(new Series("The Simpsons", 24, 7));

		// Now it's three hours later, and the queue entry is expired
		TimeUtilTest.resumeTime();

		startMediaDownloader();

		File archivedTvDirectory = configurationManager
				.getArchivedTvDirectory();
		File simpsonsDir = new File(archivedTvDirectory, "The Simpsons");

		assertTrue("Expected the simpsons directory to exist!",
				simpsonsDir.isDirectory());

		File expectedFile = new File(simpsonsDir, "The_Simpsons_S24E07.nzb");

		assertTrue(
				"An episode expired in the download queue should have been downloaded!!",
				expectedFile.isFile());
	}

	@Test
	public void movesMovieDownloadsToMoviesArchiveDir() {
		File someMovieFile = placeDownloadedMovie("someMovie");

		startMediaDownloader();

		File archivedMoviesDirectory = configurationManager
				.getArchivedMoviesDirectory();
		File expectedMovieDirectory = new File(archivedMoviesDirectory,
				someMovieFile.getParentFile().getName());

		assertTrue(
				"Expected the movie to have been moved to the archived directory!",
				expectedMovieDirectory.isDirectory());

		File expectedFile = new File(expectedMovieDirectory,
				someMovieFile.getName());

		assertTrue(
				"Did not find expected file [" + expectedFile.getAbsolutePath()
						+ "]!", expectedFile.isFile());
	}

	// TODO: Tests for emails being sent?

	/**
	 * Writes a movie file out to a folder with the specified name in the
	 * downloaded directory.
	 * 
	 * @param movieName
	 *            the movie name, used as the folder name
	 * @return the file reference to the "movie" file
	 */
	private File placeDownloadedMovie(String movieName) {
		File downloadedDir = configurationManager.getNzbDownloadedDirectory();
		File someMovie = new File(downloadedDir, movieName);
		someMovie.mkdirs();
		File someMovieFile = new File(someMovie, movieName + ".avi");
		try {
			FileUtils.write(someMovieFile, movieName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return someMovieFile;
	}

	private void createRequiredDirectories() {
		configurationManager.getNzbDestinationDirectory().mkdirs();
		configurationManager.getNzbDownloadedDirectory().mkdirs();
		configurationManager.getArchivedTvDirectory().mkdirs();
		configurationManager.getArchivedMoviesDirectory().mkdirs();
	}

	private void startNzbDownloader() {
		nzbDownloader = new MockNzbDownloader(configurationManager);
		nzbDownloader.start();
	}

	private void stopNzbDownloader() {
		nzbDownloader.stop = true;
	}

	private void loadDatabase() {
		SeriesDASService seriesDas = ServiceLocator.getSeriesDAS();
		seriesDas.addObject(new Series("The Simpsons", 24, 5));
	}

	private void startMediaDownloader() {
		MediaDownloader.main(new String[] { propertiesFile.getAbsolutePath() });
	}

	private void createIntegrationTestPropertiesFile() {
		try {
			String propertiesFileTemplate = IOUtils.toString(BlackBoxTest.class
					.getResourceAsStream("/" + PROPERTIES_FILE_NAME));
			propertiesFileTemplate = propertiesFileTemplate.replaceAll(
					"@BASE.TEST.DIR@", baseTestDir.getAbsolutePath());

			FileUtils.write(propertiesFile, propertiesFileTemplate);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Series getSimpsonsEpisode() {
		return ServiceLocator.getSeriesDAS().getSeriesByName("The Simpsons");
	}
}
