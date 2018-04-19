#!/bin/bash
clear
miIp="$(ifconfig | grep -A 1 'eth0' | tail -1 | cut -d ':' -f 2 | cut -d ' ' -f 1)"

ips=( $miIp $@ )
idProceso=1

for (( i=0; i<${#ips[@]}; i++ )) do
	ipsAux=${ips[@]/${ips[$i]}}
    stringIps=`echo $ipsAux | tr '[ ]' '*'`
	echo "http://${ips[$i]}:8080/PracticaObligatoriaISIS/services/dispatcher/inicializar?proceso=1&idProceso=$idProceso&ips=$stringIps"
	(( idProceso++ ))
	echo "http://${ips[$i]}:8080/PracticaObligatoriaISIS/services/dispatcher/inicializar?proceso=2&idProceso=$idProceso&ips=$stringIps"
	(( idProceso++ ))
done