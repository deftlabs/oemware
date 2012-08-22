/**
 * (C) Copyright 2007, Deft Labs.
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

// Java
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * The number utils.
 */
public final class NumberUtils {

    public static final int SHORT_CACHE_MAX = 1000;
    private static volatile Short SHORT_CACHE [] = new Short[SHORT_CACHE_MAX];

    static {
        // Initialize the short cache.
        for (int idx=0; idx < SHORT_CACHE_MAX; idx++) {
            SHORT_CACHE[idx] = new Short((short)idx);
        }
    };

    /**
     * Returns the cached copy or a new object for the primitive passed.
     * @param pValue The value.
     * @return The object.
     */
    public static final synchronized Short getShort(final int pValue) {
        if (pValue > -1 && pValue < SHORT_CACHE_MAX) return SHORT_CACHE[pValue];
        return new Short((short)pValue);
    }

    /**
     * Check to see if doubles are equal (null safe).
     * @param pValue1 The first value.
     * @param pValue2 The second value.
     */
    public static final boolean equals( final Double pValue1,
                                        final Double pValue2)
    {
        if (pValue1 != null) return pValue1.equals(pValue2);
        if (pValue2 != null) return pValue1.equals(pValue2);
        return false;
    }

    /**
     * Check to see if longs are equal (null safe).
     * @param pValue1 The first value.
     * @param pValue2 The second value.
     */
    public static final boolean equals( final Long pValue1,
                                        final Long pValue2)
    {
        if (pValue1 != null) return pValue1.equals(pValue2);
        if (pValue2 != null) return pValue1.equals(pValue2);
        return false;
    }

    /**
     * Check to see if ints are equal (null safe).
     * @param pValue1 The first value.
     * @param pValue2 The second value.
     */
    public static final boolean equals( final Integer pValue1,
                                        final Integer pValue2)
    {
        if (pValue1 != null) return pValue1.equals(pValue2);
        if (pValue2 != null) return pValue1.equals(pValue2);
        return false;
    }

    /**
     * Check to see if the shorts are equal (null safe).
     * @param pValue1 The first value.
     * @param pValue2 The second value.
     */
    public static final boolean equals( final Short pValue1,
                                        final Short pValue2)
    {
        if (pValue1 != null) return pValue1.equals(pValue2);
        if (pValue2 != null) return pValue1.equals(pValue2);
        return false;
    }

    /**
     * Check to see if the big ints are equal (null safe).
     * @param pValue1 The first value.
     * @param pValue2 The second value.
     */
    public static final boolean equals( final BigInteger pValue1,
                                        final BigInteger pValue2)
    {
        if (pValue1 == null || pValue2 == null) return false;
        return pValue1.equals(pValue2);
    }

    /**
     * Check to see if the big decimals are equal (null safe).
     * @param pValue1 The first value.
     * @param pValue2 The second value.
     */
    public static final boolean equals( final BigDecimal pValue1,
                                        final BigDecimal pValue2)
    {
        if (pValue1 == null || pValue2 == null) return false;
        return pValue1.equals(pValue2);
    }
}

