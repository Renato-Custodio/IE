#!/bin/bash
cd

echo "Updating Packages"
sudo yum update -y

echo "Downloading Ollama"
sudo curl -fsSL https://ollama.com/install.sh | sh

export HOME=$HOME:/usr/local/bin

echo "Binding Ollama to 0.0.0.0:11434"
sudo sed -i "s/\[Install\]/Environment=\"OLLAMA_HOST=0.0.0.0:11434\"\n\[Install\]/g" /etc/systemd/system/ollama.service

echo "Enabling and starting ollama"
sudo systemctl enable ollama
sudo systemctl start ollama

echo "Pulling Ollama model llama3.2. Sleeping 5 seconds."
sleep 5
ollama pull llama3.2
echo "Success pulling Ollama model llama3.2"