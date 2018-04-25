#!/bin/bash

ssh $USER@$1 << 'ENDSSH'
	./tomcat-ISIS/bin/shutdown.sh
ENDSSH
