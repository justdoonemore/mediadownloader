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
package com.jdom.mediadownloader.series.util;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.download.RegexMatch;
import com.jdom.mediadownloader.series.download.RegexUtil;
import com.jdom.mediadownloader.series.download.SeriesDownloadFinder;
import com.jdom.mediadownloader.services.UrlDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NzbIndexRssFeedLinkFinder extends AbstractSeriesLinkFinder {

    private static final Logger LOG = LoggerFactory
            .getLogger(NzbIndexRssFeedLinkFinder.class);

    private static final String DEFAULT_AGE = System.getProperty("default.age");

    private static final String INITIAL_AGE = System.getProperty("initial.age");

    private static final Pattern SHOW_REPLACEMENT_PATTERN = Pattern
            .compile("@SHOW@");

    private static final Pattern AGE_REPLACEMENT_PATTERN = Pattern
            .compile("@AGE@");

    private static final Pattern SPACE_REPLACEMENT_PATTERN = Pattern
            .compile("\\s");

    protected static final String LINK_REGEX = "<item>.*?</item>";

    protected static final long TWO_MINUTES_IN_MILLIS = 2 * 60000;

    private final String seriesSearchUrl;

    private final String[] seriesDownloadTitleExclusions;

    public NzbIndexRssFeedLinkFinder(String seriesSearchUrl,
            String[] seriesDownloadTitleExclusions,
            UrlDownloadService downloadService) {
        super(downloadService);
        this.seriesSearchUrl = seriesSearchUrl;
        this.seriesDownloadTitleExclusions = seriesDownloadTitleExclusions;
    }

    @Override
    public Collection<SeriesDownload> findSeriesDownloads(List<Series> entities) {
        Collection<SeriesDownload> downloads = findSeriesDownloads(entities,
                seriesDownloadTitleExclusions);

        return downloads;
    }

    /**
     * This method finds any available downloads for the specified series.
     * 
     * @param listOfSeries
     *            the list of series to look for links of
     * @param titleExclusions
     * @return the collection of series downloads
     */
    private Collection<SeriesDownload> findSeriesDownloads(
            List<Series> listOfSeries, String[] titleExclusions) {
        Collection<SeriesDownload> downloads = new HashSet<SeriesDownload>();

        // Loop over each series and try to find any downloads
        for (Series series : listOfSeries) {
            SeriesDownloadFinder seriesDownloadFinder = new SeriesDownloadFinder(
                    series, this, titleExclusions);

            downloads.addAll(seriesDownloadFinder.findDownloads());
        }

        return downloads;
    }

    @Override
    protected Collection<RegexMatch> getLinkMatches(Series series) {

        String seriesSearchPage = getSeriesSearchPage(series);
        try {
            String homepageHtml = urlDownloadService
               .downloadUrlContents(seriesSearchPage);
            return RegexUtil.findRegexMatches(homepageHtml, getRegexPattern());
        } catch (IOException e) {
            LOG.error(String.format("Unable to download contents of url [%s]", seriesSearchPage), e);
            return Collections.emptyList();
        }
    }

    @Override
    protected String getSeriesSearchPage(Series series) {
        String seriesUrl = getSeriesDownloadUrl();

        Matcher matcher = SHOW_REPLACEMENT_PATTERN.matcher(seriesUrl);
        seriesUrl = matcher.replaceAll(series.getName());

        boolean initialSearch = series.getEpisode() == 1
                && series.getSeason() == 1;

        matcher = AGE_REPLACEMENT_PATTERN.matcher(seriesUrl);
        seriesUrl = matcher.replaceAll((initialSearch) ? INITIAL_AGE
                : DEFAULT_AGE);

        matcher = SPACE_REPLACEMENT_PATTERN.matcher(seriesUrl);
        seriesUrl = matcher.replaceAll(".");

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Series [%s] search url [%s]",
                    series.getName(), seriesUrl));
        }

        return seriesUrl;
    }

    String getSeriesDownloadUrl() {
        return seriesSearchUrl;
    }

    protected String getRegexPattern() {
        return LINK_REGEX;
    }
}
