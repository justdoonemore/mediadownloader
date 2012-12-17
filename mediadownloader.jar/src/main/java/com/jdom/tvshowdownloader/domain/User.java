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
 */package com.jdom.tvshowdownloader.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jdom.util.compare.CompareUtil;

@Entity
public class User extends AbstractEntity<User> {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1288444004592864249L;

	private int id;

	private String name;

	private String password;

	private String roles;

	private String emailAddress;

	public User() {

	}

	public User(String name, String password, String roles, String emailAddress) {
		this.name = name;
		this.password = password;
		this.roles = roles;
		this.emailAddress = emailAddress;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public int compareTo(User o) {
		int compared = this.getName().compareTo(o.getName());

		if (compared == 0) {
			compared = this.getRoles().compareTo(o.getRoles());

			if (compared == 0) {
				compared = this.getEmailAddress()
						.compareTo(o.getEmailAddress());
			}
		}

		return compared;
	}

	@Override
	public boolean equals(Object other) {
		boolean equal = false;

		if (other instanceof User) {
			User user = (User) other;

			equal = CompareUtil.isEqual(this.getName(), user.getName())
					&& CompareUtil.isEqual(this.getPassword(),
							user.getPassword())
					&& CompareUtil.isEqual(this.getRoles(), user.getRoles())
					&& CompareUtil.isEqual(this.getEmailAddress(),
							user.getEmailAddress());

		}

		return equal;
	}

	/**
	 * Retrieve the hashcode for this object.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getName())
				.append(this.getPassword()).append(this.getRoles())
				.append(this.getEmailAddress()).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("name [").append(this.getName())
				.append("]");
		sb.append(" id [").append(this.getId()).append("]");
		sb.append(" roles [").append(this.getRoles()).append("]");
		sb.append(" email [").append(this.getEmailAddress()).append("]");

		return sb.toString();
	}

	@Override
	public User clone() {
		return new User(this.getName(), this.getPassword(), this.getRoles(),
				this.getEmailAddress());
	}
}
