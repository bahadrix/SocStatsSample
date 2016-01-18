#!/usr/bin/env bash
echo "--------------------------------------------"
echo "Scala-Spark Sample App. by Bahadir Katipoglu"
echo "--------------------------------------------"

THREADS=2
M=20G
m=20G

d=0
cmd="~run 9010"
rpmCacheSize=200000

if [[ $# -eq 0 ]] ; then
    echo "Usage: load.sh [OPTS]"
    echo "OPTS:"
    echo "-t|--threads threadNum    : Thread count for SPARK. Default: $THREADS"
    echo "-mx|--max-memory maxHeap  : JVM Xmx. Default: $M"
    echo "-ms|--min-memory minHeap  : JVM Xms. Default: $m"
    echo "-rc|--rpm-cache-size		: RPM Cache size Default: 200000"
    echo "-d|--debug port           : Debug port for remote debugging."
    echo "-c|--command              : Run command for activator. Default: $cmd"


    exit 0
fi

acthome=${ACTIVATOR_HOME:?"Please set ACTIVATOR_HOME environment variable (eg /opt/activator-dist-1.3.5) or make the activator command accessible from everywhere. "}

while [[ $# > 1 ]]
do
	key="$1"

	case $key in
		-t|--threads)
		THREADS="$2"
		shift
		;;
		-mx|--max-memory)
		M="$2"
		shift
		;;
		-ms|--min-memory)
		m="$2"
		shift
		;;
		-d|--debug)
		d="$2"
		shift
		;;
		-rc|--rpm-cache-size)
		rpmCacheSize="$2"
		shift
		;;
        -c|--command)
        cmd="$2"
        shift
        ;;
		*)

		;;
esac
shift
done

debugArg=""


if [[ $d -ne 0 ]];then
      debugArg="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$d"
fi

export SPARK_MODE="local[$THREADS]"
export RPM_CACHE_SIZE=${rpmCacheSize}
echo "Spark mode    	: $SPARK_MODE"
echo "Memory        	: $m - $M"
echo "RPM Cache Size	: $rpmCacheSize"
echo "Activator home	: $acthome"
echo "Activator cmd 	: $cmd"
echo "Debug Args    	: $debugArg"
echo "--------------------------------------------"

until java -Dactivator.home=$acthome -Xms$m -Xmx$M -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled  -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=256m $debugArg -jar $acthome/activator-launch-*.jar "$cmd"; do
    echo "WLSA Server halted with exit code $?.  Respawning in 3 seconds.." >&2
    sleep 3
done