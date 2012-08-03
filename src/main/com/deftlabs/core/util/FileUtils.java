/**
 * (C) Copyright 2007, Deft Labs.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deftlabs.core.util;

// OEMware
import com.deftlabs.core.OemException;

// Java
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Date;
import java.util.Calendar;
import java.util.Properties;
import java.util.InvalidPropertiesFormatException;
import java.util.UUID;

/**
 * The file utils.
 */
public final class FileUtils {

    public static final int DEFAULT_MAX_FILE_NAMES = 10000;

    private static final char SLASH = '/';
    private static final char DASH = '-';
    private static final char PERIOD = '.';

    private static final String DASH_STR = "-";
    private static final String EMPTY_STR = "";
    private static final String PERIOD_STR = ".";
    private static final int MAX_EXT_LENGTH = 7;

    /**
     * Writes the entire input stream to the file. This class creates a 1024
     * byte buffer for reading.
     * @param pInputStream The is.
     * @param pFileName The file name.
     * @throws OemException
     */
    public static final void writeInputStreamToFile(final InputStream pInputStream,
                                                    final String pFileName)
        throws OemException
    {
        final byte [] buffer = new byte[1024];
        writeInputStreamToFile(pInputStream, pFileName, buffer);
    }

    /**
     * Returns the file file path.
     * @param pFile The file.
     * @return The file path.
     */
    public static String getFileParent(final String pFile)
    { return getFileParent(new File(pFile)); }

    /**
     * Returns the file file path.
     * @param pFile The file.
     * @return The file path.
     */
    public static String getFileParent(final File pFile) {
        return pFile.getParent();
    }

    /**
     * Writes the entire input stream to the file.
     * @param pInputStream The is.
     * @param pReadBufferSize The read buffer size.
     * @param pFilePath The directory to store the file in.
     * @return The name of the new file.
     * @throws OemException
     */
    public static final String writeInputStreamToFile(  final InputStream pInputStream,
                                                        final int pReadBufferSize,
                                                        final String pFilePath)
        throws OemException
    {
        final String fileName = createUuidFileName(pFilePath);

        writeInputStreamToFile( pInputStream,
                                fileName,
                                new byte[pReadBufferSize]);

        return fileName;
    }

    /**
     * Writes the entire input stream to the file.
     * @param pInputStream The is.
     * @param pReadBuffer The read buffer.
     * @param pFilePath The directory to store the file in.
     * @return The total number of bytes read.
     * @throws OemException
     */
    public static final int writeInputStreamToFile( final InputStream pInputStream,
                                                    final byte [] pReadBuffer,
                                                    final String pFilePath)
        throws OemException
    {
        final String fileName = pFilePath + UUID.randomUUID().toString();
        return writeInputStreamToFile(pInputStream, fileName, pReadBuffer);
    }

    /**
     * Writes the entire input stream to the file.
     * @param pInputStream The is.
     * @param pFileName The file name.
     * @param pReadBufferSize The read buffer size.
     * @return The total number of bytes read.
     * @throws OemException
     */
    public static final int writeInputStreamToFile( final InputStream pInputStream,
                                                    final String pFileName,
                                                    final int pReadBufferSize)
        throws OemException
    {
        return writeInputStreamToFile(  pInputStream,
                                        pFileName,
                                        new byte[pReadBufferSize]);
    }

    /**
     * Writes the string to to a file.
     * @param pContent The content.
     * @param pFileName The file name.
     * @throws OemException
     */
    public static final void writeStrToFile(final String pContent,
                                            final String pFileName)
        throws OemException
    {
        FileWriter writer = null;
        try {
            final File file = createFile(pFileName);
            writer = new FileWriter(file);
            writer.write(pContent);

        } catch (IOException ioe) { throw new OemException(ioe);
        } finally {
            try { if (writer != null) writer.close();
            } catch (IOException ioe) { throw new OemException(ioe); }
        }
    }

