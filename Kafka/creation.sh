#!/bin/bash

echo "Starting..."
cd
sudo wget https://dlcdn.apache.org/zookeeper/zookeeper-3.9.3/apache-zookeeper-3.9.3-bin.tar.gz
sudo tar -zxf apache-zookeeper-3.9.3-bin.tar.gz
sudo mv apache-zookeeper-3.9.3-bin /usr/local/zookeeper
sudo mkdir -p /var/lib/zookeeper
echo "tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181" > /usr/local/zookeeper/conf/zoo.cfg 

sudo yum -y install java-17-amazon-corretto-devel.x86_64  

sudo /usr/local/zookeeper/bin/zkServer.sh start

sudo wget https://downloads.apache.org/kafka/3.9.0/kafka_2.13-3.9.0.tgz
sudo tar -zxf kafka_2.13-3.9.0.tgz
sudo mv kafka_2.13-3.9.0 /usr/local/kafka
sudo mkdir /tmp/kafka-logs

ip=`curl http://169.254.169.254/latest/meta-data/public-hostname`
sudo sed -i "s/#listeners=PLAINTEXT:\/\/:9092/listeners=PLAINTEXT:\/\/$ip:9092/g" /usr/local/kafka/config/server.properties

# due to AWS network stablishment process, check if 60 seconds is enough for your situation
(sleep 120 && sudo /usr/local/kafka/bin/kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties )&

echo "Finished."
