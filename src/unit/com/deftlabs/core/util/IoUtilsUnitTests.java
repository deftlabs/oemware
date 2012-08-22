/**
 * (C) Copyright 2008, Deft Labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deftlabs.core.util;

// JUnit
import org.junit.Test;
import static org.junit.Assert.*;

// Java
import java.util.Random;
import java.util.Arrays;
import java.io.ByteArrayInputStream;

/**
 * The OEMware io utility tests.
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public final class IoUtilsUnitTests {

    @Test
    public final void testReadInputStream() throws Exception {
        final Random random = new Random(System.currentTimeMillis());
        final byte [] data = new byte[10485761];

        for (int idx=0; idx < data.length; idx++)
            data[idx] = (byte)random.nextInt(125);

        final ByteArrayInputStream input = new ByteArrayInputStream(data);
        final byte [] response
        = IoUtils.readInputStream(input, 1024);
        assertTrue(Arrays.equals(data, response));
    }

    @Test
    public final void testReadInputStreamEmptyStream() throws Exception {
        final ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
        final byte [] response = IoUtils.readInputStream(input, 1025);
        assertEquals(response.length, 0);
    }

    @Test
    public final void testReadInputStreamNullInput() throws Exception {
        boolean exception = false;
        try { IoUtils.readInputStream(null, -1);
        } catch (IllegalArgumentException iae) { exception = true; }
        assertTrue(exception);
    }

    @Test
    public final void testReadInputStreamInvalidBufferSize() throws Exception {
        boolean exception = false;
        final ByteArrayInputStream input = new ByteArrayInputStream(new byte[1]);
        try { IoUtils.readInputStream(input, 0);
        } catch (IllegalArgumentException iae) { exception = true; }
        assertTrue(exception);
    }
}

