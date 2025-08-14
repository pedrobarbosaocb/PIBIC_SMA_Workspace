#!/bin/bash
export CLASSPATH=$CLASPATH:$1
javac -cp "$JADE_CP:." *java
java jade.Boot -nomtp -gui -agents ${2}


