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

package oemware.core.net;

/**
 * The core message ids. Constants used in the header of certain
 * messages to identify it's contents.
 *
 * @author Ryan Nitz
 * @version $Id: Constants.java 13 2008-06-15 19:43:04Z oemware $
 */
public interface Constants {

    public static final int COMMON_UDP_BUFFER_SIZE = 4086;

    public static final String COMMON_UDP_BIND_ADDRESS_PARAM_NAME 
        = "common_udp_bind_address";

    public static final String COMMON_UDP_PORT_PARAM_NAME 
        = "common_udp_port";
}
