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

