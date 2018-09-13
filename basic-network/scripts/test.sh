#!/bin/bash

set -ev

export FABRIC_START_TIMEOUT=5

sleep ${FABRIC_START_TIMEOUT}

# Create the channel
peer channel create -o orderer.usoftchain.com:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx
# Join peer to the channel.
peer channel join -b mychannel.block
# install chaincode
peer chaincode install -n stockcontract -v 1.0 -p github.com/usoftchina/usoftchain-sample/chaincode/stock
# instantiate chaincode
peer chaincode instantiate -o orderer.usoftchain.com:7050 -C mychannel -n stockcontract -v 1.0 -c '{"Args":["init"]}' -P "OR ('XinningMSP.member')"
sleep 10
# invoke chaincode
peer chaincode invoke -o orderer.usoftchain.com:7050 -C mychannel -n stockcontract -c '{"Args":["createWarehouse","新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号"]}'