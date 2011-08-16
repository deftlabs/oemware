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

package com.deftlabs.core.util;

// OEMware
import com.deftlabs.core.OemException;

// Java
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.InvalidPropertiesFormatException;

/**
 * The classpath utils.
 */
public final class ClasspathUtils {

    /**
     * Load properties from an xml config file in the path.
     * @param pResourceName The resource name.
     * @return The properties object.
     * @throws OemException
     */
    public final static Properties loadXmlPropertes(final String pResourceName)
        throws OemException
    {
        final Properties properties = new Properties();
        InputStream inputStream = null;

        try {
            inputStream
                = ClasspathUtils.openResourceUrl(pResourceName);
            properties.loadFromXML(inputStream);

        } catch (InvalidPropertiesFormatException ipfe) {
            String errorMessage
                = "invalid resource format - name: "
                + pResourceName
                + " - root error message: "
                + ipfe.getMessage();
            throw new OemException(errorMessage, ipfe);
        } catch (IOException ioe) { throw new OemException(ioe);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException ioe) { throw new OemException(ioe); }
            }
        }
        return properties;
    }

    /**
     * Open a resource url input stream. Look at the loadResourceUrl method
     * for a description of how the resource is searched for.
     * @param pName The resource name.
     * @return The open input stream.
     * @throws OemException
     */
    public final static InputStream openResourceUrl(final String pName)
        throws OemException
    {
        final URL resourceUrl = loadResourceUrl(pName);
        try { return resourceUrl.openStream();
        } catch (IOException ioe) {
            throw new OemException("error opening resource: " + pName, ioe);
        }
    }

    /**
     * Load a resource url. This looks at the current class loader
     * first and if the resource isn't found it looks at the system
     * class loader. If it's not found anywhere, an exception is thrown.
     * @param pName The resource name.
     * @return The resource url.
     * @throws OemException
     */
    public final static URL loadResourceUrl(final String pName) throws OemException {
        // Verify the name is set.
        if (StringUtils.isBlank(pName)) {
            throw new OemException("resource name not set - value = '"
                                    + pName
                                    + "'");
        }

        try {
            // Try the thread class loader first.
            ClassLoader classLoader
                = Thread.currentThread().getContextClassLoader();

            URL url = null;

            if (classLoader != null) {
                url = classLoader.getResource(pName);
                if (url != null) return url;
            }

            // We didn't have any luck in the parent. Try the system
            // class loader.
            classLoader = ClassLoader.getSystemClassLoader();

            url = classLoader.getResource(pName);

            // The resource isn't found (anywhere). Throw an exception.
            if (url == null) {
                throw new OemException(    "resource not found in classpath "
                                            + "- name: '"
                                            + pName
                                            + "'");
            }

            return url;

        } catch (Throwable t) { throw new OemException(t); }
    }
}

