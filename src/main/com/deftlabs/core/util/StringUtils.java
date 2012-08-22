/**
 * (C) Copyright 2007, Deft Labs.
 *
 * Original source copyright 2002-2005 The Apache Software Foundation.
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

/**
 * Some misc string utils. Original source from Jakarta
 * Commons (I didn't want to import 3rd party lib).
 */
public class StringUtils {

    public static final boolean isBlank(final String pV) {
        int len;
        if (pV == null || (len = pV.length()) == 0) return true;

        for (int i = 0; i < len; i++)
        { if ((Character.isWhitespace(pV.charAt(i)) == false)) return false; }

        return true;
    }

    public static final boolean equals(final String str1, final String str2) { return str1 == null ? str2 == null : str1.equals(str2); }

    public static final boolean isEmpty(final String pV) { return pV == null || pV.length() == 0; }
}

