#!/bin/bash

#echo "" >> /usr/local/kafka/config/server.properties
#echo "zookeeper.connect=${zk_connect}" >> /usr/local/kafka/config/server.properties

# Wait until the file exists
while [ ! -f /usr/local/zookeeper/conf/zoo.cfg ]; do
  echo "Waiting for zoo.cfg to be created..."
  sleep 5
done

if ! grep -q "${zoo_servers}" /usr/local/zookeeper/conf/zoo.cfg; then
  echo "" >> /usr/local/zookeeper/conf/zoo.cfg
  echo "${zoo_servers}" >> /usr/local/zookeeper/conf/zoo.cfg
  echo "zoo_servers appended to zoo.cfg"
else
  echo "zoo_servers already present in zoo.cfg, skipping append"
fi

# Restart ZooKeeper
sudo /usr/local/zookeeper/bin/zkServer.sh stop
sleep 2

#!/bin/bash

while true; do
  echo "Starting ZooKeeper..."
  sudo /usr/local/zookeeper/bin/zkServer.sh start

  echo "Waiting for ZooKeeper to initialize..."
  sleep 5

  if sudo /usr/local/zookeeper/bin/zkServer.sh status | grep -q "Mode:"; then
    echo "✅ ZooKeeper started successfully."
    break
  else
    echo "❌ ZooKeeper failed to start. Retrying in 5 seconds..."
    sleep 5
  fi
done

sudo yum -y install nc
while ! nc -z localhost 2181; do
  sleep 2
done

# Restart Kafka
sudo pkill -f kafka.Kafka
sleep 2

sudo /usr/local/kafka/bin/kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties