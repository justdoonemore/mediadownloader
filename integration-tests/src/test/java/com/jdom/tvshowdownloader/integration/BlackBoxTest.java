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

import com.google.common.io.Closeables;
import com.jdom.mediadownloader.MediaDownloader;
import com.jdom.mediadownloader.domain.User;
import com.jdom.mediadownloader.series.SeriesConfiguration;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesNotification;
import com.jdom.mediadownloader.series.download.queue.SeriesDownloadQueueManager;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.services.SeriesDASService;
import com.jdom.mediadownloader.services.UserDASService;
import com.jdom.util.time.Duration;
import com.jdom.util.time.TimeUtil;
import com.jdom.util.time.TimeUtilTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.jdom.mediadownloader.services.Emailer.Email;
import static org.junit.Assert.*;

public class BlackBoxTest {

	private static final File TEST_CONFIGURATION_FILE_DIRECTORY = TestUtil
			.setupTestClassDir(MediaDownloader.class);

	private static final String PROPERTIES_FILE_NAME = "integration_test.properties";

	private static final File propertiesFile = new File(
			TEST_CONFIGURATION_FILE_DIRECTORY, PROPERTIES_FILE_NAME);

	private final User user = new User("someUser", "someEmail@blah.com");

	private final Series simpsonsSeries = new Series("The Simpsons", 24, 5);

	private final Series raisingHopeSeries = new Series("Raising Hope", 3, 9);

	private SeriesDasFactory dasFactory;

	@BeforeClass
	public static void staticSetUp() {
		createIntegrationTestPropertiesFile();

		Properties properties = new Properties();
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(propertiesFile);
			properties.load(fileReader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Closeables.closeQuietly(fileReader);
		}

		System.getProperties().putAll(properties);
	}

	@Before
	public void setUp() {
		// Clean test directory each time
		TestUtil.setupTestClassDir(BlackBoxTest.class);

		MediaDownloader.initializeContext();

		dasFactory = BlackBoxTest.getService(SeriesDasFactory.class);

		createRequiredDirectories();

		// Dependent services
		loadDatabase();
	}

	@After
	public void tearDown() throws SQLException {
		MediaDownloader.closeContext();
	}

	@Test
	public void updatesSeriesInDatabaseToReflectNewestDownload() {
		startMediaDownloader();

		assertEquals(
				"Expected the database to reflect the new episode to search for!",
				8, getSimpsonsEpisode().getEpisode());
	}

	@Test
	public void removesSeriesDownloadInQueueForSuccessfulDownload() {
		startMediaDownloader();

		assertFalse(
				"The SeriesDownload should have been removed from the queue!",
				getService(SeriesDownloadQueueManager.class).containsEntity(
						simpsonsSeries));
	}

