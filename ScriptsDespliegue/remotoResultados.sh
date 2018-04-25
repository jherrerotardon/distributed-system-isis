#!/bin/bash

sftp $USER@$1 << 'ENDSFTP'
	get proceso*.log
	bye
ENDSFTP
