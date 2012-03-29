/**
 * (C) Copyright 2007, Deft Labs.
 *
 * Original source copyright 2002-2005 The Apache Software Foundation.
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

/**
 * Some misc string utils. Original source from Jakarta
 * Commons (I didn't want to import 3rd party lib).
 */
public class StringUtils {

    public static final boolean isBlank(final String pV) {
        int len;
        if (pV == null || (len= pV.length()) == 0) return true;

        for (int i = 0; i < len; i++)
        { if ((Character.isWhitespace(pV.charAt(i)) == false)) return false; }

        return true;
    }

    public static final boolean equals(String   str1, String   str2) { return str1 == null ? str2 == null : str1.equals(str2); }

    public static final boolean isEmpty(final String pV) { return pV == null || pV.length() == 0; }
}

