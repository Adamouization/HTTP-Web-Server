#!/bin/bash
ROOT=$TESTDIR/../../../Resources/www
(timeout 2 java WebServerMain $ROOT 12345 > /dev/null 2>&1) & (sleep 1 ; curl -s -I -X GET localhost:12345/f1.png | grep -i -a 'Content-Type')
wait
