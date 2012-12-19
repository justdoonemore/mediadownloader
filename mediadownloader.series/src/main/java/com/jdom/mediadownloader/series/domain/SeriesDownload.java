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
package com.jdom.mediadownloader.series.domain;

import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jdom.mediadownloader.domain.EntityDownload;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "seriesid",
		"season", "episode" }))
public class SeriesDownload extends EntityDownload<SeriesDownload, Series> {

	private static final long serialVersionUID = 1L;

	private int season;

	private int episode;

	private URL link;

	private String nzbTitle;

	public SeriesDownload() {
	}

	public SeriesDownload(Series series, int season, int episode, Date time) {
		this(series, null, season, episode, time, null);
	}

	public SeriesDownload(Series series, String nzbTitle, int season,
			int episode, Date time, URL url) {
		super(series, time);
		this.nzbTitle = nzbTitle;
		this.season = season;
		this.episode = episode;
		this.link = url;
	}

	@Override
	@ManyToOne(optional = false)
	@JoinColumn(name = "seriesid")
	public Series getEntity() {
		return entity;
	}

	@Override
	public void setEntity(Series entity) {
		this.entity = entity;
	}

	@Transient
	public String getName() {
		return entity.getName();
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	@Transient
	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	@Transient
	public String getNzbTitle() {
		return nzbTitle;
	}

	public void setNzbTitle(String nzbTitle) {
		this.nzbTitle = nzbTitle;
	}

	@Override
	public int compareTo(SeriesDownload o) {
		return this.getEntity().compareTo(o.getEntity());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[").append(this.getName())
				.append("]");
		sb.append(" season [").append(this.getSeason()).append("]");
		sb.append(" episode [").append(this.getEpisode()).append("]");
		sb.append(" time [").append(this.getTime()).append("]");

		return sb.toString();
	}

	@Override
	public SeriesDownload clone() {
		return new SeriesDownload(entity, season, episode, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SeriesDownload) {
			SeriesDownload other = (SeriesDownload) obj;

			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.entity, other.entity);
			builder.append(this.season, other.season);
			builder.append(this.episode, other.episode);
			builder.append(this.time, other.time);

			return builder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getEntity());
		builder.append(this.getSeason());
		builder.append(this.getEpisode());
		builder.append(this.getTime());

		return builder.toHashCode();
	}
}
