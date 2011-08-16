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

package oemware.core.web;

// J2EE
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Java
import java.io.PrintWriter;
import java.io.IOException;

/**
 * The heartbeat servlet. Used for monitoring... 
 *
 * @author Ryan Nitz
 * @version $Id: HeartbeatServlet.java 67 2008-09-16 02:55:42Z oemware $
 */
public final class HeartbeatServlet extends HttpServlet {

    private static final long serialVersionUID = -3879991148577267629L;
    private static final String RESPONSE_VALUE = "alive";

    /**
     * Handle the get, post, etc. request.
     * @param pRequest The http request.
     * @param pResponse The http response.
     * @throws ServletException
     * @throws IOException
     */
    public final void service(  HttpServletRequest pRequest,
                                HttpServletResponse pResponse)
        throws ServletException, IOException
    {
        final PrintWriter printWriter = pResponse.getWriter();
        printWriter.print(RESPONSE_VALUE);
        printWriter.flush();
        pResponse.flushBuffer();
    }
}

