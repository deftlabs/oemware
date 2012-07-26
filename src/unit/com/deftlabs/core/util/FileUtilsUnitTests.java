/**
 * (C) Copyright 2008, Deft Labs.
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

// JUnit
import org.junit.Test;
import static org.junit.Assert.*;

// Java
import java.util.ArrayList;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * The file utils tests.
 */
public final class FileUtilsUnitTests {

    //private static final int TEST_FILES = 100000;
    private static final int TEST_FILES = 1000;

    //private static final int UNBALANCE_COUNT = 1000;
    private static final int UNBALANCE_COUNT = 100;

    private static final int MAX_FILES = 20;

    //private static final int BUCKET_COUNT = 1000;
    private static final int BUCKET_COUNT = 100;

    private static final String SCRATCH_DIR = "/tmp/unit_java";
    private static final String BUCKET_DIR = SCRATCH_DIR + "/buckets/";
    private static final String EXT = ".xml";
    private static final byte [] CONTENT = "Test".getBytes();

    /**
     * Add this to file utils.
     */
    private final boolean deleteDir(final File pPath) {
        if (pPath.exists()) {
            final File [] files = pPath.listFiles();
            for (int idx=0; idx < files.length; idx++) {
                if (files[idx].isDirectory()) deleteDir(files[idx]);
                else files[idx].delete();
            }
        }
        return pPath.delete();
    }

    @Test
    public void testExtractDir() throws Exception {
        final String file1 = "/one/two/three/test.txt";
        assertEquals("/one/two/three", FileUtils.extractDir(file1));
    }

    @Test
    public void testGetFileParent() throws Exception {
        final String path = "/one/two/three";
        final String file = path + "/test.txt";
        assertEquals(path, FileUtils.getFileParent(file));
    }

    @Test
    public final void testGetFileNamesRecursive() throws Exception {
        final File scratchDir = new File(SCRATCH_DIR);
        deleteDir(scratchDir);

        final Random random = new Random(System.currentTimeMillis());

        // Create the buckets.
        for (int idx=0; idx < BUCKET_COUNT; idx++)
        { FileUtils.createDir((BUCKET_DIR + Integer.toString(idx))); }

        final ArrayList<String> fileNames = new ArrayList<String>();
        // Create test files.
        for (int idx=0; idx < TEST_FILES; idx++) {

            int bucket = (idx % BUCKET_COUNT);
            final ByteArrayInputStream is = new ByteArrayInputStream(CONTENT);
            final String fileName
            = (BUCKET_DIR + Integer.toString(bucket) + "/" + random.nextInt(Integer.MAX_VALUE) + EXT);
            FileUtils.writeInputStreamToFile(is, fileName);
            fileNames.add(fileName);
        }

        // Unbalance the buckets and write a lot of additional files to one of the
        // buckets.
        for (int idx=0; idx < UNBALANCE_COUNT; idx++) {
            final ByteArrayInputStream is = new ByteArrayInputStream(CONTENT);
            final String fileName
            = (BUCKET_DIR + Integer.toString(50) + "/" + random.nextInt(Integer.MAX_VALUE) + EXT);
            FileUtils.writeInputStreamToFile(is, fileName);
            fileNames.add(fileName);
        }

        //System.out.println("file generated");

        // Execute the file find.
        final String [] files
        = FileUtils.getFileNames(BUCKET_DIR, EXT, false, true, true, MAX_FILES);

        // Validate against files found.
        assertEquals(files.length, MAX_FILES);
        for (final String file : files) {
            assertNotNull(file);
            assertNotSame(file.trim(), "");
            assertEquals(fileNames.contains(file), true);
        }

        //System.out.println("file validation complete");
        //System.out.println("files found: " + files.length);

        // Cleanup.
        deleteDir(scratchDir);
    }

    @Test
    public final void writeInputStreamtoFile() throws Exception {
        // Generate 10mb of randomness
        final Random entropy = new Random();
        final byte[] randBytes = new byte[1024 * 10];
        entropy.nextBytes(randBytes);

        // Copy bytes for comparison later
        final byte[] randBytesCopy = new byte[1024 * 10];
        System.arraycopy(randBytes, 0, randBytesCopy, 0, randBytes.length);

        // Generate an InputStream from the junk
        final InputStream in = new ByteArrayInputStream(randBytes);

        // Call the method we want to test, use a 1mb buffer
        final String fileName = "/tmp/fileUtilsBytes.dat";
        // if file is already there, delete it
        if (FileUtils.fileExists(fileName)) FileUtils.deleteFile(fileName);

        final byte[] pReadBuffer = new byte[1024];

        final int numBytesRead = FileUtils.writeInputStreamToFile(in, fileName, pReadBuffer);

        // Check that the correct number of bytes were read
        assertEquals(randBytes.length, numBytesRead);

        // Check that the correct data was read
        final File testFile = new File(fileName);
        testFile.deleteOnExit();
        final FileInputStream out = new FileInputStream(fileName);
        final byte[] outBuffer = new byte[numBytesRead];
        out.read(outBuffer);
        assertArrayEquals(randBytesCopy, outBuffer);
    }

    @Test public void renameFile() {

        try {

            if (!FileUtils.fileExists("/tmp/renameFileTest1")) FileUtils.deleteFile("/tmp/renameFileTest1");
            if (!FileUtils.fileExists("/tmp/renameFileTest")) FileUtils.writeStrToFile("testThis", "/tmp/renameFileTest");

            FileUtils.renameFile("/tmp/renameFileTest", "/tmp/renameFileTest1");

            assertTrue(!FileUtils.fileExists("/tmp/renameFileTest"));
            assertTrue(FileUtils.fileExists("/tmp/renameFileTest1"));
        } finally {
            FileUtils.deleteFile("/tmp/renameFileTest");
            FileUtils.deleteFile("/tmp/renameFileTest1");
        }
    }

    @Test
    public void testExtension1() throws Exception {
        final String file1 = "/one/two/three/test.txt";
        assertEquals("txt", FileUtils.extension(file1));
    }

    @Test
    public void testExtension2() throws Exception {
        final String file1 = "/one/two/three/test";
        assertNull(FileUtils.extension(file1));
    }

    @Test
    public void testExtension3() throws Exception {
        final String file1 = "/one.two/two/three/test";
        assertNull(FileUtils.extension(file1));
    }

}

