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
ORDERER_SYSCHAN_ID=e2e-orderer-syschan

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

checkOSNAvailability() {
	# Use orderer's MSP for fetching system channel config block
	CORE_PEER_LOCALMSPID="OrdererMSP"
	CORE_PEER_TLS_ROOTCERT_FILE=$ORDERER_CA
	CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/usoftchain.com/orderers/orderer.usoftchain.com/msp

	local rc=1
	local starttime=$(date +%s)

	# continue to poll
	# we either get a successful response, or reach TIMEOUT
	while test "$(($(date +%s)-starttime))" -lt "$TIMEOUT" -a $rc -ne 0
	do
		 sleep 3
		 echo "Attempting to fetch system channel '$ORDERER_SYSCHAN_ID' ...$(($(date +%s)-starttime)) secs"
		 if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
			 peer channel fetch 0 -o orderer.usoftchain.com:7050 -c "$ORDERER_SYSCHAN_ID" >&log.txt
		 else
			 peer channel fetch 0 0_block.pb -o orderer.usoftchain.com:7050 -c "$ORDERER_SYSCHAN_ID" --tls --cafile $ORDERER_CA >&log.txt
		 fi
		 test $? -eq 0 && VALUE=$(cat log.txt | awk '/Received block/ {print $NF}')
		 test "$VALUE" = "0" && let rc=0
	done
	cat log.txt
	verifyResult $rc "Ordering Service is not available, Please try again ..."
	echo "===================== Ordering Service is up and running ===================== "
	echo
}

createChannel() {
	setGlobals 0 1
	if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
		peer channel create -o orderer.usoftchain.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx >&log.txt
	else
		peer channel create -o orderer.usoftchain.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx --tls --cafile $ORDERER_CA >&log.txt
	fi
	res=$?
	cat log.txt
	verifyResult $res "Channel creation failed"
	echo "===================== Channel '$CHANNEL_NAME' created ===================== "
	echo
}

updateAnchorPeers() {
	setGlobals $1

	if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
		peer channel update -o orderer.usoftchain.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/${CORE_PEER_LOCALMSPID}anchors.tx >&log.txt
	else
		peer channel update -o orderer.usoftchain.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/${CORE_PEER_LOCALMSPID}anchors.tx --tls --cafile $ORDERER_CA >&log.txt
	fi
	res=$?
	cat log.txt
	verifyResult $res "Anchor peer update failed"
	echo "===================== Anchor peers updated for org '$CORE_PEER_LOCALMSPID' on channel '$CHANNEL_NAME' ===================== "
	sleep 5
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
		peer chaincode instantiate -o orderer.usoftchain.com:7050 -C $CHANNEL_NAME -n stockcontract -v 1.0 -c '{"Args":["init"]}' -P "AND ('huaslMSP.peer','skypineMSP.peer','xinningMSP.peer','usoftMSP.peer')" >&log.txt
	else
		peer chaincode instantiate -o orderer.usoftchain.com:7050 --tls --cafile $ORDERER_CA -C $CHANNEL_NAME -n stockcontract -v 1.0 -c '{"Args":["init"]}' -P "AND ('huaslMSP.peer','skypineMSP.peer','xinningMSP.peer','usoftMSP.peer')" >&log.txt
	fi
	res=$?
	cat log.txt
	verifyResult $res "Chaincode instantiation on ${PEER} on channel '$CHANNEL_NAME' failed"
	echo "===================== Chaincode is instantiated on ${PEER} on channel '$CHANNEL_NAME' ===================== "
	echo
}

# Check for orderering service availablility
echo "Check orderering service availability..."
checkOSNAvailability

# Create channel
echo "Creating channel..."
createChannel

# Join all the peers to the channel
echo "Having all peers join the channel..."
joinChannel

# Set the anchor peers for each org in the channel
echo "Updating anchor peers for huasl..."
updateAnchorPeers peer0.huasl
echo "Updating anchor peers for skypine..."
updateAnchorPeers peer0.skypine
echo "Updating anchor peers for xinning..."
updateAnchorPeers peer0.xinning
echo "Updating anchor peers for usoft..."
updateAnchorPeers peer0.usoft

#Install chaincode
echo "Installing chaincode on peer0.huasl..."
installChaincode peer0.huasl
echo "Install chaincode on peer0.skypine..."
installChaincode peer0.skypine
echo "Installing chaincode on peer0.xinning..."
installChaincode peer0.xinning
echo "Installing chaincode on peer0.usoft..."
installChaincode peer0.usoft

#Instantiate chaincode
echo "Instantiating chaincode on peer0.huasl..."
instantiateChaincode peer0.huasl

echo
echo "===================== completed ===================== "
echo

exit 0