	@Test
	public void movesTvShowDownloadsToTvArchiveDir() {
		startMediaDownloader();

		File archivedTvDirectory = getArchivedTvDirectory();
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
	public void movesMovieDownloadsToMoviesDirEvenWhenNoNewDownloadsFound() {
		// Make it so there are no downloads
		simpsonsSeries.setSeason(99);
		raisingHopeSeries.setSeason(99);
		SeriesDASService seriesDAS = dasFactory.getSeriesDAS();
		seriesDAS.updateObject(simpsonsSeries);
		seriesDAS.updateObject(raisingHopeSeries);

		movesMovieDownloadsToMoviesArchiveDir();
	}

	@Test
	public void doesNotDownloadOldEpisodes() {
		startMediaDownloader();

		File archivedTvDirectory = getArchivedTvDirectory();
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
		// episode 7 is already downloading
		alreadyDownloadingEpisodeSevenOfSimpsons();

		startMediaDownloader();

		File archivedTvDirectory = getArchivedTvDirectory();
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
		TimeUtilTest.freezeTime(TimeUtil.currentTimeMillis()
				- (new Duration(3, TimeUnit.HOURS).toMillis().value));
		alreadyDownloadingEpisodeSevenOfSimpsons();

		// Now it's three hours later, and the queue entry is expired
		TimeUtilTest.resumeTime();

		startMediaDownloader();

		File archivedTvDirectory = getArchivedTvDirectory();
		File simpsonsDir = new File(archivedTvDirectory, "The Simpsons");

		assertTrue("Expected the simpsons directory to exist!",
				simpsonsDir.isDirectory());

		File expectedFile = new File(simpsonsDir, "The_Simpsons_S24E07.nzb");

		assertTrue(
				"An episode expired in the download queue should have been downloaded!!",
				expectedFile.isFile());
	}

	@Test
	public void sendsEmailForSeriesSignedUpForNotification() {

		startMediaDownloader();

		MockEmailService emailerService = MockEmailService.instance;
		List<Email> sentEmails = emailerService.getSentEmails();

		// Check that an email was sent
		assertEquals("Expected one email to have been sent", 1,
				sentEmails.size());
		Email email = sentEmails.iterator().next();

		// Check the destination email addresses
		Collection<String> emailAddresses = email.getEmailAddresses();
		assertEquals("Expected one email address for the destination", 1,
				emailAddresses.size());
		assertEquals(user.getEmailAddress(), emailAddresses.iterator().next());

		assertEquals(
				"Incorrect email body",
				"This email is to notify you that the following show is available:\n\n[Raising Hope] - Season [3] Episode [9]",
				email.getBody());

		// Implicitly tests that no emails were sent regarding Simpsons
		// downloads
	}

	@Test
	public void movesMovieDownloadsToMoviesArchiveDir() {
		File someMovieFile = placeDownloadedMovie("someMovie");

		startMediaDownloader();

		File archivedMoviesDirectory = getArchivedMoviesDirectory();
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

	private static File getArchivedMoviesDirectory() {
		return SeriesConfiguration.ARCHIVED_MOVIES_DIRECTORY;
	}

	private static File getArchivedTvDirectory() {
		return SeriesConfiguration.ARCHIVED_TV_DIRECTORY;
	}

	private static void createIntegrationTestPropertiesFile() {
		try {
			String propertiesFileTemplate = IOUtils.toString(BlackBoxTest.class
					.getResourceAsStream("/" + PROPERTIES_FILE_NAME));
			// Have the properties file reference what will be the test
			// directory for each run
			propertiesFileTemplate = propertiesFileTemplate.replaceAll(
					"@BASE.TEST.DIR@",
					TestUtil.setupTestClassDir(BlackBoxTest.class)
							.getAbsolutePath());

			FileUtils.write(propertiesFile, propertiesFileTemplate);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Allows retrieving a service implementation by using an
	 * {@link ApplicationContextAware} mock component.
	 * 
	 * @param serviceInterface
	 *            the interface
	 * @return the implementation class
	 */
	private static <T> T getService(Class<T> serviceInterface) {
		return MockNzbDownloader.context.getBean(serviceInterface);
	}

	private void alreadyDownloadingEpisodeSevenOfSimpsons() {
		Series alreadyDownloadingEpisode = getSimpsonsEpisode();
		alreadyDownloadingEpisode.setEpisode(7);
		SeriesDownloadQueueManager queueManager = getService(SeriesDownloadQueueManager.class);
		queueManager.addEntity(alreadyDownloadingEpisode);
	}

	/**
	 * Writes a movie file out to a folder with the specified name in the
	 * downloaded directory.
	 * 
	 * @param movieName
	 *            the movie name, used as the folder name
	 * @return the file reference to the "movie" file
	 */
	private File placeDownloadedMovie(String movieName) {
		File someMovie = new File(SeriesConfiguration.NZB_DOWNLOADED_DIRECTORY,
				movieName);
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
		SeriesConfiguration.NZB_QUEUE_DIRECTORY.mkdirs();
		SeriesConfiguration.NZB_DOWNLOADED_DIRECTORY.mkdirs();
		SeriesConfiguration.ARCHIVED_TV_DIRECTORY.mkdirs();
		SeriesConfiguration.ARCHIVED_MOVIES_DIRECTORY.mkdirs();
	}

	private void loadDatabase() {
		SeriesDASService seriesDas = dasFactory.getSeriesDAS();
		seriesDas.addObject(simpsonsSeries);
		seriesDas.addObject(raisingHopeSeries);

		UserDASService userDas = dasFactory.getUserDAS();
		userDas.addObject(user);

		SeriesNotification notification = new SeriesNotification(user,
				raisingHopeSeries);
		dasFactory.getSeriesNotificationDAS().addObject(notification);
	}

	private void startMediaDownloader() {
		MediaDownloader.main(new String[] { propertiesFile.getAbsolutePath() });
	}

	private Series getSimpsonsEpisode() {
		return dasFactory.getSeriesDAS().getSeriesByName("The Simpsons");
	}
}
