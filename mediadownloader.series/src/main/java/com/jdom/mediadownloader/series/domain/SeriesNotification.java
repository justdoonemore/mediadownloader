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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jdom.mediadownloader.domain.AbstractEntity;
import com.jdom.mediadownloader.domain.User;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "seriesid",
		"userid" }))
public class SeriesNotification extends AbstractEntity<SeriesNotification> {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 129037798432143L;

	private int id;

	private Series series;

	private User user;

	/**
	 * Default constructor required by JPA.
	 */
	public SeriesNotification() {

	}

	public SeriesNotification(User user, Series series) {
		this.user = user;
		this.series = series;
	}

	@Override
	public int compareTo(SeriesNotification other) {
		int compared = series.compareTo(other.getSeries());

		if (compared == 0) {
			compared = user.compareTo(other.getUser());
		}

		return compared;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SeriesNotification) {
			SeriesNotification that = (SeriesNotification) other;

			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.getSeries(), that.getSeries());
			builder.append(this.getUser(), that.getUser());
			return builder.isEquals();
		}

		return false;
	}

	/**
	 * Retrieve the hashcode for this object.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getSeries()).append(getUser())
				.toHashCode();
	}

	/**
	 * Auto-generated primary key.
	 * 
	 * @return the id for this entity object
	 */
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	@Override
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "userid")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "[" + getUser().getName() + "] [" + getSeries().getName() + "]";
	}

	@Override
	public SeriesNotification clone() {
		return new SeriesNotification(this.getUser(), this.getSeries());
	}
}
