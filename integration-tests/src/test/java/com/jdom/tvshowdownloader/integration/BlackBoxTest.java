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
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContextAware;

import com.jdom.mediadownloader.MediaDownloader;
import com.jdom.mediadownloader.domain.Series;
import com.jdom.mediadownloader.domain.SeriesNotification;
import com.jdom.mediadownloader.domain.User;
import com.jdom.mediadownloader.services.ConfigurationManagerService;
import com.jdom.mediadownloader.services.DasFactory;
import com.jdom.mediadownloader.services.SeriesDASService;
import com.jdom.mediadownloader.services.UserDASService;
import com.jdom.services.series.download.util.SeriesDownloadUtil;
import com.jdom.util.email.Email;
import com.jdom.util.properties.PropertiesUtil;
import com.jdom.util.time.TimeUtil;
import com.jdom.util.time.TimeUtilTest;

public class BlackBoxTest {

	private static final File TEST_CONFIGURATION_FILE_DIRECTORY = TestUtil
			.setupTestClassDir(MediaDownloader.class);

	private static final String PROPERTIES_FILE_NAME = "integration_test.properties";

	private static final File propertiesFile = new File(
			TEST_CONFIGURATION_FILE_DIRECTORY, PROPERTIES_FILE_NAME);

	private final User user = new User("someUser", "someEmail@blah.com");

	private final Series simpsonsSeries = new Series("The Simpsons", 24, 5);

	private final Series raisingHopeSeries = new Series("Raising Hope", 3, 9);

	private ConfigurationManagerService configurationManager;

	private DasFactory dasFactory;

	@BeforeClass
	public static void staticSetUp() {
		createIntegrationTestPropertiesFile();

		System.getProperties().putAll(
				PropertiesUtil.readPropertiesFile(propertiesFile));
	}

	@Before
	public void setUp() {
		// Clean test directory each time
		TestUtil.setupTestClassDir(BlackBoxTest.class);

		MediaDownloader.initializeContext();

		configurationManager = BlackBoxTest
				.getService(ConfigurationManagerService.class);
		dasFactory = BlackBoxTest.getService(DasFactory.class);

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
				SeriesDownloadUtil.containsSeries(simpsonsSeries));
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

		// Implicitly tests that no emails were sent regarding Simpsons
		// downloads
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
