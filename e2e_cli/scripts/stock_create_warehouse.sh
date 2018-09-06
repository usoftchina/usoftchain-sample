#!/bin/bash
# Copyright London Stock Exchange Group All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#
echo
echo " ____    _____      _      ____    _____           _____   ____    _____ "
echo "/ ___|  |_   _|    / \    |  _ \  |_   _|         | ____| |___ \  | ____|"
echo "\___ \    | |     / _ \   | |_) |   | |    _____  |  _|     __) | |  _|  "
echo " ___) |   | |    / ___ \  |  _ <    | |   |_____| | |___   / __/  | |___ "
echo "|____/    |_|   /_/   \_\ |_| \_\   |_|           |_____| |_____| |_____|"
echo

CHANNEL_NAME="$1"
: ${CHANNEL_NAME:="mychannel"}
: ${TIMEOUT:="60"}
COUNTER=1
MAX_RETRY=5
ORDERER_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem
PEER0_ORG1_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
PEER1_ORG1_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/ca.crt
PEER0_ORG2_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
PEER1_ORG2_CA=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer1.org2.example.com/tls/ca.crt
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
        ORG=$2
        if [ $ORG -eq 1 ] ; then
                CORE_PEER_LOCALMSPID="Org1MSP"
                CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG1_CA
                CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
                if [ $PEER -eq 0 ]; then
                        CORE_PEER_ADDRESS=peer0.org1.example.com:7051
                else
                        CORE_PEER_ADDRESS=peer1.org1.example.com:7051
                fi
        elif [ $ORG -eq 3 ] ; then
                CORE_PEER_LOCALMSPID="Org3MSP"
                CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
                CORE_PEER_ADDRESS=peer0.org1.example.com:7051
                CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/idemix/idemix-config
                CORE_PEER_LOCALMSPTYPE=idemix
        else
                CORE_PEER_LOCALMSPID="Org2MSP"
                CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG2_CA
                CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
                if [ $PEER -eq 0 ]; then
                        CORE_PEER_ADDRESS=peer0.org2.example.com:7051
                else
                        CORE_PEER_ADDRESS=peer1.org2.example.com:7051
                fi
        fi

        env |grep CORE
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
        res=$?
        verifyResult $res "Invoke transaction failed on channel '$CHANNEL_NAME' due to uneven number of peer and org parameters "

        # while 'peer chaincode' command can get the orderer endpoint from the
        # peer (if join was successful), let's supply it directly as we know
        # it using the "-o" option
        echo $PEER_CONN_PARMS
        if [ -z "$CORE_PEER_TLS_ENABLED" -o "$CORE_PEER_TLS_ENABLED" = "false" ]; then
            peer chaincode invoke -o orderer.example.com:7050 -C $CHANNEL_NAME -n stockcontract $PEER_CONN_PARMS -c '{"Args":["createWarehouse","91440300778798789B", "新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号"]}' >&log.txt
        else
            peer chaincode invoke -o orderer.example.com:7050  --tls --cafile $ORDERER_CA -C $CHANNEL_NAME -n stockcontract $PEER_CONN_PARMS -c '{"Args":["createWarehouse","91440300778798789B", "新宁", "新宁仓", "广东省东莞市长安镇乌沙第六工业区海滨路23号"]}' >&log.txt
        fi
        res=$?
        cat log.txt
        verifyResult $res "Invoke execution on PEER$PEER failed "
        echo "===================== Invoke transaction successful on $PEERS on channel '$CHANNEL_NAME' ===================== "
        echo
}

chaincodeInvoke 0 1 1 1

echo
echo "===================== Completed ===================== "
echo

exit 0