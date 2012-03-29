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

