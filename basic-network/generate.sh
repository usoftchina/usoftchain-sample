#!/bin/sh
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
export FABRIC_ROOT=$PWD/../../../hyperledger/fabric
OS_ARCH=$(echo "$(uname -s|tr '[:upper:]' '[:lower:]'|sed 's/mingw64_nt.*/windows/')-$(uname -m | sed 's/x86_64/amd64/g')" | awk '{print tolower($0)}')
export PATH=$FABRIC_ROOT/release/$OS_ARCH/bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
CHANNEL_NAME=mychannel

# remove previous crypto material and config transactions
rm -fr config/*
rm -fr crypto-config/*

# generate crypto material
cryptogen generate --config=./crypto-config.yaml
if [ "$?" -ne 0 ]; then
  echo "Failed to generate crypto material..."
  exit 1
fi

# generate genesis block for orderer
configtxgen -profile UsoftchainOrdererGenesis -outputBlock ./config/genesis.block
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
fi

# generate channel configuration transaction
configtxgen -profile UsoftchainChannel -outputCreateChannelTx ./config/channel.tx -channelID $CHANNEL_NAME
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi

# generate anchor peer transaction
configtxgen -profile UsoftchainChannel -outputAnchorPeersUpdate ./config/huaslMSPanchors.tx -channelID $CHANNEL_NAME -asOrg huaslMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for huaslMSP..."
  exit 1
fi
configtxgen -profile UsoftchainChannel -outputAnchorPeersUpdate ./config/skypineMSPanchors.tx -channelID $CHANNEL_NAME -asOrg skypineMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for skypineMSP..."
  exit 1
fi
configtxgen -profile UsoftchainChannel -outputAnchorPeersUpdate ./config/xinningMSPanchors.tx -channelID $CHANNEL_NAME -asOrg xinningMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for xinningMSP..."
  exit 1
fi
configtxgen -profile UsoftchainChannel -outputAnchorPeersUpdate ./config/usoftMSPanchors.tx -channelID $CHANNEL_NAME -asOrg usoftMSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for usoftMSP..."
  exit 1
fi
