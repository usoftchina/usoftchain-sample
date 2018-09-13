#!/bin/bash

CHANNEL_NAME="$1"
: ${CHANNEL_NAME:="mychannel"}
: ${TIMEOUT:="60"}
COUNTER=1
MAX_RETRY=5
ORDERER_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/usoftchain.com/orderers/orderer.usoftchain.com/msp/tlscacerts/tlsca.usoftchain.com-cert.pem
PEER0_HUASL_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/huasl.usoftchain.com/peers/peer0.huasl.usoftchain.com/tls/ca.crt
PEER0_SKYPINE_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/skypine.usoftchain.com/peers/peer0.skypine.usoftchain.com/tls/ca.crt
PEER0_XINNING_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/xinning.usoftchain.com/peers/peer0.xinning.usoftchain.com/tls/ca.crt
PEER0_USOFT_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/usoft.usoftchain.com/peers/peer0.usoft.usoftchain.com/tls/ca.crt

echo "Channel name : "$CHANNEL_NAME

verifyResult () {
	if [ $1 -ne 0 ] ; then
		echo "!!!!!!!!!!!!!!! "$2" !!!!!!!!!!!!!!!!"
                echo "================== ERROR !!! FAILED to execute End-2-End Scenario =================="
		echo
   		exit 1
	fi
}

setGlobals () {
	PEER=$1
	if [ "$PEER" == "peer0.huasl" ] ; then
		CORE_PEER_LOCALMSPID="huaslMSP"
		CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_HUASL_CA
		CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/huasl.usoftchain.com/users/Admin@huasl.usoftchain.com/msp
		CORE_PEER_ADDRESS=peer0.huasl.usoftchain.com:7051
	elif [ "$PEER" == "peer0.skypine" ] ; then
		CORE_PEER_LOCALMSPID="skypineMSP"
		CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_SKYPINE_CA
        CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/skypine.usoftchain.com/users/Admin@skypine.usoftchain.com/msp
        CORE_PEER_ADDRESS=peer0.skypine.usoftchain.com:7051
	elif [ "$PEER" == "peer0.xinning" ] ; then
    	CORE_PEER_LOCALMSPID="xinningMSP"
    	CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_XINNING_CA
        CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/xinning.usoftchain.com/users/Admin@xinning.usoftchain.com/msp
        CORE_PEER_ADDRESS=peer0.xinning.usoftchain.com:7051
    else
    	CORE_PEER_LOCALMSPID="usoftMSP"
    	CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_USOFT_CA
        CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/usoft.usoftchain.com/users/Admin@usoft.usoftchain.com/msp
        CORE_PEER_ADDRESS=peer0.usoft.usoftchain.com:7051
	fi

	env |grep CORE
}

createChannel() {
	if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
		peer channel create -o orderer.usoftchain.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx >&log.txt
	else
		peer channel create -o orderer.usoftchain.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx --tls --cafile $ORDERER_CA >&log.txt
	fi
	cat log.txt
	echo "===================== Channel '$CHANNEL_NAME' created ===================== "
	echo
}

## Sometimes Join takes time hence RETRY atleast for 5 times
joinChannelWithRetry () {
    PEER=$1
	setGlobals $PEER

	peer channel join -b $CHANNEL_NAME.block  >&log.txt
	res=$?
	cat log.txt
	if [ $res -ne 0 -a $COUNTER -lt $MAX_RETRY ]; then
		COUNTER=` expr $COUNTER + 1`
		echo "${PEER} failed to join the channel, Retry after 2 seconds"
		sleep 2
		joinChannelWithRetry $1
	else
		COUNTER=1
	fi
	verifyResult $res "After $MAX_RETRY attempts, ${PEER} has failed to join channel '$CHANNEL_NAME' "
}

joinChannel () {
    for i in peer0.huasl peer0.skypine peer0.xinning peer0.usoft ;
    do
        joinChannelWithRetry $i
        echo "===================== $i joined channel '$CHANNEL_NAME' ===================== "
        sleep 2
        echo
    done
}

installChaincode () {
	PEER=$1
	setGlobals $PEER
	peer chaincode install -n stockcontract -v 1.0 -p github.com/usoftchina/usoftchain-sample/chaincode/stock >&log.txt
	res=$?
	cat log.txt
	verifyResult $res "Chaincode installation on ${PEER} has Failed"
	echo "===================== Chaincode is installed on ${PEER} ===================== "
	echo
}

instantiateChaincode () {
	PEER=$1
	setGlobals $PEER
	# while 'peer chaincode' command can get the orderer endpoint from the peer (if join was successful),
	# lets supply it directly as we know it using the "-o" option
	if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
		peer chaincode instantiate -o orderer.usoftchain.com:7050 -C $CHANNEL_NAME -n stockcontract -v 1.0 -c '{"Args":["init"]}' -P "OR ('huaslMSP.member','skypineMSP.member','xinningMSP.member','usoftMSP.member')" >&log.txt
	else
		peer chaincode instantiate -o orderer.usoftchain.com:7050 --tls --cafile $ORDERER_CA -C $CHANNEL_NAME -n stockcontract -v 1.0 -c '{"Args":["init"]}' -P "OR ('huaslMSP.member','skypineMSP.member','xinningMSP.member','usoftMSP.member')" >&log.txt
	fi
	res=$?
	cat log.txt
	verifyResult $res "Chaincode instantiation on ${PEER} on channel '$CHANNEL_NAME' failed"
	echo "===================== Chaincode is instantiated on ${PEER} on channel '$CHANNEL_NAME' ===================== "
	sleep 2
	peer chaincode list -C $CHANNEL_NAME --instantiated >&log.txt
	cat log.txt
	echo
}

# Create channel
echo "Creating channel..."
createChannel

# Join all the peers to the channel
echo "Having all peers join the channel..."
joinChannel

#Install chaincode
#echo "Installing chaincode on peer0.huasl..."
#installChaincode peer0.huasl
#echo "Install chaincode on peer0.skypine..."
#installChaincode peer0.skypine
echo "Installing chaincode on peer0.xinning..."
installChaincode peer0.xinning
#echo "Installing chaincode on peer0.usoft..."
#installChaincode peer0.usoft

#Instantiate chaincode
#echo "Instantiating chaincode on peer0.huasl..."
#instantiateChaincode peer0.huasl
echo "Instantiating chaincode on peer0.xinning..."
instantiateChaincode peer0.xinning

echo
echo "===================== completed ===================== "
echo

exit 0