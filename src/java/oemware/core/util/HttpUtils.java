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

// OEMware
import static oemware.core.SystemConstants.DEFAULT_USER_AGENT;

// Java
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * The http utils.
 *
 * @author Ryan Nitz
 * @version $Id: HttpUtils.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class HttpUtils {

    private static final String USER_AGENT_KEY = "User-Agent";
  
    // Page defaults.
    private static final int PAGE_CONNECT_TIMEOUT = 5000;
    private static final int PAGE_READ_TIMEOUT = 5000;
    private static final int PAGE_READ_BUFFER_SIZE = 2097152;
    private static final int PAGE_REMOTE_READ_BLOCK_SIZE = 51200;
    
    /**
     * Opens an http connection and tries to read the page content. This has
     * a fixed buffer set to PAGE_READ_BUFFER_SIZE.
     * @param pUrl The url.
     * @return The content string.
     * @throws IOException
     */
    public static final byte [] readPageContent( final String pUrl) 
        throws IOException 
    {
        return readContent( pUrl, 
                            PAGE_CONNECT_TIMEOUT, 
                            PAGE_READ_TIMEOUT, 
                            DEFAULT_USER_AGENT, 
                            PAGE_READ_BUFFER_SIZE, 
                            PAGE_REMOTE_READ_BLOCK_SIZE);
    }

    /**
     * Opens an http connection and tries to read the contents.
     * @param pUrl The url.
     * @param pConnectTimeout The connection timeout.
     * @param pReadTimeout The read timeout.
     * @param pUserAgent The user agent.
     * @return The content.
     * @throws IOException
     */
    public static final byte [] readContent(final String pUrl, 
                                            final int pConnectTimeout, 
                                            final int pReadTimeout, 
                                            final String pUserAgent,
                                            final int pReadBufferSize,
                                            final int pRemoteReadBlockSize) 
        throws IOException
    {
        if (pUrl == null) throw new IllegalArgumentException("url is null");
        final byte [] readBuffer = new byte[pReadBufferSize];
        HttpURLConnection remoteServer = null;
        try {
            final URL remoteCall = new URL(pUrl);
            
            remoteServer
            = (HttpURLConnection)remoteCall.openConnection();

            remoteServer.setConnectTimeout(pConnectTimeout);
            remoteServer.setReadTimeout(pReadTimeout);
            remoteServer.setRequestProperty(USER_AGENT_KEY, pUserAgent);
            remoteServer.connect();
            final InputStream remoteStream = remoteServer.getInputStream();

            final int bytesRead 
            = IoUtils.readInputStream(  remoteStream, 
                                        readBuffer, 
                                        pRemoteReadBlockSize);

            final byte [] content = new byte[bytesRead];
            System.arraycopy(readBuffer, 0, content, 0, bytesRead);
            return content; 
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException("invalid url: " + pUrl);
        } finally { if (remoteServer != null) remoteServer.disconnect(); }
    }
}

