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

/**
 * The byte array object. This is a thin wrapper around a byte array
 * that implements the hash code method and eauals. This is mainly
 * used for it's comparison features. Null data objects are not allowd.
 * This uses the length and offset in the equals and hash code.
 * <br/><br/>
 *
 * Some of this code was taken from java.util.Arrays.
 */
public final class ByteArray {

    public byte [] data;

    public volatile int length;
    public volatile int offset;

    /**
     * Creates an empty byte array. The data must be set. You must manually
     * set the length.
     */
    public ByteArray() { offset = 0; }

    /**
     * Create a new byte array with the data. The offset is set to zero
     * and the length is set to the length of the data.
     * @param pData The byte array.
     */
    public ByteArray(final byte [] pData) {
        data = pData;
        length = pData.length;
        offset = 0;
    }

    /**
     * Create a new byte array with a subset of the byte array.
     * @param pData The data.
     * @param pOffset The offset in the pData array.
     * @param pLength the length of the data to use.
     */
    public ByteArray(   final byte [] pData,
                        final int pOffset,
                        final int pLength)
    {
        data = pData;
        offset = pOffset;
        length = pLength;
    }

    /**
     * Check to see if the data passed is equal to this value.
     * @param pData The data.
     * @return True if it's equal.
     */
    public final boolean isEqual(final byte [] pData) {

        if (pData == null) return false;
        if (pData.length != length) return false;

        int idx2 = 0;
        for (int idx1=0; idx1 < length; idx1++) {
            if (data[idx1 + offset] != pData[idx2++]) return false;
        }
        return true;
    }

    /**
     * Overrides the default equals to compare the byte array.
     * @param pObject The object to check. May not be null.
     * @return True if they're equal.
     */
    public final boolean equals(final Object pObject) {

        final ByteArray check = (ByteArray)pObject;

        if (check.length != length) return false;

        int idx2 = check.offset;
        for (int idx1=0; idx1 < length; idx1++) {
            if (data[idx1 + offset] != check.data[idx2++]) return false;
        }

        return true;
    }

    /**
     * Overrides the default hash code to handle the bye array (or sction of).
     * @return The hash code.
     */
    public final int hashCode() {
        int result = 1;
        for (int idx=0; idx < length; idx++) result = 31 * result + data[idx + offset];
        return result;
    }

    /**
     * This is for debug only. It creates a string of the object.
     * @return A new string object.
     */
    public final String toString() { return new String(data, offset, length); }
}

