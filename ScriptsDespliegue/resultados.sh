#!/bin/bash

clear
ips=( $@ )

for (( i=0; i<${#ips[@]}; i++ )) do
	./remotoResultados.sh ${ips[$i]}
done


for (( i=2; i<=(${#ips[@]}+1)*2; i++ )) do
	echo "Comparando diferencias proceso1 con proceso$i..."
	diff proceso1.log proceso$i.log
done


