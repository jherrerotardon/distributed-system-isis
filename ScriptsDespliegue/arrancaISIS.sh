clear
miIp=`echo $(ifconfig | grep -A 1 'eth0' | tail -1 | cut -d ':' -f 2 | cut -d ' ' -f 1)`

ips=( $miIp $@ )
idProceso=1
for (( i=0; i<${#ips[@]}; i++ )) do
	ipsAux=${ips[@]/${ips[$i]}}
	stringIps=`echo $ipsAux | tr '[ ]' '*'`
	curl "http://${ips[$i]}:8080/PracticaObligatoriaISIS/services/dispatcher/inicializar?proceso=1&idproceso=$idProceso&ips=$stringIps" &
	(( idProceso++ ))
	curl "http://${ips[$i]}:8080/PracticaObligatoriaISIS/services/dispatcher/inicializar?proceso=2&idproceso=$idProceso&ips=$stringIps" &
	(( idProceso++ ))
done
