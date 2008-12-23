#!/bin/bash

if [ "`uname`" == "Darwin" ]
then
  export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home
  export PATH=$JAVA_HOME/bin:$PATH
fi

BINDIR=`dirname $0`
BASEDIR=`dirname ${BINDIR}`


CP=`find lib -maxdepth 1 -name '*.jar'`
CP=`echo $CP | tr ' ' ':'`
CP=${BASEDIR}/build/java:${CP}

java -Xmx256M -cp "$CP:$PARENT_CP" $XARGS concurrentinc.simulator.Dump $*
