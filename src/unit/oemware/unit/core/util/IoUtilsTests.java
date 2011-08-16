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

package oemware.unit.core.util;

// OEMware
import oemware.core.util.IoUtils;

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
public final class IoUtilsTests {

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

