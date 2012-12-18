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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.jdom.junit.utils.TestUtil;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.util.time.TimeUtil;

public class SabnzbdNzbDownloaderTest {

	private static final long LONG_AGO_IN_MILLIS = 100000;

	private static final int TOTAL_DOWNLOADS = 4;

	private final SabnzbdNzbDownloader action = new SabnzbdNzbDownloader(null,
			null, null, null);

	private File workingDir;

	private File downloadedDirectory;

	private File tvDirectory;

	private File moviesDirectory;

	private File movieOneFolder;

	private File movieTwoFolder;

	private File seriesOneFolder;

	private File seriesTwoFolder;

	@Before
	public void setUp() throws IOException {
		workingDir = TestUtil.setupTestClassDir(SabnzbdNzbDownloaderTest.class);

		downloadedDirectory = new File(workingDir, "downloaded");
		tvDirectory = new File(workingDir, "tv");
		moviesDirectory = new File(workingDir, "movies");

		downloadedDirectory.mkdirs();
		tvDirectory.mkdirs();
		moviesDirectory.mkdirs();

		movieOneFolder = createFolderWithFile(downloadedDirectory, "MovieOne");
		movieTwoFolder = createFolderWithFile(downloadedDirectory, "MovieTwo");
		seriesOneFolder = createFolderWithFile(downloadedDirectory,
				"The Ultimate Fighter.S10E07.XVID");
		seriesTwoFolder = createFolderWithFile(downloadedDirectory,
				"Heroes.S10E07.XVID");
	}

	@Test
	public void testFoldersAreMovedIfPastTime() throws IOException {

		makeFilesRealOld(movieOneFolder);
		makeFilesRealOld(seriesOneFolder);

		testContentsMoved(1, 1);
	}

	@Test
	public void testFilesAreRenamedDuringMoveToMatchStandardNaming()
			throws IOException {
		makeFilesRealOld(seriesOneFolder);

		testContentsMoved(0, 1);

		File[] contents = tvDirectory.listFiles();
		File[] files = contents[0].listFiles();

		assertEquals("Should have found one file in the series directory!", 1,
				files.length);
		assertEquals("Incorrect series episode name!",
				"The_Ultimate_Fighter_S10E07.avi", files[0].getName());
	}

	@Test
	public void testNoFoldersAreMovedIfNotPastTime() {
		testContentsMoved(0, 0);
	}

	@Test
	public void testAllFoldersAreMovedIfPastTime() {
		makeFilesRealOld(movieOneFolder);
		makeFilesRealOld(seriesOneFolder);
		makeFilesRealOld(movieTwoFolder);
		makeFilesRealOld(seriesTwoFolder);

		testContentsMoved(2, 2);
	}

	@Test
	public void testUnpackPrefixFoldersArentMovedEvenIfPastTime()
			throws IOException {
		// Create folder with _UNPACK prefix
		File unpackPrefixFolder = createFolderWithFile(downloadedDirectory,
				SabnzbdNzbDownloader.UNPACK_PREFIX + "_SHOULD_NOT_MOVE");

		makeFilesRealOld(movieOneFolder);
		makeFilesRealOld(seriesOneFolder);
		makeFilesRealOld(movieTwoFolder);
		makeFilesRealOld(seriesTwoFolder);
		makeFilesRealOld(unpackPrefixFolder);

		testContentsMoved(2, 2, 5);
	}

	@Test
	public void newerSeasonTakesPrecedence() {
		Series seriesObj = new Series("Test", 2, 5);

		// The download
		Series series = seriesObj.clone();
		series.setSeason(3);
		series.setEpisode(2);

		seriesObj = action.prepareSeriesUpdate(seriesObj, series);

		series.incrementEpisode();

		assertEquals(series, seriesObj);
	}

	@Test
	public void newerEpisodeTakesPrecedence() {
		Series seriesObj = new Series("Test", 2, 5);

		// The download
		Series series = seriesObj.clone();
		series.setEpisode(6);

		seriesObj = action.prepareSeriesUpdate(seriesObj, series);

		series.incrementEpisode();

		assertEquals(series, seriesObj);
	}

