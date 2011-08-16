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

package oemware.core.chain;

// OEMware
import static oemware.core.Kernel.KERNEL_CONTEXT;

// Spring
import org.springframework.context.support.GenericApplicationContext;

// Jakarta Commons
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Shutdown the component kernel.
 *  
 * @author Ryan Nitz
 * @version $Id: ShutdownKernelCommand.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class ShutdownKernelCommand implements Command {
    private final Log mLogger = LogFactory.getLog(ShutdownKernelCommand.class);
    /**
     * The "run" method.
     * @param pContext The context.
     * @throws Exception
     */
    public final boolean execute(final Context pContext) throws Exception {
        long startTime = System.currentTimeMillis();

        if (mLogger.isDebugEnabled()) {
            mLogger.debug(   "----- stopping: kernel -----");
        }

        // Load the context object.
        final GenericApplicationContext kernelContext     
            = (GenericApplicationContext)pContext.get(KERNEL_CONTEXT);

        if (kernelContext == null) {
            mLogger.warn(   "kernel context is null - chain param name: " 
                            + KERNEL_CONTEXT);

            return PROCESSING_COMPLETE;
        }

        kernelContext.stop();
        kernelContext.close();

        final long shutdownTime = (System.currentTimeMillis() - startTime);

        if (mLogger.isDebugEnabled()) {
            mLogger.debug(  "----- stopped:  kernel - stop time: " 
                            + shutdownTime 
                            + " (ms) -----");
        }

        return CONTINUE_PROCESSING;
    }
}

