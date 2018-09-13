#!/bin/bash
#
# setup usoftchain_network
#

UP_DOWN="$1"
CH_NAME="$2"

HOSTS=("192.168.0.176" "192.168.0.177" "192.168.0.178" "192.168.0.179" "192.168.0.180")
declare -A ORDERERS=(["192.168.0.176"]="orderer")
declare -A PEERS=(["192.168.0.177"]="peer0-huasl" ["192.168.0.178"]="peer0-skypine" ["192.168.0.179"]="peer0-xinning" ["192.168.0.180"]="peer0-usoft")

function printHelp () {
	echo "Usage: ./network_setup <up|down|restart> <\$channel-name>"
}

function validateArgs () {
	if [ -z "${UP_DOWN}" ]; then
		echo "Option up / down / restart not mentioned"
		printHelp
		exit 1
	fi
	if [ -z "${CH_NAME}" ]; then
		echo "setting to default channel 'mychannel'"
		CH_NAME=mychannel
	fi
}

function gitPull () {
    git reset --hard
    git fetch
    git pull

    for key in ${!PEERS[@]}
    do
      ssh -T $key <<EOF
      rm -rf go/src/github.com/usoftchina/usoftchain-sample
      go get github.com/usoftchina/usoftchain-sample
      exit
EOF
    done
}

function copyConfig () {
    for key in ${!PEERS[@]}
    do
      scp -rp channel-artifacts $key:go/src/github.com/usoftchina/usoftchain-sample/example-network/
      scp -rp crypto-config $key:go/src/github.com/usoftchina/usoftchain-sample/example-network/
    done
}

function clearConfig () {
    for var in ${HOSTS[@]}
    do
      ssh -T $var <<EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/example-network/
      rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
      exit
EOF
    done
}

function startOrderer() {
    for key in ${!ORDERERS[@]}
    do
      file="docker-compose-${ORDERERS[$key]}.yaml"
      ssh -T $key <<EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/example-network
      docker-compose -f $file up -d
      exit
EOF
    done
}

function stopOrderer() {
    for key in ${!ORDERERS[@]}
    do
      file="docker-compose-${ORDERERS[$key]}.yaml"
      ssh -T $key <<EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/example-network
      docker-compose -f $file down
      exit
EOF
    done
}

function startPeer() {
    for key in ${!PEERS[@]}
    do
      file="docker-compose-${PEERS[$key]}.yaml"
      ssh -T $key <<EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/example-network
      docker-compose -f $file up -d
      exit
EOF
    done
}

function stopPeer() {
    for key in ${!PEERS[@]}
    do
      file="go/src/github.com/usoftchina/usoftchain-sample/example-network/docker-compose-${PEERS[$key]}.yaml"
      ssh $key "docker-compose -f $file down"
      # clear unwanted containers
      CONTAINER_IDS=$(ssh $key "docker ps -a | grep \"dev\|none\|test-vp\|peer[0-9]-\"" | awk '{print $1}')
      if [ -z "$CONTAINER_IDS" -o "$CONTAINER_IDS" = " " ]; then
          echo "---- No containers available for deletion ----"
      else
          ssh $key "docker rm -f $CONTAINER_IDS"
      fi
      # remove unwanted images
      DOCKER_IMAGE_IDS=$(ssh $key "docker images | grep \"dev\|none\|test-vp\|peer[0-9]-\"" | awk '{print $3}')
      if [ -z "$DOCKER_IMAGE_IDS" -o "$DOCKER_IMAGE_IDS" = " " ]; then
          echo "---- No images available for deletion ----"
      else
          ssh $key "docker rmi -f $DOCKER_IMAGE_IDS"
      fi
    done
}

function networkUp () {
    gitPull

#    if [ -d "./crypto-config" ]; then
#      echo "crypto-config directory already exists."
#    else
#      #Generate all the artifacts that includes org certs, orderer genesis block,
#      # channel configuration transaction
#      source generateArtifacts.sh $CH_NAME
#      copyConfig
#    fi

    startOrderer

    startPeer
}

function networkDown () {
    stopPeer

    stopOrderer

#    clearConfig
}

validateArgs

#Create the network using docker compose, run at 192.168.0.176
if [ "${UP_DOWN}" == "up" ]; then
	networkUp
elif [ "${UP_DOWN}" == "down" ]; then ## Clear the network
	networkDown
elif [ "${UP_DOWN}" == "restart" ]; then ## Restart the network
	networkDown
	networkUp
else
	printHelp
	exit 1
fi
