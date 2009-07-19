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

package oemware.unit.core.apictrl;

// OEMware
import oemware.core.apictrl.ApiController;
import oemware.core.apictrl.ActionContext;;

// JUnit
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The api controller tests.
 *
 * @author Ryan Nitz
 * @version $Id$
 */
public final class ApiControllerTests {

    private static final String TEST_FILE 
    = "data/unit/apictrl/apictrl-test.xml";

    @Test
    public void tesInit() throws Exception {
        final ApiController controller = new ApiController("test", TEST_FILE);
        assertNotNull(controller); 
    }
    
    @Test
    public void tesActionCalls() throws Exception {
        final ApiController controller = new ApiController("test", TEST_FILE);
        final ActionContext context = new ActionContext();
        controller.executeAction("test", "oneTest", context);
        controller.executeAction("test", "twoTest", context);

        
    }
}