	@Test
	public void newerDBTakesPrecedence() {
		Series seriesObj = new Series("Test", 5, 3);

		// The download
		Series series = seriesObj.clone();
		series.setEpisode(1);

		Series seriesNew = action.prepareSeriesUpdate(seriesObj, series);

		assertEquals(seriesObj, seriesNew);
	}

	@Test
	public void equalIncrementsEpisode() {
		Series seriesObj = new Series("Test", 5, 3);

		// The download
		Series series = seriesObj.clone();

		Series seriesNew = action.prepareSeriesUpdate(seriesObj, series);

		seriesObj.incrementEpisode();

		assertEquals(seriesObj, seriesNew);
	}

	/**
	 * Test normal folder moving.
	 * 
	 * @param numberOfMoviesFoldersMoved
	 *            the number of movies folders that should have been moved
	 * @param numberOfTvFoldersMoved
	 *            the number of tv folders that should have been moved @
	 */
	private void testContentsMoved(int numberOfMoviesFoldersMoved,
			int numberOfTvFoldersMoved) {
		testContentsMoved(numberOfMoviesFoldersMoved, numberOfTvFoldersMoved,
				TOTAL_DOWNLOADS);
	}

	/**
	 * Test normal folder moving excluding UNPACK prefix.
	 * 
	 * @param numberOfMoviesFoldersMoved
	 *            the number of movies folders that should have been moved
	 * @param numberOfTvFoldersMoved
	 *            the number of tv folders that should have been moved
	 * @param numberOfFolderCreatedForTest
	 *            the number of folders that were created for the test @
	 */
	private void testContentsMoved(int numberOfMoviesFoldersMoved,
			int numberOfTvFoldersMoved, int numberOfFolderCreatedForTest) {

		int totalMoved = numberOfMoviesFoldersMoved + numberOfTvFoldersMoved;
		int leftInDownloaded = numberOfFolderCreatedForTest - totalMoved;

		List<Series> seriesList = action.handleRetrievedNzbs(
				downloadedDirectory, tvDirectory, moviesDirectory,
				LONG_AGO_IN_MILLIS);

		assertEquals(numberOfTvFoldersMoved, seriesList.size());

		assertFolderContentsAreCorrect(numberOfMoviesFoldersMoved,
				moviesDirectory);
		assertFolderContentsAreCorrect(numberOfTvFoldersMoved, tvDirectory);
		assertFolderContentsAreCorrect(leftInDownloaded, downloadedDirectory);
	}

	/**
	 * Creates a sub-folder with the specified name and places a file with the
	 * same name in the folder.
	 * 
	 * @param folder
	 *            the parent
	 * @param subfolderName
	 *            the child
	 * @return the file reference
	 * @throws IOException
	 */
	private File createFolderWithFile(File folder, String subfolderName)
			throws IOException {
		File subFolder = new File(folder, subfolderName);
		subFolder.mkdirs();

		File textFile = new File(subFolder, subfolderName + ".avi");

		FileUtils.write(textFile, subfolderName);

		textFile.setLastModified(TimeUtil.currentTimeMillis());

		return subFolder;
	}

	/**
	 * Sets the last modified time on files in a folder to ridiculously long
	 * ago.
	 * 
	 * @param folder
	 *            the folder
	 * 
	 */
	private void makeFilesRealOld(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				file.setLastModified(TimeUtil.currentTimeMillis()
						- (LONG_AGO_IN_MILLIS * 2));
			}
		}
	}

	/**
	 * Asserts the folder contents have the correct amount of contents.
	 * 
	 * @param expectedNumber
	 *            the number of expected contents
	 * @param folder
	 *            the folder to check
	 * 
	 */
	private void assertFolderContentsAreCorrect(int expectedNumber, File folder) {
		File[] contents = folder.listFiles();

		assertNotNull(contents);

		assertEquals(expectedNumber, contents.length);
	}
}
