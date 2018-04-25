#!/bin/bash

clear
ips=( localhost $@ )
if ! [ -e /home/$USER/.ssh/id_rsa ]; then
	ssh-keygen
	for (( i=1; i<${#ips[@]}; i++ )) do
		ssh-copy-id -i ~/.ssh/id_rsa.pub $USER@${ips[$i]}
	done
fi

tar xzf PracticaObligatoriaISIS.tar.gz
chmod +x ./tomcat-ISIS/bin/*
./tomcat-ISIS/bin/startup.sh
for (( i=1; i<${#ips[@]}; i++ )) do
	./remotoDespliegue.sh ${ips[$i]}
done
