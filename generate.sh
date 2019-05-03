#!/bin/sh
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
export PATH=${PWD}/bin:$PATH
export FABRIC_CFG_PATH=${PWD}
CHANNEL_NAME=${1}

echo $CHANNEL_NAME

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
configtxgen -profile OrgsOrdererGenesis -outputBlock ./config/genesis.block -channelID first-channel
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
fi

# generate Public channel configuration transaction
configtxgen -profile Channels -outputCreateChannelTx ./config/${CHANNEL_NAME}.tx -channelID $CHANNEL_NAME
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi


# generate anchor peer transaction
configtxgen -profile Channels -outputAnchorPeersUpdate ./config/BezantMSPanchors.tx -channelID $CHANNEL_NAME -asOrg BezantOrg
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for BezantOrg..."
  exit 1
fi