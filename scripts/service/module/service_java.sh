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
# The java service script. This foundation is useless by itself.
# You must include this and configure yourself.
#
#
# Make sure you have the following env variables set. These env
# variables can also be set in the calling script.
#
# JAVA_HOME
# OEMWARE_ENV_NAME
# OEMWARE_DIR
# OEMWARE_NODE_ID
#
# The calling script needs to set the following variables.
#
# service_name='example';
# instance_id=prod1;
# startup_class='oemware.core.ServiceManager';
#
# The calling script typcially includes the 'default.sh' config.
#
# version: $Id: service_java.sh 66 2008-09-16 02:55:25Z oemware $
#

#
# Set the env variables.
#
java_home=${JAVA_HOME};
env_name=${OEMWARE_ENV_NAME};
dir_conf="${OEMWARE_DIR}/conf";
data_dir="${OEMWARE_DATA_DIR}";
node_id=${OEMWARE_NODE_ID};

exec_username="";

is_linux=false;
if [ `uname` == 'Linux' ]; then
    is_linux=true;
else
    bind_cpu=-1; 
fi

app_dir="${OEMWARE_DIR}/${service_name}";

#
# Include the global node, node and instance conf.
#
. ${dir_conf}/node.properties
. ${dir_conf}/${service_name}-node.properties
. ${dir_conf}/${service_name}-instance-${instance_id}.properties


current_username=`whoami`;
exec_change_user=false;

#
# Check to see if the user should be changed.
#
if [ "${exec_username}" != "" ]; then
    if [ "${current_username}" == "root" ]; then
        exec_change_user=true;
    fi
fi

if [ "${log_dir}" == "" ]; then
    log_dir="${OEMWARE_DIR}/log";
fi

if [ ! -d ${log_dir} ]; then
    mkdir ${log_dir};
fi

#
# Setup the classpath.
#
app_classpath="${app_dir}/conf:${app_dir}/lib/java/${service_name}.jar";
app_classpath="${app_classpath}:${OEMWARE_DIR}/lib/java/*"

#
# Setup the arguments.
#
app_args="${app_args} -Doemware.service.name=${service_name} -Doemware.env.name=${env_name} -Doemware.data.dir=${data_dir} ";

app_args="${app_args} -Duser.timezone=GMT ";
app_args="${app_args} -Dfile.encoding=UTF-8 ";
app_args="${app_args} -server -XX:MaxHeapFreeRatio=70 -XX:MinHeapFreeRatio=40 -XX:+UseParallelGC";
app_args="${app_args} -Djava.net.preferIPv4Stack=true ";
app_args="${app_args} -Dsun.net.client.defaultReadTimeout=5000 ";
app_args="${app_args} -Dsun.net.client.defaultConnectTimeout=5000 ";

if [ ${is_linux} == true ]; then
    app_args="${app_args} -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider ";
fi

app_args="${app_args} ${jvm_mem_args} ";
app_args="${app_args} -Doemware.instance.id=${instance_id} ";
app_args="${app_args} -Doemware.node.id=${node_id} ";
app_args="${app_args} -Doemware.dir=${OEMWARE_DIR} ";
app_args="${app_args} -Doemware.app.dir=${app_dir} ";

app_args="${app_args} -Doemware.instance=${dir_conf}/${service_name}-instance-${instance_id}.properties ";
app_args="${app_args} -Doemware.module=${dir_conf}/${service_name}-node.properties ";
app_args="${app_args} -Doemware.node=${dir_conf}/node.properties ";

app_args="${app_args} -classpath ${app_classpath}";

#
# Setup the command variable.
#
app_command="${java_home}/bin/${service_name}-${instance_id}-j ${app_args} ${startup_class}";

#
# The startup function.
#
start() {
    start_command="";
    if [ "${bind_cpu}" != "-1" ]; then
        start_command="exec taskset -c ${bind_cpu} ${app_command} > ${log_dir}/${service_name}-${instance_id}-out.log 2>&1";
    else
        start_command="exec ${app_command} > ${log_dir}/${service_name}-${instance_id}-out.log 2>&1";
    fi 
    
    if [ ${exec_change_user} == true ]; then
        if [ ${is_linux} == true ]; then
            start_command="su - ${exec_username} -c \"${start_command}\"";
        fi
    fi

    nohup sh -c ${start_command} > /dev/null &
}

# 
# The shudown function.
#
stop() {
    shutdown_command="${java_home}/bin/java -client -classpath ${app_classpath} ${shutdown_class} ${common_udp_port} ${instance_id}";
    sh -c "exec ${shutdown_command}"; 
    sleep ${kill_sleep_time}

    #
    # Check to see if the server shutdown cleanly. If it didn't, kill the process.
    #
    if [ ${is_linux} == true ]; then
        pid=`ps -o pid,command ax | grep ${service_name}-${instance_id}-j | awk '!/awk/ && !/grep/ {print $1}'`;
    else
        pid=`ps -o pid,command -ax | grep ${service_name}-${instance_id}-j | awk '!/awk/ && !/grep/ {print $1}'`;
    fi

    if [ "${pid}" != "" ]; then
        kill -9 ${pid}; 
        echo "service did not shutdown cleanly: ${service_name} - instance: ${instance_id} - process killed: ${pid}";
    fi
}

#
# Run differs from start in that it doesn't background the process. Use for
# dev or non-servers.
#
run() {
    if [ "${bind_cpu}" != "-1" ]; then
        sh -c "exec taskset -c ${bind_cpu} ${app_command} > ${log_dir}/${service_name}-${instance_id}-out.log 2>&1" 
    else
        sh -c "exec ${app_command}" 
    fi
}

#
# Restart the process.
#
restart() {
    stop
    start
}

#
# Print a message about the process (if it's running).
#
status() {

    if [ $is_linux == true ]; then
        pid=`ps -o pid,command ax | grep ${service_name}-${instance_id}-j | awk '!/awk/ && !/grep/ {print $1}'`;
    else
        pid=`ps -o pid,command -ax | grep ${service_name}-${instance_id}-j | awk '!/awk/ && !/grep/ {print $1}'`;
    fi

    if [ "${pid}" != "" ]; then
        echo "service running: ${service_name}-${instance_id}-j - pid: ${pid}";
    else
        echo "service not running: ${service_name}-${instance_id}-j";
    fi
}

#
# Check to see what the command is and then dispatch the appropriate method.
#
case "$1" in
    start|stop|restart|run|status)
        $1
        ;;
    *)

    echo $"Usage: $0 {start|stop|restart|run|status}"
    exit 2
esac