    /**
     * Writes the entire input stream to the file.
     * @param pInputStream The is.
     * @param pFileName The file name.
     * @param pReadBuffer The read buffer.
     * @return The total number of bytes read.
     * @throws OemException
     */
    public static final int writeInputStreamToFile( final InputStream pInputStream,
                                                    final String pFileName,
                                                    final byte [] pReadBuffer)
        throws OemException
    {
        final File file = createFile(pFileName);
        FileOutputStream os = null;
        int totalBytesRead = 0;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            while (true) {
                bytesRead = pInputStream.read(pReadBuffer, 0, pReadBuffer.length);
                if (bytesRead == -1) break;
                totalBytesRead += bytesRead;
                os.write(pReadBuffer, 0, bytesRead);
            }

            return totalBytesRead;

        } catch (IOException ioe) { throw new OemException(ioe);
        } finally { closeFileOutputStream(os); }
    }

    /**
     * Write the entire byte buffer to a file. This calls clear on the buffer.
     * @param pBuffer The buffer.
     * @param pFileName The file name.
     * @throws OemException
     */
    public static final void writeBufferToFile( final ByteBuffer pBuffer,
                                                final String pFileName)

        throws OemException
    { writeBufferToFile(pBuffer, pFileName, null); }

    /**
     * Read a file to a memory mapped buffer (read only).
     * @param pFileName The file name.
     * @throws OemException
     */
    public static final ByteBuffer mapFileToBuffer( final String pFileName)
        throws OemException
    {
        if (!fileExists(pFileName)) {
            throw new OemException("file not found: " + pFileName);
        }

        try {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(pFileName);

                final FileChannel channel = fis.getChannel();
                final long fileSize = channel.size();

                return channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            } finally { if (fis != null) fis.close(); }
        } catch (Throwable t) {
            throw new OemException(("error on file: " + pFileName), t);
        }
    }

    /**
     * Write the entire byte buffer to a file. This calls clear on the buffer.
     * @param pBuffer The buffer.
     * @param pFileName The file name.
     * @param pTmpFileName The temp file name if null, doesn't use.
     * @throws OemException
     */
    public static final void writeBufferToFile( final ByteBuffer pBuffer,
                                                final String pFileName,
                                                final String pTmpFileName)
        throws OemException
    {
        File outFile = null;
        if (pTmpFileName != null) outFile = new File(pTmpFileName);
        else outFile = new File(pFileName);

        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFile);
                final FileChannel channel = fos.getChannel();
                final long size = pBuffer.limit();
                long written = 0;
                pBuffer.rewind();
                while (true) {
                   written += channel.write(pBuffer);
                   if (written == size) break;
                }
            } finally { if (fos != null) fos.close(); }

            if (pTmpFileName != null) {
                FileUtils.renameFile(pTmpFileName, pFileName);
            }
        } catch (Throwable t) {
            // Try and delete the files.
            deleteFile(pFileName);
            if (pTmpFileName != null) deleteFile(pTmpFileName);
            throw new OemException(("error on file: "
                                    + pFileName
                                    + " - tmp file: "
                                    + pTmpFileName
                                    + " - message: "
                                    + t.getMessage()),
                                    t);
        }
    }

    public static final void deleteFile(final File pFile)
        throws OemException
    {
        if (!fileExists(pFile)) return;
        if (!pFile.delete()) {
            throw new OemException(("unable to delete file: "
                                    + pFile.getName()));
        }
    }

    /**
     * Delete the file.
     * @param pFileName The file name.
     * @throws OemException
     */
    public static final void deleteFile(final String pFileName)
        throws OemException
    {
        if (!fileExists(pFileName)) return;
        final File file = new File(pFileName);
        if (!file.delete()) {
            throw new OemException(("unable to delete file: " + pFileName));
        }
    }

    /**
     * Returns files from the data dir based on the max allowed.
     * @return The file names.
     */
    public static final String [] getFileNames( final String pDataDir,
                                                final String pFilter)
    { return getFileNames(pDataDir, pFilter, false); }

    /**
     * Returns files from the data dir based on the max allowed.
     * @return The file names.
     */
    public static final String [] getFileNames( final String pDataDir,
                                                final String pFilter,
                                                final boolean pExclude)
    { return getFileNames(pDataDir, pFilter, pExclude, false); }

    /**
     * Returns files from the data dir based on the max allowed.
     * @param pDataDir The data directory.
     * @param pExclude Set to true to get all the files but this filter.
     * @param pEndsWith Set to true to indicate that the filter be the end
     * value.
     * @return The files names.
     */
    public static final String [] getFileNames( final String pDataDir,
                                                final String pFilter,
                                                final boolean pExclude,
                                                final boolean pEndsWith)
    { return getFileNames(pDataDir, pFilter, pExclude, pEndsWith, false); }

    /**
     * Returns files from the data dir based on the max allowed.
     * @param pDataDir The data directory.
     * @param pExclude Set to true to get all the files but this filter.
     * @param pEndsWith Set to true to indicate that the filter be the end
     * value.
     * @param pRecursive Set to true to walk the directories below. Uses the
     * DEFAULT_MAX_FILE_NAMES (10,000).
     * @return The files names.
     */
    public static final String [] getFileNames( final String pDataDir,
                                                final String pFilter,
                                                final boolean pExclude,
                                                final boolean pEndsWith,
                                                final boolean pRecursive)
    {
        return getFileNames(pDataDir,
                            pFilter,
                            pExclude,
                            pEndsWith,
                            false,
                            DEFAULT_MAX_FILE_NAMES);
    }

    /**
     * Returns files from the data dir based on the max allowed.
     * @param pDataDir The data directory.
     * @param pExclude Set to true to get all the files but this filter.
     * @param pEndsWith Set to true to indicate that the filter be the end
     * value.
     * @param pRecursive Set to true to walk the directoeis below.
     * @param pMaxFiles The maximum number of files to return. This only
     * applies if to recursive is set to true.
     * @return The files names.
     */
    public static final String [] getFileNames( final String pDataDir,
                                                final String pFilter,
                                                final boolean pExclude,
                                                final boolean pEndsWith,
                                                final boolean pRecursive,
                                                final int pMaxFiles)
    {
        final File dataDir = new File(pDataDir);
        if (!dataDir.exists())
        { throw new IllegalStateException("invalid dir: " + pDataDir); }

        final FilenameFilter filter
        = getFilenameFilter(pFilter, pExclude, pEndsWith);

        if (!pRecursive) return dataDir.list(filter);

        return getFileNamesRecursive(pDataDir, filter, pMaxFiles);
    }

    /**
     * Do a recursive file find.
     * @param pDataDir The data directory.
     * @param pFilter The file filter.
     * @param pMaxFiles The maximum number of files.
     * @return The file names.
     */
    public static final String [] getFileNamesRecursive(final String pDataDir,
                                                        final FilenameFilter pFilter,
                                                        final int pMaxFiles)
    {
        final File currentDir = new File(pDataDir);
        // Check to see if it's a directory.
        if (!isDir(currentDir))
        { throw new IllegalStateException("Not a dir: " + pDataDir); }

        final LinkedList<String> files = new LinkedList<String>();
        final File [] listFiles = currentDir.listFiles();

        if (listFiles != null) {
            for (final File listFile : listFiles ) {
                if (files.size() == pMaxFiles) break;
                if (listFile.isDirectory()) {
                    final String [] newFiles
                    = getFileNamesRecursive(listFile.getAbsolutePath(),
                                            pFilter,
                                            pMaxFiles);

                    for (final String newFile : newFiles) {
                        if (files.size() == pMaxFiles) break;
                        files.addLast(newFile);

                    }
                } else {
                    if (pFilter.accept(currentDir, listFile.getName())) {
                        if (files.size() == pMaxFiles) break;
                        files.addLast(listFile.getAbsolutePath());
                    }
                }
            }
        }

        return files.toArray(new String[files.size()]);
    }

    /**
     * Load the file input streams for the files passed.
     * @param pFiles The files.
     * @return The input streams.
     * @throws OemException
     */
    public static final List<FileInputStream> loadInputStreams(final List<File> pFiles)
        throws OemException
    {
        List<FileInputStream> streams
            = new ArrayList<FileInputStream>(pFiles.size());

        for (final File file : pFiles) {
            try { streams.add(new FileInputStream(file));
            } catch (FileNotFoundException fnfe) {
                throw new OemException(fnfe);
            }
        }
        return streams;
    }



    /**
     * Close the input streams.
     * @param pInputStreams The input streams.
     * @throws OemException
     */
    public static final void closeFileInputStreams(final List<FileInputStream> pInputStreams)
        throws OemException
    {
        for (final FileInputStream stream : pInputStreams) {
            closeFileInputStream(stream);
        }
    }

    /**
     * Close the input stream.
     * @param pInputStream The input stream.
     * @throws OemException
     */
    public static final void closeFileInputStream(final FileInputStream pInputStream)
        throws OemException
    {
        if (pInputStream == null) return;
        try { pInputStream.close();
        } catch (IOException ioe) { throw new OemException(ioe); }
    }

    /**
     * Close the input stream.
     * @param pInputStream The input stream.
     * @throws OemException
     */
    public static final void closeInputStream(final InputStream pInputStream)
        throws OemException
    {
        if (pInputStream == null) return;
        try { pInputStream.close();
        } catch (IOException ioe) { throw new OemException(ioe); }
    }

    /**
     * Close the output stream.
     * @param pOutputStream The file.
     * @throws OemException
     */
    public static final void closeFileOutputStream(final FileOutputStream pOutputStream)
        throws OemException
    {
        if (pOutputStream == null) return;
        try { pOutputStream.close();
        } catch (IOException ioe) { throw new OemException(ioe); }
    }

    /**
     * Load a file channel.
     * @param pFileName The file name.
     * @param pExists The flag that indicates if an error should be
     * thrown if the file doesn't exist.
     * @return The file channel.
     * @throws OemException
     */
    public static final FileInputStream createFileInputStream(  final String pFileName,
                                                                final boolean pExists)
        throws OemException
    {
        final File file = new File(pFileName);
        final boolean fileExists = fileExists(file);

        // If we're expecting this file to be here and it's not, throw an
        // exception.
        if (!fileExists && pExists) {
            throw new OemException("File not found (expected) name: "
                                    + pFileName);
        }

        try {
            // Create a new file.
            if (!fileExists) {
                file.mkdirs();
                file.createNewFile();
            }

            return (new FileInputStream(file));
        } catch (FileNotFoundException fnfe) {
            throw new OemException(("file not found: " + pFileName), fnfe);
        } catch (IOException ioe) {
            throw new OemException(("file: " + pFileName), ioe);
        }
    }

    /**
     * Load the file channels for the files passed.
     * @param pInputStreams The input streams.
     * @return The file channels.
     * @throws FileNotFoundException
     */
    public static final List<FileChannel> loadChannels(final List<FileInputStream> pInputStreams)
    {
        List<FileChannel> channels
            = new ArrayList<FileChannel>(pInputStreams.size());

        for (final FileInputStream inputStream: pInputStreams) {
            channels.add(inputStream.getChannel());
        }
        return channels;
    }

    /**
     * Load all (or max) of the file input streams.
     * @param pDataDir The data dir.
     * @param pFilter The filter.
     * @param pMax The maximum number of files to process.
     */
    public static final List<File> loadFiles(   final String pDataDir,
                                                final String pFilter,
                                                final int pMax)
    { return loadFiles(pDataDir, pFilter, pMax, false); }

    /**
     * Load all (or max) of the file input streams.
     * @param pDataDir The data dir.
     * @param pFilter The filter.
     * @param pMax The maximum number of files to process.
     */
    public static final List<File> loadFiles(   final String pDataDir,
                                                final String pFilter,
                                                final int pMax,
                                                final boolean pEndsWith)
    {
        final List<File> files = new ArrayList<File>();
        final String [] fileNames
            = getFileNames(pDataDir, pFilter, false, pEndsWith);
        if (fileNames == null || fileNames.length == 0) return files;

        for (int idx=0; idx < fileNames.length; idx++) {
            if (idx == (pMax - 1)) break;
            files.add(new File(pDataDir + "/" + fileNames[idx]));
        }

        return files;
    }

    /**
     * Load all (or max) of the file names.
     * @param pDataDir The data dir.
     * @param pFilter The filter.
     * @param pMax The maximum number of files to process.
     */
    public static final String [] loadFileNames(final String pDataDir,
                                                final String pFilter,
                                                final int pMax,
                                                final boolean pEndsWith)
    {
        final String [] fileNames
            = getFileNames(pDataDir, pFilter, false, pEndsWith);
        if (fileNames == null || fileNames.length == 0) return fileNames;

        int fileCount = fileNames.length;
        if (fileCount >= pMax) fileCount = pMax;
        final String [] returnValues = new String[fileCount];

        System.arraycopy(fileNames, 0, returnValues, 0, fileCount);
        return returnValues;
    }

    /**
     * Reads a file and closes the file stream/channel. WARNING: This
     * loads the entire file into memory. Do this for known "small" files.
     * @param pFileName The file name.
     * @throws OemException
     */
    public static final String readFileStr(final String pFileName)
        throws OemException
    { return readFileStr(new File(pFileName)); }

    /**
     * Reads a file and closes the file stream/channel. WARNING: This
     * loads the entire file into memory. Do this for known "small" files.
     * @param pFile The file.
     * @throws OemException
     */
    public static final String readFileStr(final File pFile)
        throws OemException
    {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(pFile, "r");
            try {
                final int length = (int)file.length();
                final byte [] buffer = new byte[length];
                file.readFully(buffer, 0, length);
                return new String(buffer);
            } finally { if (file != null) file.close(); }
        } catch (IOException ioe) { throw new OemException(ioe); }
    }

    /**
     * Reads a file and closes the file stream/channel. WARNING: This
     * loads the entire file into memory. Do this for known "small" files.
     * @param pFile The file.
     * @throws OemException
     */
    public static final ByteBuffer readFileBuffer(final String pFile)
        throws OemException
    { return readFileBuffer(new File(pFile)); }

    /**
     * Reads a file and closes the file stream/channel. WARNING: This
     * loads the entire file into memory. Do this for known "small" files.
     * @param pFile The file.
     * @throws OemException
     */
    public static final ByteBuffer readFileBuffer(final File pFile)
        throws OemException
    {
        try {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(pFile);
                final FileChannel fc = fis.getChannel();
                final long fileSize = fc.size();
                long bytesRead = 0;
                long total = 0;

                final ByteBuffer buffer = ByteBuffer.allocate((int)fileSize);

                while (true) {
                    bytesRead = fc.read(buffer);
                    if (bytesRead == -1) break;
                    total += bytesRead;
                    if (total == fileSize) break;
                }

                return buffer;
            } finally { if (fis != null) fis.close(); }
        } catch (IOException ioe) { throw new OemException(ioe); }
    }

    /**
     * Returns the file filter object. This checks the cache based on the
     * filter value.
     * @param pFilter The filter.
     * @param pExclude The exclude flag.
     */
    public static final FilenameFilter getFilenameFilter(   final String pFilter,
                                                            final boolean pExclude)
    { return getFilenameFilter(pFilter, pExclude, false); }

    /**
     * Returns the file filter object. This checks the cache based on the
     * filter value. This method can be refactored :-) In a hurry now,.
     * @param pFilter The filter.
     * @param pExclude The exclude flag.
     * @param pEndsWith  Make sure the suffix is there (in the filter).
     */
    public static final FilenameFilter getFilenameFilter(   final String pFilter,
                                                            final boolean pExclude,
                                                            final boolean pEndsWith)
    {
        FilenameFilter filter;
        if (pExclude) {

            if (pEndsWith) {
                filter = new FilenameFilter() {
                    public boolean accept(final File pFile, final String pName) {
                        return !pName.endsWith(pFilter);
                    }
                };
            } else {
                filter = new FilenameFilter() {
                    public boolean accept(final File pFile, final String pName) {
                        return (pName.indexOf(pFilter) == -1)  ? true : false;
                    }
                };
            }
        } else {
            if (pEndsWith) {
                filter = new FilenameFilter() {
                    public boolean accept(final File pFile, final String pName) {
                        return pName.endsWith(pFilter);
                    }
                };
            } else {
                filter = new FilenameFilter() {
                    public boolean accept(final File pFile, final String pName) {
                        return (pName.indexOf(pFilter) != -1) ? true : false;
                    }
                };
            }
        }

        return filter;
    }

    /**
     * Create a new file with a UUID file name.
     * @param pFilePath The file path. This must end with a slash ( / or \ ).
     * @return The new file.
     * @throws OemException
     */
    public static final File createUuidFile(final String pFilePath)
        throws OemException
    { return createFile(createUuidFileName(pFilePath)); }

    /**
     * Create a new file with a UUID file name.
     * @param pFilePath The file path. This must end with a slash ( / or \ ).
     * @return The new file.
     * @throws OemException
     */
    public static final String createUuidFileName(final String pFilePath)
        throws OemException
    { return (pFilePath + UUID.randomUUID().toString()); }

    /**
     * Extract the directory from the absolute file name.
     * @param pFileName The file name.
     * @return The directory <b>without</b> the trailing slash /.
     */
    public static String extractDir(final String pFileName) {
        return pFileName.substring(0, pFileName.lastIndexOf(SLASH));
    }

    /**
     * Extract the file extension. Does not include the period.
     * @param pFileName The file name.
     * @return Returns the file extension or null if not available.
     */
    public static String extension(final String pFileName) {
        final int idx = pFileName.lastIndexOf(PERIOD);
        if (idx == -1) return null;
        final String ext = pFileName.substring(idx+1, pFileName.length());

        // Check and see if it found a period in the middle.
        if (ext.length() > MAX_EXT_LENGTH) return null;
        if (ext.indexOf(SLASH) != -1) return null;
        return ext;
    }

    /**
     * Create a new file. If the file exisits, an exception is thrown.
     * @param pFileName The file name.
     * @return The new file.
     * @throws OemException
     */
    public static final File createFile(final String pFileName)
        throws OemException
    {
        // Create the parent directory.
        createDir(extractDir(pFileName));

        final File file = new File(pFileName);

        if (fileExists(file))
        { throw new OemException("New file exists: " + pFileName); }

        try { file.createNewFile();
        } catch (FileNotFoundException fnfe)
        { throw new OemException(("file not found: " + pFileName), fnfe);
        } catch (IOException ioe)
        { throw new OemException(("file: " + pFileName), ioe);
        } catch (SecurityException se)
        { throw new OemException("security: " + pFileName, se); }

        return file;
    }

    /**
     * Make sure all the directories are in place for the path. If already
     * there, does nothing.
     * @param pDir The directory we're looking for.
     * @throws OemException
     */
    public static final void createDir(final String pDir)
        throws OemException
    {
        final File file = new File(pDir);
        try { if (!file.exists()) file.mkdirs();
        } catch (SecurityException se) { throw new OemException(se); }
    }

    /**
     * Rename the file.
     * @param pBefore The current name.
     * @param pAfter THe new name.
     * @throws OemException
     */
    public final static void renameFile(final String pBefore,
                                        final String pAfter)
        throws OemException
    { renameFile(new File(pBefore), new File(pAfter)); }

    /**
     * Rename the file.
     * @param pBefore The current name.
     * @param pAfter THe new name.
     * @throws OemException
     */
    public final static void renameFile(final File pBefore,
                                        final File pAfter)
        throws OemException
    { renameFile(pBefore, pAfter, 0); }

    /**
     * Rename a file with a retry.
     * @param pFrom The from file.
     * @param pTo The to file.
     * @param pRetrySleep The retry sleep on failure. Set to zero for
     * no retry.
     * @throws OemException
     */
    public final static void renameFile(final File pFrom,
                                        final File pTo,
                                        final long pRetrySleep)
        throws OemException
    {
        if (!pFrom.renameTo(pTo)) {
            if (pRetrySleep == 0) {
                throw new OemException("unable to rename file: "
                                        + pFrom
                                        + " - to: "
                                        + pTo);
            }

            try { Thread.sleep(pRetrySleep);
            } catch (InterruptedException ie) { }

            if (!pFrom.renameTo(pTo)) {
                throw new OemException("unable to rename file: "
                                        + pFrom
                                        + " - to: "
                                        + pTo
                                        + " - second attempt");
            }
        }
    }

    /**
     * Move a file.
     * @param pFile The from file.
     * @param pToDir The directory to file.
     * @throws OemException
     */
    public final static void moveFile(final String pFile,
                                      final String pToDir)
        throws OemException
    { moveFile(new File(pFile), new File(pToDir)); }

    /**
     * Move a file.
     * @param pFile The from file.
     * @param pToDir The directory to file.
     * @throws OemException
     */
    public final static void moveFile(final File pFile,
                                      final File pToDir)
        throws OemException
    {
        if (!pToDir.exists()) pToDir.mkdirs();

        if (!pFile.renameTo(new File(pToDir, pFile.getName()))) {
            throw new OemException("Unable to move file: "
                                    + pFile
                                    + " - to dir: "
                                    + pToDir);
        }
    }

    /**
     * Retrurns a file's last modified time.
     * @param pFile The file.
     * @return The last modified time.
     * @throws OemException
     */
    public final static long lastModifiedTime(final File pFile)
        throws OemException
    {
        if (pFile == null) throw new OemException("file is null");
        try { return pFile.lastModified();
        } catch (SecurityException se) { throw new OemException(se); }
    }

    /**
     * Store properties object to xml.
     * @param pFileName The file name.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void storeXmlProperties(final String pFileName,
                                                final Properties pProperties)
        throws OemException
    { storeProperties(pFileName, pProperties, true); }

    /**
     * Store properties object.
     * @param pFileName The file name.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void storeProperties(   final String pFileName,
                                                final Properties pProperties)
        throws OemException
    { storeProperties(pFileName, pProperties, false); }

    /**
     * Store properties object.
     * @param pFileName The file name.
     * @param pProperties The properties.
     * @param pUseXml The xml flag.
     * @throws OemException
     */
    public final static void storeProperties(   final String pFileName,
                                                final Properties pProperties,
                                                final boolean pUseXml)
        throws OemException
    {
        if (pProperties == null) {
            throw new OemException("properties object is null");
        }

        writableFile(pFileName);

        FileOutputStream outputStream = null;
        try {

            Date storeDate = new Date(System.currentTimeMillis());

            StringBuilder comment = new StringBuilder("modified on: ");
            comment.append(storeDate.toString());

            outputStream  = new FileOutputStream(pFileName);

            if (pUseXml) {
                pProperties.storeToXML( outputStream,
                                        comment.toString(),
                                        "UTF-8");
            } else {
                pProperties.store(outputStream, comment.toString());
            }

        } catch (InvalidPropertiesFormatException ipfe) {
            String errorMessage
                = "invalid file format - name: "
                + pFileName
                + " - root error message: "
                + ipfe.getMessage();

            throw new OemException(errorMessage, ipfe);
        } catch (IOException ioe) { throw new OemException(ioe);
        } catch (ClassCastException cce) { throw new OemException(cce);
        } finally {
            if (outputStream == null) return;
            try {
                outputStream.close();
                outputStream = null;
            } catch (IOException ioe) { throw new OemException(ioe); }
        }
    }

    /**
     * Refresh the values in a properties object with the values
     * in the file system.
     * @param pFile The file.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void loadProperties( final File pFile,
                                             final Properties pProperties)
        throws OemException
    { loadProperties(pFile, pProperties, false); }

    /**
     * Refresh the values in a properties object with the values
     * in the file system.
     * @param pFileName The file name.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void loadProperties( final String pFileName,
                                             final Properties pProperties)
        throws OemException
    { loadProperties(new File(pFileName), pProperties, false); }

    /**
     * Refresh the values in a properties object with the values
     * in the file system.
     * @param pFileName The file name.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void loadXmlProperties( final String pFileName,
                                                final Properties pProperties)
        throws OemException
    { loadXmlProperties(new File(pFileName), pProperties); }

    /**
     * Refresh the values in a properties object with the values
     * in the file system.
     * @param pFile The file.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void loadXmlProperties( final File pFile,
                                                final Properties pProperties)
        throws OemException
    { loadProperties(pFile, pProperties, true); }

    /**
     * Refresh the values in a properties object with the values
     * in the file system.
     * @param pFile The file name.
     * @param pProperties The properties.
     * @throws OemException
     */
    public final static void loadProperties(final File pFile,
                                            final Properties pProperties,
                                            final boolean mIsXml)
        throws OemException
    {
        if (pProperties == null) {
            throw new OemException("properties object is null");
        }

        // Make sure the file exists.
        readableFile(pFile);

        FileInputStream inputStream = null;
        try {

            inputStream = new FileInputStream(pFile);

            if (mIsXml) pProperties.loadFromXML(inputStream);
            else pProperties.load(inputStream);

        } catch (InvalidPropertiesFormatException ipfe) {

            String errorMessage
                = "invalid file format - name: "
                + pFile.getName()
                + " - root error message: "
                + ipfe.getMessage();
            throw new OemException(errorMessage, ipfe);

        } catch (IOException ioe) {
            throw new OemException(ioe);
        } finally {
            if (inputStream == null) return;
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException ioe) { throw new OemException(ioe); }
        }
    }

    /**
     * Load properties from an xml config file. This checks to see if the
     * file is there (see readableFile(fileName). This creates a new
     * properties object.
     * @param pFileName The file name.
     * @return The properties object.
     * @throws OemException
     */
    public final static Properties loadXmlProperties(final String pFileName)
        throws OemException
    {
        final Properties properties = new Properties();
        loadXmlProperties(pFileName, properties);
        return properties;
    }

    /**
     * Checks to see if the object passed is a file.
     * @param pFileName The file name.
     * @return True if the file exists.
     */
    public final static boolean fileExists(final String pFileName) {
        try { isFile(pFileName); return true;
        } catch (OemException ce) { return false; }
    }

    /**
     * Checks to see if the object passed is a file.
     * @param pFile The file.
     * @return True if the file exists.
     */
    public final static boolean fileExists(final File pFile) {
        try { isFile(pFile); return true;
        } catch (OemException ce) { return false; }
    }

    /**
     * Checks to see if the object passed is a file. Throws an exception
     * if it's not.
     * @param pFileName The file name.
     * @throws OemException
     */
    public final static void isFile(final String pFileName)
        throws OemException
    {
        if (StringUtils.isBlank(pFileName)) {
            throw new OemException("file name isn't set");
        }
        isFile(new File(pFileName));
    }

    /**
     * Checks to see if the object passed is a file. Throws an exception
     * if it's not.
     * @param pFile The file.
     * @throws OemException
     */
    public final static void isFile(final File pFile)
        throws OemException
    {
        if (pFile == null) throw new OemException("file is null");

        try {

            if (pFile.isDirectory()) {
                throw new OemException("location is a dir: "
                                        + pFile.getName());
            }

            if (!pFile.isFile()) {
                throw new OemException("location is not a file: "
                                        + pFile.getName());
            }

        } catch (SecurityException se) {
            throw new OemException("security error - file name: "
                                    + pFile.getName(),
                                    se);
        } catch (Throwable t) {
            throw new OemException("error - file: " + pFile.getName(), t);
        }
    }

    /**
     * Checks to see if the object passed is a directory.
     * @param pFile The file.
     * @return True if the location is a directory.
     * @throws OemException
     */
    public final static boolean isDir(final String pFile)
    { return isDir(new File(pFile)); }

    /**
     * Checks to see if the object passed is a directory.
     * @param pFile The file.
     * @return True if the location is a directory.
     */
    public final static boolean isDir(final File pFile)
    { return pFile.isDirectory(); }

    /**
     * Check to see if a file is readable or writable.
     * @param pFile The file.
     * @param pReadCheck The read check flag.
     * @throws OemException
     */
    public final static void checkCanReadOrWrite(   final File pFile,
                                                    final boolean pReadCheck)
        throws OemException
    {
        if (pFile == null) throw new OemException("file is null");

        try {

            isFile(pFile);

            if (pReadCheck) {
                if (!pFile.canRead()) {
                    throw new OemException("can't read file: "
                                            + pFile.getName());
                }
            } else {
                if (!pFile.canWrite()) {
                    throw new OemException("can't write file: "
                                            + pFile.getName());
                }
            }

        // VERIFY THE CATCH ORDER HERE
        } catch (SecurityException se) {
            throw new OemException("security error - file name: "
                                    + pFile.getName(),
                                    se);
        } catch (OemException ce) { throw ce;
        } catch (Throwable t) { throw new OemException(t); }
    }

    /**
     * Check to see if the file name is a writable file. If it's not
     * then throw an exception with the reason.
     * @param  pFileName The file name.
     * @throws OemException
     */
    public final static void writableFile(final String pFileName)
        throws OemException
    { checkCanReadOrWrite(new File(pFileName), false); }

    /**
     * Check to see if the file name is a writable file. If it's not
     * then throw an exception with the reason.
     * @param  pFile The file.
     * @throws OemException
     */
    public final static void writableFile(final File pFile)
        throws OemException
    { checkCanReadOrWrite(pFile, false); }

    /**
     * Check to see if the file name is a readable file. If it's not
     * then throw an exception with the reason.
     * @param pFile The file.
     * @throws OemException
     */
    public final static void readableFile(final File pFile)
        throws OemException
    { checkCanReadOrWrite(pFile, true); }

    /**
     * Check to see if the file name is a readable file. If it's not
     * then throw an exception with the reason.
     * @param  pFileName The file name.
     * @throws OemException
     */
    public final static void readableFile(final String pFileName)
        throws OemException
    { checkCanReadOrWrite(new File(pFileName), true); }

    /**
     * Returns the file name based on the params.
     * @param pDataDir The data directory. Don't include trailing /.
     * @param pPrefix The file name prefix.
     * @param pExtension File extension (do not include '.').
     * @param pNodeId The node id.
     * @param pInstanceId The instance id.
     * @param pFormatId The format id.
     * @param pVersionId The version id.
     * @param pStartTime The start time.
     * @param pStopTime The stop time.
     * @param pRandom A random int.
     */
    public final static String createFileName(  final String pDataDir,
                                                final String pPrefix,
                                                final String pExtension,
                                                final short pNodeId,
                                                final byte pInstanceId,
                                                final byte pFormatId,
                                                final byte pVersionId,
                                                final int pStartTime,
                                                final int pStopTime,
                                                final int pRandom)
    {

        final StringBuilder fileName = new StringBuilder(pDataDir);

        fileName.append("/");
        fileName.append(pPrefix);

        fileName.append("-");
        fileName.append(pNodeId);

        fileName.append("-");
        fileName.append((int)pInstanceId);

        fileName.append("-");
        fileName.append((int)pFormatId);

        fileName.append("-");
        fileName.append((int)pVersionId);

        fileName.append("-");
        fileName.append(pStartTime);

        fileName.append("-");
        fileName.append(pStopTime);

        fileName.append("-");
        fileName.append(pRandom);

        fileName.append(".");
        fileName.append(pExtension);
        return fileName.toString();
    }

    /**
     * Create a file name path with a random uuid for the name
     * and the current time for the path (and passed extension).
     * @param pRootDir The base directory.
     * @param pFileNameExt The file name extension.
     * @return The file name/path.
     */
    public static String assembleTimePathUuidFile(  final String pRootDir,
                                                    final String pFileNameExt)
    {
        final StringBuilder fileName = new StringBuilder(pRootDir);

        // Get the time.
        final Calendar now = Calendar.getInstance();
        fileName.append(now.get(Calendar.YEAR));
        fileName.append(SLASH);
        fileName.append(now.get(Calendar.MONTH));
        fileName.append(SLASH);
        fileName.append(now.get(Calendar.DATE));
        fileName.append(SLASH);
        fileName.append(now.get(Calendar.HOUR_OF_DAY));
        fileName.append(SLASH);
        fileName.append(now.get(Calendar.MINUTE));
        fileName.append(SLASH);
        fileName.append(now.get(Calendar.SECOND));
        fileName.append(SLASH);
        final File dir = new File(fileName.toString());
        if (!dir.exists()) dir.mkdirs();
        fileName.append(UUID.randomUUID().toString().replace(DASH_STR, EMPTY_STR));
        if (!pFileNameExt.startsWith(PERIOD_STR)) fileName.append(PERIOD);
        fileName.append(pFileNameExt);
        return fileName.toString();
    }
}

