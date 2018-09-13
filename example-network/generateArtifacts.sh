#!/bin/bash +x
#
# Copyright IBM Corp. All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#


#set -e

CHANNEL_NAME=$1
: ${CHANNEL_NAME:="mychannel"}
echo $CHANNEL_NAME

export FABRIC_ROOT=$PWD/../../../hyperledger/fabric
export FABRIC_CFG_PATH=$PWD
echo

OS_ARCH=$(echo "$(uname -s|tr '[:upper:]' '[:lower:]'|sed 's/mingw64_nt.*/windows/')-$(uname -m | sed 's/x86_64/amd64/g')" | awk '{print tolower($0)}')

## Generates Org certs using cryptogen tool
function generateCerts (){
	CRYPTOGEN=$FABRIC_ROOT/release/$OS_ARCH/bin/cryptogen

	if [ -f "$CRYPTOGEN" ]; then
            echo "Using cryptogen -> $CRYPTOGEN"
	else
	    echo "Building cryptogen"
	    make -C $FABRIC_ROOT release
	fi

	echo
	echo "##########################################################"
	echo "##### Generate certificates using cryptogen tool #########"
	echo "##########################################################"
	$CRYPTOGEN generate --config=./crypto-config.yaml
	echo
}

function generateIdemixMaterial (){
	IDEMIXGEN=$FABRIC_ROOT/release/$OS_ARCH/bin/idemixgen
	CURDIR=`pwd`
	IDEMIXMATDIR=$CURDIR/crypto-config/idemix

	if [ -f "$IDEMIXGEN" ]; then
            echo "Using idemixgen -> $IDEMIXGEN"
	else
	    echo "Building idemixgen"
	    make -C $FABRIC_ROOT release
	fi

	echo
	echo "####################################################################"
	echo "##### Generate idemix crypto material using idemixgen tool #########"
	echo "####################################################################"

	mkdir -p $IDEMIXMATDIR
	cd $IDEMIXMATDIR

	# Generate the idemix issuer keys
	$IDEMIXGEN ca-keygen

	# Generate the idemix signer keys
	$IDEMIXGEN signerconfig -u OU1 -e OU1 -r 1

	cd $CURDIR
}

## Generate orderer genesis block , channel configuration transaction and anchor peer update transactions
function generateChannelArtifacts() {

	CONFIGTXGEN=$FABRIC_ROOT/release/$OS_ARCH/bin/configtxgen
	if [ -f "$CONFIGTXGEN" ]; then
            echo "Using configtxgen -> $CONFIGTXGEN"
	else
	    echo "Building configtxgen"
	    make -C $FABRIC_ROOT release
	fi

	echo "##########################################################"
	echo "#########  Generating Orderer Genesis block ##############"
	echo "##########################################################"
	# Note: For some unknown reason (at least for now) the block file can't be
	# named orderer.genesis.block or the orderer will fail to launch!
	$CONFIGTXGEN -profile UsoftchainOrdererGenesis -outputBlock ./channel-artifacts/genesis.block

	echo
	echo "#################################################################"
	echo "### Generating channel configuration transaction 'channel.tx' ###"
	echo "#################################################################"
	$CONFIGTXGEN -profile UsoftchainChannel -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID $CHANNEL_NAME

	echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for huaslMSP   ##########"
	echo "#################################################################"
	$CONFIGTXGEN -profile UsoftchainChannel -outputAnchorPeersUpdate ./channel-artifacts/huaslMSPanchors.tx -channelID $CHANNEL_NAME -asOrg huaslMSP

	echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for skypineMSP   ##########"
	echo "#################################################################"
	$CONFIGTXGEN -profile UsoftchainChannel -outputAnchorPeersUpdate ./channel-artifacts/skypineMSPanchors.tx -channelID $CHANNEL_NAME -asOrg skypineMSP
	echo

    echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for xinningMSP   ##########"
	echo "#################################################################"
	$CONFIGTXGEN -profile UsoftchainChannel -outputAnchorPeersUpdate ./channel-artifacts/xinningMSPanchors.tx -channelID $CHANNEL_NAME -asOrg xinningMSP
	echo

    echo
	echo "#################################################################"
	echo "#######    Generating anchor peer update for usoftMSP   ##########"
	echo "#################################################################"
	$CONFIGTXGEN -profile UsoftchainChannel -outputAnchorPeersUpdate ./channel-artifacts/usoftMSPanchors.tx -channelID $CHANNEL_NAME -asOrg usoftMSP
	echo
}

generateCerts
generateIdemixMaterial
generateChannelArtifacts
