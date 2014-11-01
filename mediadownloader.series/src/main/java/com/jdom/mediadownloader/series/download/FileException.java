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

public class FileException extends RuntimeException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4639217059668640228L;

	public FileException() {
		super();
	}

	public FileException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FileException(String arg0) {
		super(arg0);
	}

	public FileException(Throwable arg0) {
		super(arg0);
	}

}
