#!/bin/sh

cp src/main/docker/Dockerfile target/

docker build -t catalogue-metadata -f target/Dockerfile target/.