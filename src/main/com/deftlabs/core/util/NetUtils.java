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
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The network utils.
 */
public final class NetUtils {

    /**
     * Takes an ipv4 address in int format and converts to a string
     * @param pAddr The address. This method creates a new buffer and
     * a string... this needs to be fixed.
     *
     * @return The ipv4 string address. Null is returned if it's invalid.
     */
    public static final String decodeIpV4Addr(final int pAddr) {
        final byte [] value = ByteNumberUtils.createIntB(pAddr);
        try {
            final String rt = InetAddress.getByAddress(value).toString();
            return rt.substring(1, rt.length());

        } catch (UnknownHostException uhe) { return null; }
    }

    /**
     * This takes a period delimited ip address and converts it to a four
     * byte value.
     * @param pAddr The address to encode.
     * @return The four byte array.
     */
    public final static byte [] encodeIpV4Addr(final String pAddr) {
        final String elements [] = pAddr.split("\\.");
        final byte buffer [] = new byte[4];

        for (int idx=0; idx < elements.length; idx++) {
            buffer[idx] = (byte)Integer.parseInt(elements[idx]);
        }

        return buffer;
    }
}

