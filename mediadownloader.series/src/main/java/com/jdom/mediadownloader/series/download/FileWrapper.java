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

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileWrapper extends File {

    private static final Logger LOG = LoggerFactory.getLogger(FileWrapper.class);

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 8796098653988152718L;

    private String baseName;

    private String extension;

    public FileWrapper(File file) {
        super(file.getAbsolutePath());

        setupExtension();
    }

    public FileWrapper(File parent, String child) {
        super(parent, child);

        setupExtension();
    }

    public FileWrapper(String parent, String child) {
        super(parent, child);

        setupExtension();
    }

    public FileWrapper(String pathname) {
        super(pathname);

        setupExtension();
    }

    public FileWrapper(URI uri) {
        super(uri);

        setupExtension();
    }

    private void setupExtension() {
        String filename = this.getName();
        int indexOfExtension = filename.lastIndexOf(".");

        if (indexOfExtension != -1) {
            baseName = filename.substring(0, indexOfExtension);
            extension = filename.substring(indexOfExtension);
        } else {
            baseName = filename;
        }
    }

    /**
     * Determine whether the file has been modified since the specified
     * milliseconds ago.
     *
     * @param milliseconds the milliseconds the file must have been modified in to return
     *                     true
     * @return true if the file has been modified in the last specified
     * milliseconds
     */
    public boolean hasBeenModifiedSince(long milliseconds) {
        if (this.isFile()) {
            return hasFileBeenModifiedSince(milliseconds);
        } else if (this.isDirectory()) {
            return hasDirectoryBeenModifiedSince(milliseconds);
        } else {
            throw new FileException(
                    "File does not seem to be a directory or a file!");
        }
    }

    public void copyTo(File directory) {
        if (this.isFile()) {
            copyFileTo(directory);
        } else if (this.isDirectory()) {
            copyDirectoryTo(directory);
        } else {
            throw new FileException(
                    "File does not seem to be a directory or a file!");
        }
    }

    public void moveTo(File destination) {
        moveTo(destination, false);
    }

    public void moveTo(File destination, boolean ignoreIfAlreadyExists) {
        try {
            if (this.isDirectory()) {
                for (File file : this.listFiles()) {
                    Files.move(file, new File(destination, file.getName()));
                }
                FileSystemUtils.deleteRecursively(this);
            } else {
                Files.move(this, destination);
            }
        } catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    private void copyDirectoryTo(File directory) {
        FileUtils.copyDirectory(this, directory);
    }

    private void copyFileTo(File destination) {

        byte[] contents = FileUtils.readFileFully(this);

        FileUtils.writeFileToDisk(destination, contents);
    }

    private boolean hasDirectoryBeenModifiedSince(long milliseconds) {
        boolean modifiedSince = false;

        File[] filesInDirectory = FileUtils
                .getFilesRecursivelyFromDirectory(this);

        for (File file : filesInDirectory) {
            FileWrapper wrappedFile = new FileWrapper(file);

            if (wrappedFile.hasBeenModifiedSince(milliseconds)) {
                modifiedSince = true;
                break;
            }
        }

        return modifiedSince;
    }

    private boolean hasFileBeenModifiedSince(long milliseconds) {
        long greatestAllowableLastModified = System.currentTimeMillis()
                - milliseconds;

        long fileLastModified = this.lastModified();

        if (LOG.isDebugEnabled()) {
            LOG.debug("File last modified [" + fileLastModified
                    + "] greatest allowable last modified ["
                    + greatestAllowableLastModified + "]");
        }

        boolean modifiedSinceGreatestAllowable = fileLastModified > greatestAllowableLastModified;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Has the file has been modified since ["
                    + greatestAllowableLastModified + "]? "
                    + modifiedSinceGreatestAllowable);
        }

        return modifiedSinceGreatestAllowable;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

}
