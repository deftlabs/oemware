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
import oemware.core.util.JaxbUtils;
import oemware.core.Kernel;

// OEMware Jaxb
import oemware.core.apictrl.config.ControllersDef;
import oemware.core.apictrl.config.ControllerDef;
import oemware.core.apictrl.config.ConstructorArgDef;
import oemware.core.apictrl.config.ActionDef;

// Jakarta Commons
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;;
import org.apache.commons.lang.StringUtils;

// Java
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 * The api controller.
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public final class ApiController {

    private Map<String, Controller> mControllers; 

    private final Object mMutex = new Object();
    private final String mConfigFile;
    private final String mName;
    private static final String CONFIG_PACKAGE = "oemware.core.apictrl.config";

    /**
     * Construct with args.
     * @param pName A unique name to identify problems in log files..
     * @param pConfigFile The configuration file name. This file needs
     * to be in the Java classpath.
     */
    public ApiController(final String pName, final String pConfigFile) {
        mName = pName;
        mConfigFile = pConfigFile;
        
   }

    /**
     * Execute the action.
     * @param pContext The action context.
     */
    public final void executeAction(final ActionContext pContext) {
        executeAction(  pContext.getControllerPath(), 
                        pContext.getActionName(), 
                        pContext);
    }
    
    /**
     * Execute the action.
     * @param pControllerPathThe controller path.
     * @param pActionName The action name.
     * @param pContext The action context.
     */
    public final void executeAction(final String pControllerPath, 
                                    final String pActionName, 
                                    final ActionContext pContext) 
    {
        try {
            final Controller controller = getControllers().get(pControllerPath);

            if (controller == null) {
                throw new RuntimeException( "controller not found: " 
                                            + pControllerPath);
            }
            
            final Action action = controller.findAction(pActionName);

            if (action == null) {
                throw new RuntimeException( "action not found: " 
                                            + pActionName);
            }

            // Invoke the method on the controller.
            MethodUtils.invokeExactMethod(controller, action.getName(), pContext);

        } catch (final Throwable t)
        { throw new IllegalStateException(mName + " - " + t.getMessage(), t); }
    }

    /**
     * Returns the controllers.
     * @return The controllers.
     */
    private Map<String, Controller> getControllers() {
        if (mControllers != null) return mControllers;
        synchronized(mMutex) {
            if (mControllers != null) return mControllers;
            final Map<String, Controller> controllers 
            = new HashMap<String, Controller>();
            for (final ControllerDef def : loadControllerDefs(mConfigFile)) 
            { controllers.put(def.getPath(), loadController(def)); }
            mControllers = controllers;
        }
        return mControllers;
    }

    /**
     * Load the feature command.
     * @param pControllerDef The controller definition.
     * @return The object.
     * @throws Exception
     */
    private Controller loadController(final ControllerDef pControllerDef) {
        try {
            final Class clazz 
            = Thread.currentThread().getContextClassLoader().loadClass(pControllerDef.getClazz());
            
            final LinkedList<Object> args = new LinkedList<Object>();
            
            for (ConstructorArgDef argDef : pControllerDef.getConstructorArg()) {

                final String componentId = StringUtils.trimToNull(argDef.getComponentId()); 
                final String value = StringUtils.trimToNull(argDef.getValue()); 
                final String valueType = StringUtils.trimToNull(argDef.getValueType()); 

                if (componentId != null) {
                    args.addLast(Kernel.findComponent(componentId));
                } else if (value != null) {
                    if (valueType == null) {
                        throw new IllegalStateException(mName 
                        + " - vaue requires value-type");
                    }

                    if (valueType.equals("int")) {
                        args.addLast(Integer.parseInt(value));
                    } else if (valueType.equals("double")) {
                        args.addLast(Double.parseDouble(value));
                    } else if (valueType.equals("long")) {
                        args.addLast(Long.parseLong(value));
                    } else if (valueType.equals("short")) {
                        args.addLast(Short.parseShort(value));
                    } else if (valueType.equals("float")) {
                        args.addLast(Float.parseFloat(value));
                    } else if (valueType.equals("string")) {
                        args.addLast(value);
                    }

                } else {
                    throw new IllegalStateException(mName 
                    + " - requires component-id or value");
                }
                
            }
            
            final Controller controller 
            = (Controller)ConstructorUtils.invokeConstructor(clazz, args.toArray(new Object[args.size()]));

            for (final ActionDef actionDef : pControllerDef.getAction()) {
                controller.addAction(createAction(actionDef));
            }

            return controller;

        } catch (final Throwable t) 
        { throw new IllegalStateException(mName + " - " + t.getMessage(), t); }
    }

    /**
     * Create an action object.
     * @param pActionDef The action definition.
     */
    private Action createAction(final ActionDef pActionDef) {
        final Action action = new Action(pActionDef.getName());
        return action;
    }

    /**
     * Returns the controlller definitions (loads).
     * @param pConfigFile The configuration file.
     */
    private List<ControllerDef> loadControllerDefs(final String pConfigFile) {
        try { 
            // Load the controller definitions.
            final ControllersDef def 
            = (ControllersDef)JaxbUtils.jaxbUnmarshal(pConfigFile, CONFIG_PACKAGE);
            return def.getController();
        } catch (final Throwable t) 
        { throw new IllegalStateException(mName + " - " + t.getMessage(), t); }
    }
}

