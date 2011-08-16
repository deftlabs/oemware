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

// OEMware
import static oemware.core.net.CoreMessageId.SHUTDOWN;

// Java
import java.nio.ByteBuffer;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

/**
 * The shutdown client.
 *
 * @author Ryan Nitz
 * @version $Id: ShutdownClient.java 37 2008-07-20 20:27:15Z oemware $
 */
public final class ShutdownClient {

    private static final int BUFFER_SIZE = 2;
    private static final int TIMEOUT = 1000;
    
    /**
     * Stop the server.
     * @param pArgs The cl args.
     */
    public static final void main(final String [] pArgs) {
        // Get the port from the cl.
        try {

        int port = 0;
        try {

            if (pArgs[0] == null) {
                System.out.println("no port specified");
                System.exit(1);
            }
            
            port = Integer.parseInt(pArgs[0]);

        } catch (NumberFormatException nfe) {
            System.out.println("invalid port: " +  pArgs[0]);
            System.exit(1);
        }

        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        buffer.putShort(0, SHUTDOWN.id());

        InetSocketAddress address  = new InetSocketAddress("localhost", port);

        // Construct the packet.
        DatagramPacket packet 
            = new DatagramPacket(buffer.array(), BUFFER_SIZE, address);

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);

        // Send the packet. 
        socket.send(packet);

        socket.close();

        } catch (Throwable t) { t.printStackTrace(); }
    }
}
