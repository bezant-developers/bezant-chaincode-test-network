#!/bin/bash

export MSYS_NO_PATHCONV=1
CHANNEL_NAME=bezant-channel

read -p "Please input chaincode name : " CHAINCODE_NAME
read -p "Please input chaincode version (ex- 1.0) :  " VERSION
read -p "Please input chaincode language (ex- go, java, node) : " LANGUAGE

case "$LANGUAGE" in
"go")
  echo "chaincode language : go"
  ;;
"java")
  echo "chaincode language : java"
  ;;
"node")
  echo "chaincode language : node"
  ;;
*)
  echo "Invalid language (input should be go or java or node)"
  exit;
esac  

if [ ! -d "./chaincode/${CHAINCODE_NAME}" ]; then
  echo "=================================================="
  echo "chaincode file does not exist."
  echo "copy chaincode file to ./chaincode/CHAINCODE_NAME"
  echo "=================================================="
  exit;
fi

# generate mendatory files...
source ./generate.sh bezant-channel


echo "Clearing environment.."
docker rm -f $(docker ps -aq)

docker-compose -f docker-compose.yml down

echo "Removing old chaincode images.."
# docker rmi $(docker images | grep example.com-chaincode | tr -s ' ' | cut -d ' ' -f 3)
docker rmi $(docker images | grep dev-* | awk '{print $3}')

# Exit on first error
set -e


ARCH=$(uname -s | grep Darwin)
  if [ "$ARCH" == "Darwin" ]; then
    OPTS="-it"
  else
    OPTS="-i"
  fi

cp docker-compose-template.yml docker-compose.yml

CURRENT_DIR=$PWD
cd crypto-config/peerOrganizations/bezant.example.com/ca/
PRIV_KEY=$(ls *_sk)
cd "$CURRENT_DIR"
sed $OPTS "s/BEZANT_CA_PRIVATE_KEY/${PRIV_KEY}/g" docker-compose.yml
# If MacOSX, remove the temporary backup of the docker-compose file
if [ "$ARCH" == "Darwin" ]; then
    rm docker-compose.ymlt
fi

docker-compose -f docker-compose.yml up -d 
# wait for Hyperledger Fabric to start
# incase of errors when running later commands, issue export FABRIC_START_TIMEOUT=<larger number>
export FABRIC_START_TIMEOUT=10
#echo ${FABRIC_START_TIMEOUT}
sleep ${FABRIC_START_TIMEOUT}

# Create the Public channel
docker exec -e "CORE_PEER_LOCALMSPID=BezantMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@bezant.example.com/msp" peer0.bezant.example.com peer channel create -o orderer.example.com:7050 -c $CHANNEL_NAME -f /etc/hyperledger/configtx/${CHANNEL_NAME}.tx
docker cp peer0.bezant.example.com:/opt/gopath/src/github.com/hyperledger/fabric/bezant-channel.block ./config
# Join peer0.bezant.example.com to the Public channel.
docker exec -e "CORE_PEER_LOCALMSPID=BezantMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@bezant.example.com/msp" peer0.bezant.example.com peer channel join -b ${CHANNEL_NAME}.block
docker exec -e "CORE_PEER_LOCALMSPID=BezantMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@bezant.example.com/msp" peer1.bezant.example.com peer channel join -b /etc/hyperledger/configtx/${CHANNEL_NAME}.block
# Updating anchor peers for bezant
docker exec -e "CORE_PEER_LOCALMSPID=BezantMSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@bezant.example.com/msp" peer0.bezant.example.com peer channel update -o orderer.example.com:7050 -c $CHANNEL_NAME -f /etc/hyperledger/configtx/BezantMSPanchors.tx

if [ "$LANGUAGE" == "go" ]; then
  docker exec cli peer chaincode install -n ${CHAINCODE_NAME} -v ${VERSION} -p ${CHAINCODE_NAME}
  docker exec cli2 peer chaincode install -n ${CHAINCODE_NAME} -v ${VERSION} -p ${CHAINCODE_NAME}
elif [ "$LANGUAGE" == "java" ]; then
  docker exec cli peer chaincode install -n ${CHAINCODE_NAME} -v ${VERSION} -l ${LANGUAGE} -p /opt/gopath/src/${CHAINCODE_NAME}
  docker exec cli2 peer chaincode install -n ${CHAINCODE_NAME} -v ${VERSION} -l ${LANGUAGE} -p /opt/gopath/src/${CHAINCODE_NAME}
elif [ "$LANGUAGE" == "node" ]; then
  docker exec cli peer chaincode install -n ${CHAINCODE_NAME} -v ${VERSION} -l ${LANGUAGE} -p /opt/gopath/src/${CHAINCODE_NAME}
  docker exec cli2 peer chaincode install -n ${CHAINCODE_NAME} -v ${VERSION} -l ${LANGUAGE} -p /opt/gopath/src/${CHAINCODE_NAME}
fi


echo -e "\nIntantiating chaincode [${CHAINCODE_NAME}]..."

docker exec cli peer chaincode instantiate -o orderer.example.com:7050 -C $CHANNEL_NAME -n ${CHAINCODE_NAME} -v ${VERSION} -c '{"Args":["init"]}'

echo -e "\nchaincode [${CHAINCODE_NAME}] is instantiated"

sleep 1

echo -e "\n\n=================================="
echo "  Network started successfully"
echo "================================="





