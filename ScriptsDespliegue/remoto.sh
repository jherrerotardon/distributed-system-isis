#!/bin/bash

sftp $USER@$1 << 'ENDSFTP'
	put hola.txt
	bye
ENDSFTP

ssh $USER@$1 << 'ENDSSH'
	tar xzf PracticaObligatoriaISIS.tar.gz 
	chmod +x ./tomcat-ISIS/bin/*
	./tomcat-ISIS/bin/startup.sh
ENDSSH


