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
import oemware.core.ServiceManager;
import oemware.core.CoreException;
import oemware.core.ServiceException;

// Jakarta Commons
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The shutdown message handler.
 *
 * @author Ryan Nitz
 * @version $Id: ShutdownMessageHandler.java 94 2008-10-03 14:27:54Z oemware $
 */
public final class ShutdownMessageHandler implements DatagramMessageHandler {
    private final Log mLog = LogFactory.getLog(ShutdownMessageHandler.class);

    /**
     * Called to execute the handler.
     * @param pDatagramMessage The message.
     * @return True if the buffer should be written.
     */
    public boolean execute(final DatagramMessage pDatagramMessage) {
        mLog.info("shutdown message received - stopping server");
        try {
            ServiceManager.getInstance().shutdown();
            return true;
        } catch (ServiceException se) { mLog.error(se.getMessage(), se); 
        } catch (Throwable t) { mLog.error(t.getMessage(), t); }
        return false;
    }
}
