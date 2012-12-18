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
 */package com.jdom.mediadownloader.series.download.queue;

import com.jdom.mediadownloader.series.domain.Series;

public interface SeriesDownloadQueueManager {

    /**
     * Adds a series to the queue.
     * 
     * @param series
     *            the series
     * @return true if the series was added
     */
    boolean addSeries(Series series);

    /**
     * Removes the series from the queue.
     * 
     * @return true if the series was removed
     */
    boolean removeSeries(Series series);

    /**
     * Determine if the series is in the download queue.
     * 
     * @param series
     *            the series
     * @return true if the series is in the download queue
     */
    boolean containsSeries(Series series);

    /**
     * Removes all series that have passed the allowed purge interval.
     * 
     * @param olderThanMillis
     *            the milliseconds of life allowed for a series download
     */
    void purgeExpiredSeries(long olderThanMillis);
}
