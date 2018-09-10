#!/bin/bash

echo
echo "Build your first network (BYFN) end-to-end test"
echo
CHANNEL_NAME="$1"
DELAY="$2"
LANGUAGE="$3"
TIMEOUT="$4"
VERBOSE="$5"
: ${CHANNEL_NAME:="mychannel"}
: ${DELAY:="3"}
: ${LANGUAGE:="golang"}
: ${TIMEOUT:="10"}
: ${VERBOSE:="false"}
LANGUAGE=`echo "$LANGUAGE" | tr [:upper:] [:lower:]`
COUNTER=1
MAX_RETRY=5

CC_SRC_PATH="github.com/usoftchina/usoftchain-sample/chaincode/stock"

echo "Channel name : "$CHANNEL_NAME

# import utils
. scripts/utils.sh

createChannel() {
	setGlobals 0 1

	if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
                set -x
		peer channel create -o orderer.example.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx >&log.txt
		res=$?
                set +x
	else
				set -x
		peer channel create -o orderer.example.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA >&log.txt
		res=$?
				set +x
	fi
	cat log.txt
	verifyResult $res "Channel creation failed"
	echo "===================== Channel '$CHANNEL_NAME' created ===================== "
	echo
}

joinChannel () {
	for org in 1 2; do
	    for peer in 0 1; do
		joinChannelWithRetry $peer $org
		echo "===================== peer${peer}.org${org} joined channel '$CHANNEL_NAME' ===================== "
		sleep $DELAY
		echo
	    done
	done
}

installChaincode() {
  PEER=$1
  ORG=$2
  setGlobals $PEER $ORG
  VERSION=${3:-1.0}
  set -x
  peer chaincode install -n stockcontract -v ${VERSION} -l ${LANGUAGE} -p ${CC_SRC_PATH} >&log.txt
  res=$?
  set +x
  cat log.txt
  verifyResult $res "Chaincode installation on peer${PEER}.org${ORG} has failed"
  echo "===================== Chaincode is installed on peer${PEER}.org${ORG} ===================== "
  echo
}

instantiateChaincode() {
  PEER=$1
  ORG=$2
  setGlobals $PEER $ORG
  VERSION=${3:-1.0}

  # while 'peer chaincode' command can get the orderer endpoint from the peer
  # (if join was successful), let's supply it directly as we know it using
  # the "-o" option
  if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
    set -x
    peer chaincode instantiate -o orderer.example.com:7050 -C $CHANNEL_NAME -n stockcontract -l ${LANGUAGE} -v ${VERSION} -c '{"Args":["init"]}' -P "AND ('Org1MSP.peer','Org2MSP.peer')" >&log.txt
    res=$?
    set +x
  else
    set -x
    peer chaincode instantiate -o orderer.example.com:7050 --tls $CORE_PEER_TLS_ENABLED --cafile $ORDERER_CA -C $CHANNEL_NAME -n stockcontract -l ${LANGUAGE} -v 1.0 -c '{"Args":["init"]}' -P "AND ('Org1MSP.peer','Org2MSP.peer')" >&log.txt
    res=$?
    set +x
  fi
  cat log.txt
  verifyResult $res "Chaincode instantiation on peer${PEER}.org${ORG} on channel '$CHANNEL_NAME' failed"
  echo "===================== Chaincode is instantiated on peer${PEER}.org${ORG} on channel '$CHANNEL_NAME' ===================== "
  echo
}

# parsePeerConnectionParameters $@
# Helper function that takes the parameters from a chaincode operation
# (e.g. invoke, query, instantiate) and checks for an even number of
# peers and associated org, then sets $PEER_CONN_PARMS and $PEERS
parsePeerConnectionParameters() {
        # check for uneven number of peer and org parameters
        if [ $(( $# % 2 )) -ne 0 ]; then
                exit 1
        fi

        PEER_CONN_PARMS=""
        PEERS=""
        while [ "$#" -gt 0 ]; do
                PEER="peer$1.org$2"
                PEERS="$PEERS $PEER"
                PEER_CONN_PARMS="$PEER_CONN_PARMS --peerAddresses $PEER.example.com:7051"
                if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "true" ]; then
                        TLSINFO=$(eval echo "--tlsRootCertFiles \$PEER$1_ORG$2_CA")
                        PEER_CONN_PARMS="$PEER_CONN_PARMS $TLSINFO"
                fi
                # shift by two to get the next pair of peer/org parameters
                shift; shift
        done
        # remove leading space for output
        PEERS="$(echo -e "$PEERS" | sed -e 's/^[[:space:]]*//')"
}

# chaincodeInvoke <peer> <org> ...
# Accepts as many peer/org pairs as desired and requests endorsement from each
chaincodeInvoke () {
        parsePeerConnectionParameters $@

        # while 'peer chaincode' command can get the orderer endpoint from the
        # peer (if join was successful), let's supply it directly as we know
        # it using the "-o" option
        echo $PEER_CONN_PARMS
        if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
            peer chaincode invoke -o orderer.example.com:7050 -C $CHANNEL_NAME -n stockcontract $PEER_CONN_PARMS -c '{"Args":["createAccount","91440300319521190W","深圳市华商龙商务互联科技有限公司"]}' >&log.txt
        else
            peer chaincode invoke -o orderer.example.com:7050  --tls --cafile $ORDERER_CA -C $CHANNEL_NAME -n stockcontract $PEER_CONN_PARMS -c '{"Args":["createAccount","91440300319521190W","深圳市华商龙商务互联科技有限公司"]}' >&log.txt
        fi
        cat log.txt
        echo "===================== Invoke transaction successful on $PEERS on channel '$CHANNEL_NAME' ===================== "
        echo
}

## Create channel
echo "Creating channel..."
createChannel

## Join all the peers to the channel
echo "Having all peers join the channel..."
joinChannel

## Set the anchor peers for each org in the channel
echo "Updating anchor peers for org1..."
updateAnchorPeers 0 1
echo "Updating anchor peers for org2..."
updateAnchorPeers 0 2

## Install chaincode
echo "Installing chaincode on peer0.org1..."
installChaincode 0 1
echo "Install chaincode on peer1.org1..."
installChaincode 1 1
echo "Install chaincode on peer0.org2..."
installChaincode 0 2
## Install chaincode on peer1.org2
echo "Installing chaincode on peer1.org2..."
installChaincode 1 2

# Instantiate chaincode on peer0.org2
echo "Instantiating chaincode on peer0.org2..."
instantiateChaincode 0 2

# Invoke on chaincode on peer0.org1 and peer1.org1
echo "Sending invoke transaction on peer0.org1 and peer1.org1..."
chaincodeInvoke 0 1 1 1

echo
echo "========= Completed =========== "
echo

exit 0
