#!/bin/bash

clear
ips=( $@ )

./tomcat-ISIS/bin/shutdown.sh
for (( i=0; i<${#ips[@]}; i++ )) do
	./remotoApagar.sh ${ips[$i]}
done



