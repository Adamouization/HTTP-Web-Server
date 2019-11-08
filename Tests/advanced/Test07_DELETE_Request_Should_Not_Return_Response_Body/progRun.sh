#!/bin/bash
ROOT=$TESTDIR/../../../www
(timeout 2 java WebServerMain $ROOT 12345 > /dev/null 2>&1 ) & (sleep 1 ; curl -s -X DELETE localhost:12345/delete_me1.txt )
wait
