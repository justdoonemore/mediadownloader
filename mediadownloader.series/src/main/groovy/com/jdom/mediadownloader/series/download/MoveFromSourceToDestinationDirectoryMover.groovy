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
package com.jdom.mediadownloader.series.download

import org.springframework.util.FileSystemUtils

import static groovy.io.FileType.FILES

import com.google.common.collect.Lists
import com.google.common.io.Files
import com.jdom.mediadownloader.series.domain.Series
import com.jdom.mediadownloader.series.download.filter.ExcludeStartsWith
import com.jdom.mediadownloader.series.util.SeriesUtil
import com.jdom.util.time.Duration
import com.jdom.util.time.TimeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author djohnson
 *
 */
class MoveFromSourceToDestinationDirectoryMover implements
      DownloadedNzbMover {

   private static final Logger LOG = LoggerFactory
         .getLogger(MoveFromSourceToDestinationDirectoryMover.class);

   public static final String UNPACK_PREFIX = "_UNPACK";

   private final FileFilter downloadedNzbsFilter;

   private final File moviesDirectory;

   private final Duration timeAgoLastModified;

   private final File tvDirectory;

   private final File downloadedDirectory;

   /**
    * @param downloadedDirectory
    *            the directory where nzb contents were downloaded to
    * @param tvDirectory
    *            the directory to place tv shows in
    * @param moviesDirectory
    *            the directory to place movies in
    * @param timeAgoLastModified
    *            how long ago the last modified time must be before, for the
    *            file to be picked up
    */
   public MoveFromSourceToDestinationDirectoryMover(File downloadedDirectory,
                                                    File tvDirectory, File moviesDirectory, Duration timeAgoLastModified) {
      this.downloadedDirectory = downloadedDirectory;
      this.tvDirectory = tvDirectory;
      this.moviesDirectory = moviesDirectory;
      this.timeAgoLastModified = timeAgoLastModified;

      // Prepare the exclusions filter
      Collection<String> exclusionPrefixes = new HashSet<String>();
      exclusionPrefixes.add(UNPACK_PREFIX);

      downloadedNzbsFilter = new ExcludeStartsWith(exclusionPrefixes);
   }

   /**
    * Handles the retrieved nzbs.
    *
    *
    * @return A list of series to be updated
    */
   @Override
   public List<Series> handleRetrievedNzbs() {
      // Get a list of all contents of the downloaded directory,
      // and exclude the ones being unpacked
      if (LOG.isDebugEnabled()) {
         LOG.debug("Looking for downloads in directory ["
               + downloadedDirectory.getAbsolutePath() + "]");
      }

      Collection<File> downloads = downloadedDirectory.listFiles(downloadedNzbsFilter)

      List<Series> seriesList = new ArrayList<Series>();

      // Look for any series
      for (File candidate : downloads) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Found file [" + candidate.getAbsolutePath() + "]");
         }

         final String downloadedEpisodeName = candidate
               .getName();

         Series series = SeriesUtil.parseSeries(downloadedEpisodeName);

         // If a series was found
         if (series != null) {
            if (moveSeries(tvDirectory, timeAgoLastModified,
                  candidate, downloadedEpisodeName, series)) {
               seriesList.add(series);
            }
         } else {
            boolean movedMovie = moveMovie(timeAgoLastModified,
                  candidate, new File(moviesDirectory,
                  downloadedEpisodeName));

            if (!movedMovie && LOG.isDebugEnabled()) {
               LOG.debug("Skipping moving movie [" + downloadedEpisodeName
                     + "]");
            }
         }
      }

      // Sort the list in ascending order by season/episode
      Collections.sort(seriesList);

      return seriesList;
   }

   /**
    * @param tvDirectory
    * @param timeAgoLastModified
    * @param directoryWithDownload
    * @param downloadedEpisodeName
    * @param series
    */
   public boolean moveSeries(File tvDirectory, Duration timeAgoLastModified,
                             File directoryWithDownload, String downloadedEpisodeName,
                             Series series) {
      String show = series.getName();

      File destinationSeriesFolder = new File(tvDirectory, show);

      boolean movedSeries = moveContents(timeAgoLastModified,
            directoryWithDownload, destinationSeriesFolder);

      if (movedSeries) {
         // Now rename the files moved in that match the folder name
         Collection<File> filesToRename = destinationSeriesFolder.listFiles(new SeriesEpisodeFileFilter(downloadedEpisodeName))

         for (File fileToRename : filesToRename) {

            String fileExtension = "." + Files.getFileExtension(fileToRename.getAbsolutePath());

            File destinationFile = new File(destinationSeriesFolder, series
                  .toDownloadedEpisodeNamingString() + fileExtension);

            boolean renamed = fileToRename.renameTo(destinationFile);
            if (!renamed) {
               LOG.warn(String.format("Failed to rename [%s] to [%s]!", fileToRename, destinationFile));
            }
         }
      }

      return movedSeries;
   }

   /**
    * @param timeAgoLastModified
    * @param directoryWithDownload
    * @param destination
    * @return
    */
   public boolean moveMovie(Duration timeAgoLastModified,
                            File directoryWithDownload, File destination) {
      return moveContents(timeAgoLastModified, directoryWithDownload,
            destination);
   }

   /**
    * Moves the contents to the appropriate directory.
    *
    * @param timeAgoLastModified
    *
    * @param sourceDir
    *            the source directory
    * @param destination
    *            the target directory
    * @return true if the contents were moved
    */
   private boolean moveContents(Duration timeAgoLastModified,
                                File sourceDir, File destination) {

      boolean movedContents = false;

      // If we should move the directory contents
      long notModifiedSince = TimeUtil.currentTimeMillis() - timeAgoLastModified.toMillis().value;

      boolean readyToMove = true
      sourceDir.eachFileRecurse(FILES) {
         long fileLastModified = it.lastModified();
         if (fileLastModified >= notModifiedSince) {
            readyToMove = false
         }
      }

      if (!readyToMove) {
         return false
      }

      if (!destination.exists() && !destination.mkdirs()) {
         throw new IllegalArgumentException(
               "Unable to create new directory [" + destination + "]");
      }

      List<File> failedFiles = Lists.newArrayList();
      for (File file : sourceDir.listFiles()) {
         File targetFile = new File(destination, file.getName());
         try {
            Files.move(file, targetFile);
         } catch (IOException e) {
            LOG.error(String.format("Unable to move file [%s] to [%s]!", file, targetFile), e);
         }
      }

      if (!failedFiles.isEmpty()) {
         StringBuilder sb = new StringBuilder("The following files failed to move: ");
         for (File failedFile : failedFiles) {
            sb.append("\n").append(failedFile);
         }
         LOG.error(sb.toString());
      } else {
         FileSystemUtils.deleteRecursively(sourceDir)
      }

      movedContents = true;

      return movedContents;
   }

   private static final class SeriesEpisodeFileFilter implements FileFilter {

      private final String filename;

      /**
       * Default Constructor.
       *
       *
       * @param filename
       *            the filename
       */
      SeriesEpisodeFileFilter(String filename) {
         this.filename = filename;
      }

      @Override
      public boolean accept(File arg0) {
         return arg0.getName().startsWith(this.filename);
      }
   }
}
