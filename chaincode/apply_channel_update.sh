APP_PATH=$1
CHANNEL_NAME=$2

configtxlator proto_encode --input ${APP_PATH}/files/original_config.json --type common.Config --output ${APP_PATH}/files/original_config.pb
configtxlator proto_encode --input ${APP_PATH}/files/modified_config.json --type common.Config --output ${APP_PATH}/files/modified_config.pb

configtxlator compute_update --channel_id ${CHANNEL_NAME} --original ${APP_PATH}/files/original_config.pb --updated ${APP_PATH}/files/modified_config.pb --output ${APP_PATH}/files/update.pb
configtxlator proto_decode --input ${APP_PATH}/files/update.pb --type common.ConfigUpdate | jq . > ${APP_PATH}/files/update.json

echo '{"payload":{"header":{"channel_header":{"channel_id":"'${CHANNEL_NAME}'", "type":2}},"data":{"config_update":'$(cat /${APP_PATH}/files/update.json)'}}}' | jq . > ${APP_PATH}/files/update_envelope.json
configtxlator proto_encode --input ${APP_PATH}/files/update_envelope.json --type common.Envelope --output ${APP_PATH}/files/update_envelope.pb

peer channel signconfigtx -f ${APP_PATH}/files/update_envelope.pb
peer channel update -f ${APP_PATH}/files/update_envelope.pb -c ${CHANNEL_NAME} -o orderer.example.com:7050
