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

/**
 * The OEMware lru list tests.
 */
public final class LruListUnitTests {

    @Test
    public final void testMaxCapacity() throws Exception {
        final int size = 100;
        final LruList<String> list = new LruList<String>(size);
        for (int idx=0; idx < (size*2); idx++) list.put("something");
        assertEquals(list.size(), size);
    }
}

