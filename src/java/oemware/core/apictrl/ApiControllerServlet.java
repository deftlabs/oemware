/**
 * (C) Copyright 2009, Deft Labs.
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

package oemware.core.apictrl;

// OEMware
import oemware.core.ServiceManager;
import static oemware.core.ServiceManager.STATE_RUNNING;

// Jakarta Commons
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

// OEMware
import oemware.core.Kernel;

// J2EE
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

// Java
import java.util.Enumeration;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The api controller servlet.
 * 
 * @author Ryan Nitz
 */
public final class ApiControllerServlet extends HttpServlet {

    private ApiController mController;
    private String mComponentId; 
    private final Object mMutex = new Object();
    private ServiceManager mServiceManager;
    private final Log mLog = LogFactory.getLog(ApiControllerServlet.class);
    private static final long serialVersionUID = 4500972115442349022L;

    private static final String COMPONENT_ID = "componentId";

    /**
     * Called to service the list api call. This sets the content type to XML.
     * @param pRequest The http servlet request.
     * @param pResponse The http servlet response.
     * @throws ServletException
     * @throws IOException
     */
    public void service(final HttpServletRequest pRequest, 
                        final HttpServletResponse pResponse)
                       
        throws ServletException, IOException
    {
        if (!isKernelRunning()) {
            pResponse.setStatus(pResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        final ApiController controller = getController();
        final String [] pathElems = pRequest.getRequestURI().split("/");

        if (pathElems == null || pathElems.length < 5) {
            mLog.error(mComponentId 
                        + " - bad request params - uri: " 
                        + pRequest.getRequestURI()); 
            pResponse.setStatus(pResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Get the controller path name.
        final String controllerPath = pathElems[3];

        // Get the action name.
        final String actionName = pathElems[4];

        // Init the context object.
        final ActionContext context 
        = new ActionContext(controllerPath, actionName, pRequest, pResponse);

        // Set the request params.
        final Enumeration names = pRequest.getParameterNames();
        while (names.hasMoreElements()) {
            final String name = (String)names.nextElement();
            context.put(name, pRequest.getParameter(name));
        }

        // Execute the action.
        try { controller.executeAction(context);
        } catch (final Throwable t) {
            mLog.error(mComponentId + " - " + t.getMessage(), t);
            pResponse.setStatus(pResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns the api controller (lazy loads).
     * @return The api controller.
     * @throws ServletException
     */
    private ApiController getController() throws ServletException {
        if (mController != null) return mController; 
        synchronized(mMutex) {
            if (mController != null) return mController; 
            mController = (ApiController)findComponent(mComponentId);
        }
        return mController;
    }
    
    /**
     * Called to init.
     * @param pConfig The servlet configuration.
     * @throws ServletException
     */
    public void init(final ServletConfig pConfig) throws ServletException {
        super.init(pConfig);

        mComponentId 
        = StringUtils.trimToNull(pConfig.getInitParameter(COMPONENT_ID));

        if (mComponentId == null) 
        { throw new ServletException("No controller componentId set"); }
    }

    /**
     * Returns true if the kernel is running.
     * @return True if the kernel is running.
     */
    private boolean isKernelRunning() {
        try {
            if (mServiceManager == null) 
            { mServiceManager = ServiceManager.getInstance(); }
            if (mServiceManager.getRunState() == STATE_RUNNING) return true;
            return false;
        } catch (final Throwable t) { return false; }
    }

    /**
     * Called to load a Spring component.
     * @param pName The component name.
     * @throws ServletException
     */
    protected Object findComponent(final String pName) throws ServletException {
        try { return Kernel.findComponent(pName);
        } catch (Throwable t) { throw new ServletException(t); }
    }
}

