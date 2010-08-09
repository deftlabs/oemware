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
import oemware.core.CoreException;

// Java
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBElement; 
import javax.xml.namespace.QName;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * The jaxb xml utils. 
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public final class JaxbUtils {

    /**
     * Converts jaxb classes to Xml (marshal).
     * @param pFileName The file name. This expects the file in the classpath.
     * @param pPackage The package where the jaxb generated files are 
     * located.
     * @param pRootElementName The root element name.
     * @param pRootType The root class type.
     * @param pObject The object to marshall.
     * @throws CoreException
     */
    public static void jaxbMarshal( final String pFileName, 
                                    final String pPackage,
                                    final String pRootElementName,
                                    final Class pRootType,
                                    final Object pObject) 
        throws CoreException
    {
        jaxbMarshal(pFileName, 
                    pPackage, 
                    pRootElementName,
                    pRootType, 
                    pObject, 
                    false);
    }

    /**
     * Converts jaxb classes to Xml (marshal).
     * @param pOutput The output stream.
     * @param pPackage The package where the jaxb generated files are 
     * located.
     * @param pRootElementName The root element name.
     * @param pRootType The root class type.
     * @param pObject The object to marshall.
     * @param pHumanReadable The flag to put eol and indents. Warning - This
     * takes a lot more space. 
     * @throws CoreException
     */
    @SuppressWarnings(value = "unchecked")
    public static void jaxbMarshal( final OutputStream pOutput,
                                    final String pPackage,
                                    final String pRootElementName,
                                    final Class pRootType,
                                    final Object pObject,
                                    final boolean pHumanReadable) 
        throws CoreException
    {
        try {
            final JAXBContext context = JAXBContext.newInstance(pPackage);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty( "jaxb.formatted.output", pHumanReadable);

            marshaller.marshal( new JAXBElement( new QName("", pRootElementName), 
                                pRootType, 
                                pObject), 
                                pOutput);

        } catch (JAXBException jaxbe) { throw new CoreException(jaxbe); }
    }

    /**
     * Converts jaxb classes to Xml (marshal).
     * @param pFileName The file name. This expects the file in the classpath.
     * @param pPackage The package where the jaxb generated files are 
     * located.
     * @param pRootElementName The root element name.
     * @param pRootType The root class type.
     * @param pObject The object to marshall.
     * @param pHumanReadable The flag to put eol and indents. Warning - This
     * takes a lot more space. 
     * @throws CoreException
     */
    @SuppressWarnings(value = "unchecked")
    public static void jaxbMarshal( final String pFileName, 
                                    final String pPackage,
                                    final String pRootElementName,
                                    final Class pRootType,
                                    final Object pObject,
                                    final boolean pHumanReadable) 
        throws CoreException
    {
        try {
            final OutputStream output = new FileOutputStream(pFileName);

            try {

                final JAXBContext context = JAXBContext.newInstance(pPackage);
                final Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty( "jaxb.formatted.output", pHumanReadable);

                marshaller.marshal( new JAXBElement( new QName("", pRootElementName), 
                                    pRootType, 
                                    pObject), 
                                    output);

            } finally { if (output != null) output.close(); }
        } catch (JAXBException jaxbe) { throw new CoreException(jaxbe);
        } catch (IOException ioe) { throw new CoreException(ioe); }
    }

    /**
     * Does a jaxb parse.
     * @param pFileName The file name. This expects the file in the classpath.
     * @param pPackage The package where the jaxb generated files are 
     * located.
     * @return The object.
     * @throws CoreException
     */
    public static Object jaxbUnmarshal( final String pFileName, 
                                        final String pPackage) 
        throws CoreException
    {
        try {
            final InputStream input = ClasspathUtils.openResourceUrl(pFileName);
            try {

                final JAXBContext context = JAXBContext.newInstance(pPackage);
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                final JAXBElement element = (JAXBElement)unmarshaller.unmarshal(input);

                return element.getValue();

            } finally { if (input != null) input.close(); }
        } catch (JAXBException jaxbe) { throw new CoreException(jaxbe);
        } catch (IOException ioe) { throw new CoreException(ioe); }
    }

    /**
     * Does a jaxb parse.
     * @param pFileName The file name. This expects the file in the classpath.
     * @param pPackage The package where the jaxb generated files are 
     * located.
     * @param pSchemaFile The schema file for validating.
     * @return The object.
     * @throws CoreException
     */
    public static Object jaxbUnmarshal( final String pFileName, 
                                        final String pPackage,
                                        final String pSchemaFile) 
        throws CoreException
    {
        try {
            final InputStream input = ClasspathUtils.openResourceUrl(pFileName);
            try {

                final SchemaFactory sf 
                = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

                final Schema schema 
                = sf.newSchema(new StreamSource(ClasspathUtils.openResourceUrl(pSchemaFile)));

                final JAXBContext context = JAXBContext.newInstance(pPackage);
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                unmarshaller.setSchema(schema);

                final JAXBElement element = (JAXBElement)unmarshaller.unmarshal(input);

                return element.getValue();

            } finally { if (input != null) input.close(); }
        } catch (final Exception e) { throw new CoreException(e); }
    }

    /**
     * Does a jaxb parse.
     * @param pInput The input stream.
     * @param pPackage The package where the jaxb generated files are 
     * located.
     * @return The object.
     * @throws CoreException
     */
    public static Object jaxbUnmarshal( final InputStream pInput,
                                        final String pPackage) 
        throws CoreException
    {
        try {
            final JAXBContext context = JAXBContext.newInstance(pPackage);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final JAXBElement element = (JAXBElement)unmarshaller.unmarshal(pInput);
            return element.getValue();
        } catch (JAXBException jaxbe) { throw new CoreException(jaxbe); }
    }
}

