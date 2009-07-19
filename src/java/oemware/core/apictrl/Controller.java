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

/**
 * The controller interface.
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public interface Controller {

    /**
     * Add an action to a controller.
     * @param pAction The action.
     */
    public void addAction(final Action pAction);

    /**
     * Find an action based on a name.
     * @param pActionName The action name.
     * @return The action or null if not found.
     */
    public Action findAction(final String pActionName);

}

