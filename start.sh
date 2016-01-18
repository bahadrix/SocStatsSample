#!/usr/bin/env bash
echo "--------------------------------------------"
echo "Scala-Spark Sample App. by Bahadir Katipoglu"
echo "--------------------------------------------"

THREADS=2
M=20G
m=20G

d=0
cmd="~run"

if [[ $# -eq 0 ]] ; then
    echo "Usage: load.sh [OPTS]"
    echo "OPTS:"
    echo "-t|--threads threadNum    : Thread count for SPARK. Default: $THREADS"
    echo "-mx|--max-memory maxHeap  : JVM Xmx. Default: $M"
    echo "-ms|--min-memory minHeap  : JVM Xms. Default: $m"
    echo "-d|--debug port           : Debug port for remote debugging."
    echo "-c|--command              : Run command for activator. Default: $cmd"


    exit 0
fi


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
echo "Spark mode    	: $SPARK_MODE"
echo "Memory        	: $m - $M"
echo "RPM Cache Size	: $rpmCacheSize"
echo "Activator home	: $acthome"
echo "Activator cmd 	: $cmd"
echo "Debug Args    	: $debugArg"
echo "--------------------------------------------"
# java -Xms1024m -Xmx1024m -XX:ReservedCodeCacheSize=128m -XX:MaxMetaspaceSize=256m -jar /usr/share/sbt-launcher-packaging/bin/sbt-launch.jar

java -Xms$m -Xmx$M -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled  -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=256m $debugArg -jar /usr/share/sbt-launcher-packaging/bin/sbt-launch.jar "$cmd"


