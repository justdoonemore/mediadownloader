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

public class RegexMatch {

	private String match;

	private String[] groups;

	private int groupCount;

	public RegexMatch(String match, String[] groups) {
		this.match = match;
		this.groupCount = groups.length;
		this.groups = groups;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public String getGroup(int groupNumber) {
		return groups[groupNumber];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Match [" + match + "]").append("\n");

		for (int i = 0; i < groupCount; i++) {
			builder.append("Group[" + i + "] = [" + groups[i] + "]");
		}

		return builder.toString();
	}
}
