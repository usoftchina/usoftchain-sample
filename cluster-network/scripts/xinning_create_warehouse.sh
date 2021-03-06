#!/bin/bash

CHANNEL_NAME="$1"
: ${CHANNEL_NAME:="mychannel"}

echo "Channel name : "$CHANNEL_NAME

setGlobals () {
    CORE_PEER_LOCALMSPID="xinningMSP"
    CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/xinning.example.com/peers/peer0.xinning.example.com/tls/ca.crt
    CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/xinning.example.com/users/Admin@xinning.example.com/msp
    CORE_PEER_ADDRESS=peer0.xinning.example.com:7051

    env |grep CORE
}

chaincodeInvoke () {
    setGlobals

    ORDERER_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem
    PEER_CONN_PARMS="--peerAddresses peer0.xinning.example.com:7051 --tlsRootCertFiles $CORE_PEER_TLS_ROOTCERT_FILE"

    if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
        peer chaincode invoke -o orderer.example.com:7050 -C $CHANNEL_NAME -n stockcontract $PEER_CONN_PARMS -c '{"Args":["createWarehouse","新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号"]}' >&log.txt
    else
        peer chaincode invoke -o orderer.example.com:7050  --tls --cafile $ORDERER_CA -C $CHANNEL_NAME -n stockcontract $PEER_CONN_PARMS -c '{"Args":["createWarehouse","新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号"]}' >&log.txt
    fi
    cat log.txt
}

chaincodeInvoke

echo
echo "===================== Completed ===================== "
echo

exit 0