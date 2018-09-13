#!/bin/bash

set -ev

export FABRIC_START_TIMEOUT=5

sleep ${FABRIC_START_TIMEOUT}

# Create the channel
peer channel create -o orderer.usoftchain.com:7050 -c mychannel -f ./channel-artifacts/channel.tx
# Join peer to the channel.
peer channel join -b mychannel.block
# install chaincode
peer chaincode install -n stockcontract -v 1.0 -p github.com/usoftchina/usoftchain-sample/chaincode/stock
# instantiate chaincode
peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n stockcontract -v 1.0 -c '{"Args":["init"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"