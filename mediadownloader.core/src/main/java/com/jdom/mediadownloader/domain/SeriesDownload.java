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
package com.jdom.mediadownloader.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "seriesid",
		"season", "episode" }))
public class SeriesDownload extends AbstractEntity<SeriesDownload> {

	private static final long serialVersionUID = 1L;

	private Series series;

	private int id;

	private int season;

	private int episode;

	private Date time;

	public SeriesDownload() {
	}

	public SeriesDownload(Series series, int season, int episode, Date time) {
		this.series = series;
		this.season = season;
		this.episode = episode;
		this.time = time;
	}

	/**
	 * Auto-generated primary key.
	 * 
	 * @return the id for this entity object
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "seriesid")
	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Transient
	public String getName() {
		return series.getName();
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

	@Override
	public int compareTo(SeriesDownload o) {
		return this.getSeries().compareTo(o.getSeries());
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
		return new SeriesDownload(series, season, episode, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SeriesDownload) {
			SeriesDownload other = (SeriesDownload) obj;

			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.series, other.series);
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
		builder.append(this.getSeries());
		builder.append(this.getSeason());
		builder.append(this.getEpisode());
		builder.append(this.getTime());

		return builder.toHashCode();
	}
}
