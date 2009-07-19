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
 * The action interface.
 *
 * @author Ryan Nitz
 * @version $Id$
 */
final class Action {

    private final String mName; 

    /**
     * Construct the action object.
     * @param pName The action name.
     */
    Action(final String pName) { mName = pName; }

    /**
     * Returns the name of the action.
     * @return THe name of the action.
     */
    String getName() { return mName; }

}

