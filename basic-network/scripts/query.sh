#!/bin/bash
#

CHANNEL_NAME="$1"
: ${CHANNEL_NAME:="mychannel"}

echo "Channel name : "$CHANNEL_NAME

setGlobals () {
	CORE_PEER_LOCALMSPID="huaslMSP"
    CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/huasl.usoftchain.com/peers/peer0.huasl.usoftchain.com/tls/ca.crt
    CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/huasl.usoftchain.com/users/Admin@huasl.usoftchain.com/msp
    CORE_PEER_ADDRESS=peer0.huasl.usoftchain.com:7051

	env |grep CORE
}

chaincodeQuery () {
	setGlobals

	echo "===================== Querying on channel '$CHANNEL_NAME'... ===================== "
    peer chaincode query -C $CHANNEL_NAME -n stockcontract -c '{"Args":["queryWarehouse","新宁"]}' >&log.txt
    cat log.txt
}

chaincodeQuery

echo
echo "===================== Completed ===================== "
echo

exit 0
