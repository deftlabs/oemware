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

/**
 * The service exception.
 *
 * @author Ryan Nitz
 * @version $Id: ServiceException.java 13 2008-06-15 19:43:04Z oemware $
 */
public class ServiceException extends CoreException {
    private static final long serialVersionUID = 6177008817373465919L;
    public ServiceException() { super(); }
    public ServiceException(String pMessage) { super(pMessage); }
    public ServiceException(Throwable pThrowable) { super(pThrowable); }
    public ServiceException(String pMessage, Throwable pThrowable) 
    { super(pMessage, pThrowable); }
}

