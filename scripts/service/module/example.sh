#!/bin/bash

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
# The example service.
#
# version: $Id: example.sh 20 2008-06-16 14:02:04Z oemware $
#
# chkconfig: - 85 15
# description: The example daemon.
# processname: example
#

service_name='example';

# Add any required config here.

. ${OEMWARE_DIR}/${service_name}/bin/default.sh

. ${OEMWARE_DIR}/${service_name}/bin/service_java.sh;
