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
package com.jdom.mediadownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

/**
 * @author djohnson
 * 
 */
public class FileLockApplicationLock implements ApplicationLock {

	private static final Logger LOG = LoggerFactory
			.getLogger(FileLockApplicationLock.class);

	private final File file;

	private FileOutputStream fos;
	private FileLock fl;

	public FileLockApplicationLock(File file) {
		this.file = file;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.ApplicationLock#tryLock()
	 */
	@Override
	public boolean tryLock() throws LockException {
		try {
			fos = new FileOutputStream(file);
			fl = fos.getChannel().tryLock();
		} catch (IOException e) {
			throw new LockException(e);
		}

		return fl != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.ApplicationLock#unlock()
	 */
	@Override
	public void unlock() {
		if (fl != null) {
			try {
				fl.release();
			} catch (IOException ioe) {
				LOG.error("Unable to release file lock!", ioe);
			}
		}
		if (fos != null) {
			try {
				Closeables.close(fos, true);
			} catch (IOException e) {
				LOG.error("Unable to close output stream!", e);
			}
		}
	}

}
