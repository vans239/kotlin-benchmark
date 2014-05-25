#!/bin/bash
set -e -o pipefail

TEST="Capacity"
LOG="Kotlin/result.log"
FULL_LOG="full_result.log"
#COMMAND="mvn -f Kotlin/pom.xml clean integration-test -Dwi=1 -Di=1"
COMMAND="mvn -f Kotlin/pom.xml clean integration-test -Dwi=10 -Di=10"

benchmarkProps=("-DbenchmarkRegexp=.*Capacity.*" "-DbenchmarkRegexp=.*InliningWins.*")
javaProps=( "-Drun=1.6-0.7.270" "-Drun=1.7-0.7.270" "-Drun=1.8-0.7.270" )
inliningProps=( "-Dinline=false" "-Dinline=true")

if [ -f $FULL_LOG ]; 
	then rm $FULL_LOG
fi

for benchmarkProp in "${benchmarkProps[@]}"
do
    for javaProp in "${javaProps[@]}"
    do
        for inliningProp in "${inliningProps[@]}"
        do
            FULL_COMMAND="$COMMAND $javaProp $inliningProp $benchmarkProp"
            echo $FULL_COMMAND
            $FULL_COMMAND
            echo $FULL_COMMAND >> $FULL_LOG
            cat $LOG >> $FULL_LOG
            echo -e "\n\n\n\n" >> $FULL_LOG
        done
    done
done


