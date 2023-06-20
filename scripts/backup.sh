#!/bin/sh

tar -cvf backup.tar data/characters data/grandexchange data/clans.json ; gzip --best backup.tar

aws s3 cp backup.tar.gz s3://zenyte-backup/server/ --storage-class GLACIER

rm backup.tar.gz
