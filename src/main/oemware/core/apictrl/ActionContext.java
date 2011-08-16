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

// J2EE
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Java
import java.util.Map;
import java.util.HashMap;

/**
 * The action context object.
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public final class ActionContext {

    private String mControllerPath;
    private String mActionName;

    private HttpServletRequest mRequest;
    private HttpServletResponse mResponse;

    public ActionContext() { }

    public ActionContext(   final String pControllerPath, 
                            final String pActionName,
                            final HttpServletRequest pRequest,
                            final HttpServletResponse pResponse) 
    { 
       mControllerPath = pControllerPath;
       mActionName = pActionName;
       mRequest = pRequest;
       mResponse = pResponse;
    }

    private final Map<String, Object> mValues = new HashMap<String, Object>();

    public Object put(final String pKey, final Object pObj) {
        return mValues.put(pKey, pObj);
    }

    public Object get(final String pKey) {
        return mValues.get(pKey);
    }

    public String getString(final String pKey) {
        return (String)mValues.get(pKey);
    }

    public String getActionName() { return mActionName; }
    public void setActionName(final String pV) { mActionName = pV; }

    public String getControllerPath() { return mControllerPath; }
    public void setControllerPath(final String pV) { mControllerPath = pV; }

    public HttpServletRequest getRequest() { return mRequest; }
    public HttpServletResponse getResponse() { return mResponse; }

}

