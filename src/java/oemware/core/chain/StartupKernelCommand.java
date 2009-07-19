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
import oemware.core.Kernel;
import oemware.core.util.ClasspathUtils;
import static oemware.core.Kernel.KERNEL_CONTEXT;

// Spring
import org.springframework.beans.BeansException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

// Jakarta Commons
import org.apache.commons.lang.StringUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Java
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Startup the component kernel.
 *  
 * @author Ryan Nitz
 * @version $Id: StartupKernelCommand.java 13 2008-06-15 19:43:04Z oemware $
 */
public final class StartupKernelCommand implements Command {
    
    private final Log mLogger = LogFactory.getLog(StartupKernelCommand.class);
    private static final String KERNEL_CONFIG_FILE = "kernel.xml";

    /**
     * The "run" method. Start the kernel.
     * @param pContext The context.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public final boolean execute(final Context pContext) throws Exception {
        long startTime = System.currentTimeMillis();

        if (mLogger.isDebugEnabled()) {
            mLogger.debug(   "----- starting: kernel -----");
        }

        // Load the kernel.xml file.
        final Properties kernelProperties 
            = ClasspathUtils.loadXmlPropertes(KERNEL_CONFIG_FILE);

        final Enumeration <String> keys = (Enumeration<String>)kernelProperties.propertyNames();

        final Map<String, String> modules = new HashMap<String, String>();

        // Load the modules.

        while (keys.hasMoreElements()) {

            String key = keys.nextElement();

            if (StringUtils.isBlank(key)) {
                mLogger.error("blank kernel key: '" + key + "'");
                continue;
            }

            // Do nothing if this isn't a module.
            if (key.indexOf(".module") == -1) {
                continue;
            }

            String value = kernelProperties.getProperty(key);

            if (StringUtils.isBlank(value)) {
                mLogger.error("no value for kernel key: " + key);
                continue;
            }

            if (mLogger.isDebugEnabled()) {
                mLogger.debug("kernel key: " + key + " - value: " + value);
            }

            modules.put(key, value);
        }

        // Get the config location strings.
        final String [] configLocations  
            = modules.values().toArray(new String[modules.size()]);

        // Load the context.
        final GenericApplicationContext kernelContext 
            = new GenericApplicationContext();

        final XmlBeanDefinitionReader xmlReader 
            = new XmlBeanDefinitionReader(kernelContext);

        for (String configLocation : configLocations) {
            xmlReader.loadBeanDefinitions(new ClassPathResource(configLocation));
        }

        // This is the order that spring requires.
        kernelContext.refresh();
        kernelContext.start();

        // Load the spring test component.
        final SpringLifecycleDiagnostic springLifecycleDiagnostic 
            = (SpringLifecycleDiagnostic)kernelContext.getBean("springLifecycleDiagnostic");
    
        // Add the service to the context. 
        pContext.put(KERNEL_CONTEXT, kernelContext);

        // Set the context in the kernel.
        Kernel.init(kernelContext);

        long startupTime = (System.currentTimeMillis() - startTime);

        // Debug.
        if (mLogger.isDebugEnabled()) {
            mLogger.debug(  "----- started:  kernel - start time: " 
                            + startupTime 
                            + " (ms) -----");
        }

        return CONTINUE_PROCESSING;
    }
}

