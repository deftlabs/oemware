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

package oemware.core;

// Spring
import org.springframework.beans.BeansException;
import org.springframework.context.support.GenericApplicationContext;

// Jakarta Commons 
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The kernel object. This must be initialized by passing in the
 * service context.
 *
 * @author Ryan Nitz
 * @version $Id: Kernel.java 35 2008-07-20 19:40:25Z oemware $
 */
public final class Kernel {

    private static Kernel sInstance = null;
    private final Log mLogger = LogFactory.getLog(Kernel.class);
    private final GenericApplicationContext mContext;
    public static final String KERNEL_CONTEXT = "oemware.core.Kernel.context";

    /**
     * Construct a new object with the context.
     * @param pContext The kernel context.
     */
    private Kernel(GenericApplicationContext pContext) { mContext = pContext; }

    /**
     * Returns the context.
     * @return THe context.
     */
    public final GenericApplicationContext getContext() { return mContext; }

    /**
     * Returns the Spring context object.
     * @return The context.
     */
    public static final GenericApplicationContext context() { 
        if (sInstance == null) {
            throw new IllegalStateException("You must call init() first.");
        }
        return sInstance.getContext(); 
    }

    /**
     * Lookup a component.
     * @param pComponentId The component id.
     * @return The component. An exception is thrown if the component isn't
     * found.
     * isn't found.
     * @throws ServiceException 
     */
    public final static Object findComponent(final String pComponentId)
        throws ServiceException
    {
        // Make sure it's initialized.
        if (sInstance == null) 
        { throw new ServiceException("kernel not initialized"); }

        // Debug.
        if (sInstance.mLogger.isDebugEnabled()) 
        { sInstance.mLogger.debug("findComponent(" + pComponentId + ")"); }

        if (StringUtils.isBlank(pComponentId)) {
            throw new ServiceException( "component id not set: '"
                                        + pComponentId
                                        + "'");
        }

        try {
            Object component = sInstance.mContext.getBean(pComponentId);

            if (component == null) {
                throw new ServiceException( "component not found - id: "
                                            + pComponentId);
            }

            return component;
        } catch (BeansException be) { throw new ServiceException(be); }
    }

    /**
     * The init method. This method must be called before using the object.
     * @param pContext The context.
     */
    public synchronized static void init(GenericApplicationContext pContext) {
        if (sInstance != null) return;
        sInstance = new Kernel(pContext);
    }
}

