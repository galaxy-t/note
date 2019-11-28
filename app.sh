#!/bin/sh

ACTION=$1

APP_NAME=$2


function start()
{
	rm -rf $APP_NAME.pid
	nohup java -jar $APP_NAME >/dev/null &
}

function stop()
{
	pid=`ps -ef |grep java|grep $APP_NAME|grep -v grep|awk '{print $2}'`	
	kill $pid;
}

function status()
{
	pid=`ps -ef |grep java|grep $APP_NAME|grep -v grep|awk '{print $2}'`
	echo $APP_NAME    $pid
}

case $ACTION in
	start)
	start;;
	stop)
	stop;;
	status)
	status;;
	*)
esac
