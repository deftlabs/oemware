#
# (C) Copyright 2007, Deft Labs.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published 
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

#
# The defaults. 
#
# version: $Id: default.sh 84 2008-09-27 17:21:13Z oemware $
#

kill_sleep_time=2;
bind_cpu=-1;
jvm_mem_args="-Xmx64m -Xms32m ";
app_args="";

#
# These are fairly constant.
#
startup_class='oemware.core.ServiceManager';
shutdown_class='oemware.core.net.ShutdownClient';

#
# The instance id is passed as the second param to this script. The first
# param is the action.
#
instance_id=$2;

#
# If it's not set then we default to one.
#
if [ "${instance_id}" == "" ]; then
    instance_id=1;
fi
