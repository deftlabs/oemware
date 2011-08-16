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

// Jakarta Commons
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The test bean. Used to test initialization on startup. 
 *
 * @author Ryan Nitz
 * @version $Id: SpringLifecycleDiagnostic.java 13 2008-06-15 19:43:04Z oemware $
 */
public class SpringLifecycleDiagnostic {

    private final Log mLogger = LogFactory.getLog(SpringLifecycleDiagnostic.class);

    public final void startup() {
        mLogger.debug("----- spring: lifecycle diagnostic bean - started");
    }

    public final void shutdown() {
        mLogger.debug("----- spring: lifecycle diagnostic bean - stopped");
    }
}

