#!/bin/bash
ROOT=$TESTDIR/../../../www
(timeout 2 java WebServerMain $ROOT 12345 > /dev/null 2>&1) & (sleep 1 ; curl -s -I -X GET localhost:12345/tp_it.jpg | grep -i -a 'Content-Length')
wait
