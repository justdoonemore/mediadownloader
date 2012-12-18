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
 */package com.jdom.mediadownloader.series.download;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.util.SeriesLinkFinder;

public class SeriesDownloadFinder {

    private static final Logger LOG = Logger.getLogger(SeriesDownloadFinder.class);

    private final Series series;

    private final SeriesLinkFinder seriesLinkFinder;

    private final String[] titleExclusions;

    /**
     * Constructs a downloader for the specified series.
     * 
     * @param series
     *            the series to find downloads for
     * @param seriesLinkFinder
     *            the link finder implementation
     * @param titleExclusions
     *            the excluded title strings
     */
    public SeriesDownloadFinder(Series series, SeriesLinkFinder seriesLinkFinder, String[] titleExclusions) {
        this.series = series;
        this.seriesLinkFinder = seriesLinkFinder;
        this.titleExclusions = titleExclusions;
    }

    /**
     * Checks all links available for this series, finding any possible downloads.
     * 
     * @param seriesLinkFinder
     *            the series link finder
     * @param titleExclusions
     *            the excluded title strings
     * @param downloads
     *            the collection of downloads
     * @param series
     *            the series we are searching for
     * @return the series download objects
     */
    public Collection<SeriesDownload> findDownloads() {
        Collection<SeriesDownload> matchingLinks = new ArrayList<SeriesDownload>();

        Collection<String> possibleLinks = seriesLinkFinder.getPossibleLinksForSeries(series);

        for (String link : possibleLinks) {
            SeriesDownload seriesDownload = checkForMatchingLink(link);

            if (seriesDownload != null) {

                // Verify not already in the set to download
                if (matchingLinks.contains(seriesDownload) && LOG.isDebugEnabled()) {
                    LOG.debug("Skipping link, " + seriesDownload + " already set to be downloaded...");
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Adding link, " + seriesDownload + " not already set to be downloaded...");
                    }

                    matchingLinks.add(seriesDownload);
                }
            }
        }

        return matchingLinks;
    }

    /**
     * Checks a link for whether or not it matches the download criteria.
     * 
     * @param titleExclusions
     *            the excluded title strings
     * @param downloads
     *            the collection of downloads
     * @param series
     *            the series in question
     * @param link
     *            the link to check
     * @return a series download object if matching, otherwise null
     */
    private SeriesDownload checkForMatchingLink(String link) {

        SeriesDownloadLink seriesDownloadLink = new RssSeriesDownloadLink(series, link, titleExclusions);

        boolean processLink = seriesDownloadLink.matchesSeriesDownloadCriteria();

        if (processLink) {

            SeriesDownload seriesToDownload = seriesDownloadLink.createSeriesDownload();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Found season [" + seriesToDownload.getSeason() + "] episode ["
                        + seriesToDownload.getEpisode() + "] for Series [" + series.getName() + "]");
            }

            // Return the matching download
            return seriesToDownload;
        }

        // Otherwise return null
        return null;
    }
}
