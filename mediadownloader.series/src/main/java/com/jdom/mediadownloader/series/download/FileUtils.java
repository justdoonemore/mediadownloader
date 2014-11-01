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

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.jdom.mediadownloader.series.download.filter.AcceptAll;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Contains utility methods for working with files.
 * 
 * @author djohnson
 * 
 */
public class FileUtils {

    /**
     * Constant to control how many bytes to read from an input stream at a time.
     */
    public static final int BYTES_TO_READ_AT_A_TIME = 4096;

    /**
     * Constant representing nothing was read.
     */
    public static final int NOTHING_READ = -1;

    /**
     * Retrieve an input stream for a file.
     * 
     * @param file
     *            The file to get an inputstream to.
     * @return The inputstream or null if the file was not found
     */
    public static InputStream getInputStream(File file) {
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // Return null
        }

        return inputStream;
    }

    public static InputStream getInputStream(Class<?> clazz, String path) {
        return clazz.getResourceAsStream(path);
    }

    public static String readFile(Class<?> clazz, String path) {
    	InputStream is = null;
    	try {
            is = clazz.getResourceAsStream(path);

            StringWriter writer = new StringWriter();
            writer.append(new String(ByteStreams.toByteArray(is)));

            return writer.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to locate or read the file!", e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    /**
     * Retrieve an outputStream for the file.
     * 
     * @param file
     *            The file to write to
     * @return The output stream or null if the file was not found
     */
    public static OutputStream getOutputStream(File file) {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // return null
        }

        return outputStream;
    }

    /**
     * Retrieve an input stream for a file.
     * 
     * @param dir
     *            The directory where the file is located
     * @param file
     *            The file to get an inputstream to.
     * @return The inputstream or null if the file was not found
     */
    public static InputStream getInputStream(String dir, String file) {
        return FileUtils.getInputStream(new File(dir, file));
    }

    /**
     * Retrieve an input stream for a file.
     * 
     * @param filepath
     *            The path to the file
     * @return The inputstream or null if the file was not found
     */
    public static InputStream getInputStream(String filepath) {
        return FileUtils.getInputStream(new File(filepath));
    }

    /**
     * Reads a file fully and returns it as a byte array.
     * 
     * @param file
     *            The file to read
     * @return The byte array of the file contents or null if the file couldn't be read
     * @throws java.io.IOException
     *             If an error occurs reading the file
     */
    public static byte[] readFileFully(File file) {
        byte[] bytes = null;

        long length = file.length();

        bytes = new byte[(int) length];

        InputStream inputStream = FileUtils.getInputStream(file);

        if (inputStream != null) {

            int bytesIndex = 0;

            byte[] temp = new byte[BYTES_TO_READ_AT_A_TIME];

            int bytesRead;

            try {
                bytesRead = inputStream.read(temp);

                // Keep reading bytes until there's nothing left to read
                while (bytesRead != NOTHING_READ) {
                    // Copy them into the return array
                    for (int i = 0; i < bytesRead; i++) {
                        bytes[bytesIndex++] = temp[i];
                    }

                    bytesRead = inputStream.read(temp);
                }
            } catch (IOException e) {
                throw new FileException("Error reading the file!", e);
            } finally {
                Closeables.closeQuietly(inputStream);
            }
        }

        return bytes;
    }

    /**
     * Retrieves all files recursively.
     * 
     * @param directoryToStart
     *            The directory to start at
     * @return The matching files
     */
    public static File[] getFilesRecursivelyFromDirectory(File directoryToStart) {

        return FileUtils.getFilesRecursivelyFromDirectory(directoryToStart, null);
    }

    /**
     * Retrieves all files recursively.
     * 
     * @param directoryToStart
     *            The directory to start at
     * @param endsWith
     *            The pattern to match the ending/extension
     * @return The matching files
     */
    public static File[] getFilesRecursivelyFromDirectory(File directoryToStart, String endsWith) {

        // Accept files that end in a specific extension
        Collection<File> matches = new ArrayList<File>();

        if (directoryToStart == null) {
            return new File[0];
        }

        File[] possibles = directoryToStart.listFiles();

        if (possibles != null) {
            for (File possible : possibles) {
                if (possible.isHidden()) {
                    continue;
                }
                if (possible.isDirectory()) {
                    File[] results = getFilesRecursivelyFromDirectory(possible, endsWith);
                    if (results != null) {
                        matches.addAll(Arrays.asList(results));
                    }
                } else {
                    // If no extension was specified or the extension matches
                    if (endsWith == null || possible.getName().endsWith(endsWith)) {
                        matches.add(possible);
                    }
                }
            }
        }

        return matches.toArray(new File[matches.size()]);
    }

    public static Collection<File> getDirectoriesRecursivelyFromDirectory(File directoryToStart) {

        return FileUtils.getDirectoriesFromDirectory(directoryToStart, true);
    }

    /**
     * Writes a file to disk. Will delete an existing file of that name.
     * 
     * @param file
     *            The file to write to
     * @param bytes
     *            The data to write
     * 
     * @return Whether or not the file was written successfully
     */
    public static boolean writeFileToDisk(File file, byte[] bytes) {
        return writeFileToDisk(file, bytes, false);
    }

    /**
     * Writes a file to disk. Will delete an existing file of that name.
     * 
     * @param file
     *            The file to write to
     * @param bytes
     *            The data to write
     * @param useHiddenFile whether or not to use a hidden file first
     * 
     * @return Whether or not the file was written successfully
     */
    public static boolean writeFileToDisk(File file, byte[] bytes, boolean useHiddenFile) {

        boolean successfullyWritten = false;

        File parentFile = file.getParentFile();
        File fileToWriteTo = file;

        // If we are to use a hidden file, write to it
        if (useHiddenFile) {
            fileToWriteTo = new File(parentFile, "." + file.getName() + ".tmp");
        }

        if (!parentFile.exists()) {
            parentFile.mkdirs();
        } else if (fileToWriteTo.exists()) {
            fileToWriteTo.delete();
        }

        try {
            Files.write(bytes, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // If we used a hidden file, then
        // rename it now that it is written
        if (useHiddenFile) {
            if (file.exists()) {
                file.delete();
            }

            fileToWriteTo.renameTo(file);
        }

        successfullyWritten = true;

        return successfullyWritten;
    }

    /**
     * Writes a file to disk. Will delete an existing file of that name.
     * 
     * @param file
     *            The file to write to
     * @param bytes
     *            The data to write
     * 
     * @return Whether or not the file was written successfully
     */
    public static boolean writeFileToDisk(String file, byte[] bytes) {
        return writeFileToDisk(new File(file), bytes);
    }

    /**
     * Writes a file to disk. Will delete an existing file of that name.
     * 
     * @param file
     *            The file to write to
     * @param contents
     *            The data to write
     * 
     * @return Whether or not the file was written successfully
     */
    public static boolean writeFileToDisk(String file, String contents) {
        return writeFileToDisk(file, contents.getBytes());
    }

    /**
     * Writes a file to disk. Will delete an existing file of that name.
     * 
     * @param file
     *            The file to write to
     * @param contents
     *            The data to write
     * 
     * @return Whether or not the file was written successfully
     */
    public static boolean writeFileToDisk(File file, String contents) {
        return writeFileToDisk(file, contents, false);
    }

    /**
     * Writes a file to disk. Will delete an existing file of that name.
     * 
     * @param file
     *            The file to write to
     * @param contents
     *            The data to write
     * @param useHiddenFile whether or not to write to a hidden file first
     * 
     * @return Whether or not the file was written successfully
     */
    public static boolean writeFileToDisk(File file, String contents, boolean useHiddenFile) {
        return writeFileToDisk(file, contents.getBytes(), useHiddenFile);
    }

    /**
     * Get only files from the specified directory.
     * 
     * @param directory
     *            the directory to work on
     * @param recurse
     *            whether or not to act recursively
     * @return the collection of files
     */
    public static Collection<File> getFilesFromDirectory(File directory, boolean recurse) {
        return getFilesAndDirectories(directory, true, false, recurse);
    }

    /**
     * Method to get directories from the specified directory.
     * 
     * @param directory
     *            the starting directory
     * @param recurse
     *            whether or not to act recursively
     * @return the collection of directory object results
     */
    public static Collection<File> getDirectoriesFromDirectory(File directory, boolean recurse) {
        return getFilesAndDirectories(directory, false, true, recurse, new AcceptAll());
    }

    /**
     * Method to get directories from the specified directory.
     * 
     * @param directory
     *            the starting directory
     * @param recurse
     *            whether or not to act recursively
     * @param fileFilter
     *            the additional file filter to apply
     * @return the collection of directory object results
     */
    public static Collection<File> getDirectoriesFromDirectory(File directory, boolean recurse, FileFilter fileFilter) {
        return getFilesAndDirectories(directory, false, true, recurse, fileFilter);
    }

    /**
     * Method to get files and directories from the specified directory.
     * 
     * @param directory
     *            the starting directory
     * @param includeFiles
     *            whether or not to include files
     * @param includeDirectories
     *            whether or not to include directories
     * @param recurse
     *            whether or not to act recursively
     * @return the collection of file object results
     */
    public static Collection<File> getFilesAndDirectories(File directory, boolean includeFiles,
            boolean includeDirectories, boolean recurse) {
        return getFilesAndDirectories(directory, includeFiles, includeDirectories, recurse, new AcceptAll());
    }

    /**
     * Method to get files and directories from the specified directory.
     * 
     * @param directory
     *            the starting directory
     * @param includeFiles
     *            whether or not to include files
     * @param includeDirectories
     *            whether or not to include directories
     * @param recurse
     *            whether or not to act recursively
     * @param fileFilter
     *            an additional file filter to apply
     * @return the collection of file object results
     */
    public static Collection<File> getFilesAndDirectories(File directory, boolean includeFiles,
            boolean includeDirectories, boolean recurse, FileFilter fileFilter) {
        Collection<File> results = new ArrayList<File>();

        // For each file and directory in the target directory
        for (File f : directory.listFiles()) {
            // if it's a directory
            if (f.isDirectory()) {
                // and we are supposed to include them
                if (includeDirectories && fileFilter.accept(f)) {
                    // add it
                    results.add(f);
                }

                // if we are acting recursively get all of its results
                if (recurse) {
                    // and add them
                    results.addAll(getFilesAndDirectories(f, includeFiles, includeDirectories, recurse, fileFilter));
                }
            } else if (f.isFile() && includeFiles && fileFilter.accept(f)) {
                // or add the file if we are supposed to add them
                results.add(f);
            }
        }

        return results;
    }

    public static void deleteDirectory(File directory) {
        FileSystemUtils.deleteRecursively(directory);
    }

    public static void copyDirectory(File srcDir, File destDir) {
        try {
            FileSystemUtils.copyRecursively(srcDir, destDir);
        } catch (IOException e) {
            throw new FileException("Unable to copy directory!", e);
        }
    }

    public static void writeStringToFile(File file, String text) {
        try {
            Files.write(text.getBytes(), file);
        } catch (IOException e) {
            throw new FileException("Unable to write string to file!", e);
        }
    }

    public static void writeByteArrayToFile(File file, byte[] bytes) {
        try {
            Files.write(bytes, file);
        } catch (IOException e) {
            throw new FileException("Unable to write bytes to file!", e);
        }
    }

    public static void delete(File file) {
        FileSystemUtils.deleteRecursively(file);
    }
}
