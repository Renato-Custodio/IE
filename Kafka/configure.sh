#!/bin/bash

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

# Wait until the file exists
while [ ! -f /usr/local/kafka/config/server.properties ]; do
  echo "Waiting for /usr/local/kafka/config/server.properties to be created..."
  sleep 5
done

if ! grep -q "${zk_connect}" /usr/local/kafka/config/server.properties; then
  echo "" >> /usr/local/kafka/config/server.properties
  echo "zookeeper.connect=${zk_connect}" >> /usr/local/kafka/config/server.properties
else
  echo "zk_connect already present in /usr/local/kafka/config/server.properties, skipping append"
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

# only create topics one time(in this case in the leader)

while true; do
  ids=$(sudo /usr/local/kafka/bin/zookeeper-shell.sh $(hostname):2181 ls /brokers/ids 2>/dev/null | grep '\[')

  if echo "$ids" | grep -q '1' && echo "$ids" | grep -q '2' && echo "$ids" | grep -q '3'; then
    echo "✅ All 3 brokers are up: $ids"
    break
  else
    echo "⏳ Waiting for brokers... current IDs: $ids"
    sleep 2
  fi
done

if echo stat | nc localhost 2181 | grep "leader"; then
  echo "Creating topics..."
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor 3 --partitions 3 --topic LoyaltyCard-at-shop
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor 3 --partitions 3 --topic DiscountCoupon
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor 3 --partitions 4 --topic CrossSellingRecommendation
fi
