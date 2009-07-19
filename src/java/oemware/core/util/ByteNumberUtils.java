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

package oemware.core.util;

/**
 * The byte number utils. Most of these came from the Sun DNS classes. They
 * were modified to work with our needs. The Java SDK source is released under
 * GPL. You can download the Sun JDK source from http://java.sun.com.
 * 
 * @author Ryan Nitz
 * @version $Id: ByteNumberUtils.java 13 2008-06-15 19:43:04Z oemware $
 */
public class ByteNumberUtils {

    //--------------------------------- The short methods.

    /**
     * Returns the 2-byte unsigned value at msg[pos].  The high
     * order byte comes first. This has a range of 0 - Short.MAX_VALUE.
     */
    public static final int getUShort(final byte [] data, final int pos) {
        return (((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF));
    }

    /**
     * Returns the 2-byte unsigned char at the pos. This reads from 0 - 
     * Short.MAX_VALUE * 2.
     */
    public static final char getUShortChar(final byte [] data, final int pos) {
        return (char)( ( (data[pos] & 0xFF) << 8) | ( data[pos + 1] & 0xFF ) );
    }

    /**
     * Set the 2-byte unsigned short at the position.
     */
    public static final void setUShort( final byte [] data, 
                                        final int pos, 
                                        final int value) 
    {
        data[pos] = (byte)((value >>> 8) & 0xFF);
        data[pos + 1] = (byte)(value & 0xFF);
    }

    /**
     * Set the 2-byte unsigned short at the position.
     */
    public static final void setUShortChar( final byte [] data, 
                                            final int pos, 
                                            final char value) 
    {
        data[pos] = ((byte) ((value & (0xff << 8)) >> 8));
        data[pos + 1] = ((byte) ((value & (0xff << 0)) >> 0));
    }

    //--------------------------------- The int methods.

    /**
     * Returns an unsigned int for the byte passed.
     * @param pValue The byte.
     * @return The unsigned int.
     */
    public static final int getUInt(final byte pValue) { 
        return pValue & 0xFF;
    }

    /**
     * Returns a new byte array (big endian) with the value.
     * @param value The int to convert.
     */
    public static final byte [] createIntB(final int value) {
        final byte [] data = new byte[4];
        data[0] = ((byte) (value  >> 24));
        data[1] = ((byte) (value >> 16));
        data[2] = ((byte) (value >> 8));
        data[3] = ((byte) (value >> 0));
        return data; 
    }

    /**
     * Returns a new byte array with the passed value.
     */
    public static final byte [] createInt(final int value) {
        final byte [] data = new byte[4];
        data[3] = ((byte) (value  >> 24));
        data[2] = ((byte) (value >> 16));
        data[1] = ((byte) (value >> 8));
        data[0] = ((byte) (value >> 0));
        return data; 
    }

    /**
     * Returns a new byte array with the passed value.
     */
    public static final byte [] createUInt(final long value) {
        final byte [] data = new byte[4];
        data[0] = ((byte) ((value & (0xff << 24)) >> 24));
        data[1] = ((byte) ((value & (0xff << 16)) >> 16));
        data[2] = ((byte) ((value & (0xff << 8)) >> 8));
        data[3] = ((byte) ((value & (0xff << 0)) >> 0));
        return data; 
    }

    /**
     * Returns the 1-byte unsigned value at msg[pos].
     */
    public static final int getUByte(final byte [] data, final int pos) {
        return (data[pos] & 0xFF);
    }

    /**
     * Set the 4-byte unsigned short at the position.
     */
    public static final void setUInt(   final byte [] data, 
                                        final int pos, 
                                        final long value) 
    {
        data[pos] = ((byte) ((value & (0xff << 24)) >> 24));
        data[pos + 1] = ((byte) ((value & (0xff << 16)) >> 16));
        data[pos + 2] = ((byte) ((value & (0xff << 8)) >> 8));
        data[pos + 3] = ((byte) ((value & (0xff << 0)) >> 0));
    }

    /**
     * Returns the 4-byte signed value at msg[pos].  The high
     * order byte comes first.
     */
    public static final int getInt( final byte [] data, 
                                    final int pos) 
    {
        return ((getUShort(data, pos) << 16) | getUShort(data, pos + 2));
    }

    /**
     * Set a int value in a byte array (big endian).
     * @param data The value.
     * @param pos The position.
     * @param value The value.
     */
    public static void setIntB( final byte [] data, 
                                final int pos, 
                                final int value) 
    {
        data[(0 + pos)] = (byte)(value >> 24);
        data[(1 + pos)] = (byte)(value >> 16);
        data[(2 + pos)] = (byte)(value >> 8);
        data[(3 + pos)] = (byte)(value >> 0);
    }

    /**
     * Returns the 4-byte unsigned value at msg[pos].  The high
     * order byte comes first.
     */
    public static final long getUInt(final byte [] data, final int pos) {
        return (getInt(data, pos) & 0xffffffffL);
    }

    //--------------------------------- The long methods.

    /**
     * Set the 8-byte long (little endian).
     */
    public static final void setLongL(  final byte [] data, 
                                        final int pos, 
                                        final long value) 
    {
        data[pos] = ((byte) ((value & (0xff << 56)) >> 56));
        data[pos + 1] = ((byte) ((value & (0xff << 48)) >> 48));
        data[pos + 2] = ((byte) ((value & (0xff << 40)) >> 40));
        data[pos + 3] = ((byte) ((value & (0xff << 32)) >> 32));
        data[pos + 4] = ((byte) ((value & (0xff << 24)) >> 24));
        data[pos + 5] = ((byte) ((value & (0xff << 16)) >> 16));
        data[pos + 6] = ((byte) ((value & (0xff << 8)) >> 8));
        data[pos + 7] = ((byte) ((value & (0xff << 0)) >> 0));
    }

    /**
     * Returns the eight byte long (little endian). This came from: 
     * java.nio.Bits.
     * @param data The place to read from.
     * @param pos The position in the data.
     * @return The little endian long.
     */
    public static final long getLongL(final byte [] data, final int pos) {
        return   (((long)data[pos + 7] & 0xFF) << 56) |
                 (((long)data[pos + 6] & 0xFF) << 48) |
                 (((long)data[pos + 5] & 0xFF) << 40) |
                 (((long)data[pos + 4] & 0xFF) << 32) |
                 (((long)data[pos + 3] & 0xFF) << 24) |
                 (((long)data[pos + 2] & 0xFF) << 16) |
                 (((long)data[pos + 1] & 0xFF) << 8) |
                 (((long)data[pos]     & 0xFF));
    }
}

