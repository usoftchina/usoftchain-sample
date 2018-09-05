#!/bin/bash
#
# Copyright IBM Corp. All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#


UP_DOWN="$1"
CH_NAME="$2"

HOSTS=("192.168.0.176" "192.168.0.177" "192.168.0.178" "192.168.0.179" "192.168.0.180")
ZOOKEEPERS=(["192.168.0.176"]="zookeeper0" ["192.168.0.177"]="zookeeper1" ["192.168.0.178"]="zookeeper2")
KAFKAS=(["192.168.0.176"]="kafka0" ["192.168.0.177"]="kafka1" ["192.168.0.178"]="kafka2" ["192.168.0.179"]="kafka3")
ORDERERS=(["192.168.0.176"]="orderer")
PEERS=(["192.168.0.177"]="peer0-org1" ["192.168.0.178"]="peer1-org1" ["192.168.0.179"]="peer0-org2" ["192.168.0.180"]="peer1-org2")

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
    for var in ${HOSTS[@]}
    do
      ssh -tt $var << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample
      git reset --hard
      git fetch
      git pull
      exit
      EOF
    done
}

function startZookeeper () {
    for key in ${!ZOOKEEPERS[@]}
    do
      file="docker-compose-${ZOOKEEPERS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file up -d
      exit
      EOF
    done
}

function stopZookeeper () {
    for key in ${!ZOOKEEPERS[@]}
    do
      file="docker-compose-${ZOOKEEPERS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file down
      exit
      EOF
    done
}

function startKafka () {
    for key in ${!KAFKAS[@]}
    do
      file="docker-compose-${KAFKAS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file up -d
      exit
      EOF
    done
}

function stopKafka () {
    for key in ${!KAFKAS[@]}
    do
      file="docker-compose-${KAFKAS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file down
      exit
      EOF
    done
}

function copyConfig () {
    for key in ${!PEERS[@]}
    do
      scp -rp channel-artifacts $key:go/src/github.com/usoftchina/usoftchain-sample/e2e_cli/
      scp -rp crypto-config $key:go/src/github.com/usoftchina/usoftchain-sample/e2e_cli/
    done
}

function clearConfig () {
    for var in ${HOSTS[@]}
    do
      ssh -tt $var << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli/
      rm -rf channel-artifacts/*.block channel-artifacts/*.tx crypto-config
      exit
      EOF
    done
}

function startOrderer() {
    for key in ${!ORDERERS[@]}
    do
      file="docker-compose-${ORDERERS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file up -d
      exit
      EOF
    done
}

function stopOrderer() {
    for key in ${!ORDERERS[@]}
    do
      file="docker-compose-${ORDERERS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file down
      exit
      EOF
    done
}

function startPeer() {
    for key in ${!PEERS[@]}
    do
      file="docker-compose-${PEERS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file up -d
      exit
      EOF
    done
}

function stopPeer() {
    for key in ${!PEERS[@]}
    do
      file="docker-compose-${PEERS[$key]}.yaml"
      ssh -tt $key << EOF
      cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli
      docker-compose -f $file down
      exit
      EOF
    done
}

function networkUp () {
    gitPull

    if [ -d "./crypto-config" ]; then
      echo "crypto-config directory already exists."
    else
      #Generate all the artifacts that includes org certs, orderer genesis block,
      # channel configuration transaction
      source generateArtifacts.sh $CH_NAME
      copyConfig
    fi

    startZookeeper

    startKafka

    startOrderer

    startPeer
}

function networkDown () {
    stopPeer

    stopOrderer

    stopKafka

    stopZookeeper

    clearConfig
}

validateArgs

#Create the network using docker compose, use at 192.168.0.176
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
