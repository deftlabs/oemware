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

