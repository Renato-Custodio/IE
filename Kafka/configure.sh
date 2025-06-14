#!/bin/bash

# Wait until the file exists
while [ ! -f /usr/local/zookeeper/conf/zoo.cfg ]; do
  echo "Waiting for zoo.cfg to be created..."
  sleep 5
done

if ! grep -q "${zoo_servers}" /usr/local/zookeeper/conf/zoo.cfg; then
  echo "" | sudo tee -a /usr/local/zookeeper/conf/zoo.cfg
  echo "${zoo_servers}" | sudo tee -a /usr/local/zookeeper/conf/zoo.cfg
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
  echo "" | sudo tee -a /usr/local/kafka/config/server.properties
  echo "zookeeper.connect=${zk_connect}" | sudo tee -a /usr/local/kafka/config/server.properties
else
  echo "zk_connect already present in /usr/local/kafka/config/server.properties, skipping append"
fi

# Restart ZooKeeper
sudo /usr/local/zookeeper/bin/zkServer.sh stop
sleep 2

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

echo "Waiting for ${broker_count} brokers"

while true; do
  ids=$(sudo /usr/local/kafka/bin/zookeeper-shell.sh $(hostname):2181 ls /brokers/ids 2>/dev/null | grep '\[')

  all_up=true
  for i in $(seq 1 ${broker_count}); do
    if ! echo "$ids" | grep -q "$i"; then
      all_up=false
      break
    fi
  done

  if [ "$all_up" = true ]; then
    echo "✅ All ${broker_count} brokers are up: $ids"
    break
  else
    echo "⏳ Waiting for brokers... current IDs: $ids"
    sleep 2
  fi
done

if echo stat | nc localhost 2181 | grep "leader"; then
  echo "Creating topics..."
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 3 --topic DiscountCoupon
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 4 --topic CrossSellingRecommendation
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 6 --topic SelledProduct-Coupon
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 6 --topic SelledProduct-Customer
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 6 --topic SelledProduct-Location
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 6 --topic SelledProduct-LoyaltyCard
  sudo /usr/local/kafka/bin/kafka-topics.sh --create --bootstrap-server $(hostname):9092 --replication-factor "${broker_count}" --partitions 6 --topic SelledProduct-Shop
fi
