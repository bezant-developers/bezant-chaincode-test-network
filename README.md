# Bezant Chaincode Test Network
This repository is for basic function testing of chaincode.

You should pre-check your chaincode before launch this. (eg. typo, runtime exception ...)

* * *

## Prerequisites
- Docker
- Fabric binaries

[Docker]
```bash
MacOSX, *nix, or Windows 10: Docker Docker version 17.06.2-ce or greater is required.

Older versions of Windows: Docker Toolbox - again, Docker version Docker 17.06.2-ce or greater is required.
```

For more detail, visit https://hyperledger-fabric.readthedocs.io/en/release-1.4/getting_started.html


* * *

## How to start network

1.make folder under ./chaincode (folder name is the same as chaincode name)

2.copy chaincode files under the folder.

[example]

```bash
mkdir ./chaincode/simple-java
cp YOUR_CHAINCODE_FILES ./chaincode/simple-java

```

[folder structure example]
```bash
simple-java
├── build.gradle
└── src
    └── main
        └── java
            └── io
                └── bezant
                    └── SimpleChaincode.java
```

[required chaincode files]

```bash
java chaincode : src, build.gradle(pom.xml)
node chaincode : *.js, package.json
go chaincode : all files
```

3.excute start.sh

```bash
./start.sh
```

4.input required parameters

[example]
```bash
Please input chaincode name : simple-java
Please input chaincode version (ex- 1.0) :  1.0
Please input chaincode language (ex- go, java, node) : java
```
when you see this log, the network is properly started
```
==================================
  Network started successfully
=================================
```

* * *

## How to invoke, query chaincode


1.invoke chaincode

```bash
docker exec cli peer chaincode invoke -C bezant-channel -n CHAINCODE_NAME -c '{"Args":["FUNCTION_NAME","REQUIRED_ARG"...]}'
```

2.query chaincode

```bash
docker exec cli peer chaincode query -C bezant-channel -n CHAINCODE_NAME -c '{"Args":["FUNCTION_NAME","REQUIRED_ARG"...]}'
```

[example]
```bash
docker exec cli peer chaincode invoke -C bezant-channel -n simple-java -c '{"Args":["put", "a", "10"]}'

docker exec cli peer chainocde query -C bezant-channel -n simple-java -c '{"Args":["get","a"]}'
```


3.get block height

```
docker exec cli peer channel getinfo -c bezant-channel
```


## Stop network

excute stop.sh

```bash
./stop.sh
```

## Extras
1.If You want to test other chaincode, excute start.sh first and repeat above steps.

2.If you want to put args when instantiating chaincode, edit start.sh and do above steps.

[example]

Before
```
docker exec cli peer chaincode instantiate -o orderer.example.com:7050 -C $CHANNEL_NAME -n ${CHAINCODE_NAME} -v ${VERSION} -c '{"Args":["init"]}'
```
After
```
docker exec cli peer chaincode instantiate -o orderer.example.com:7050 -C $CHANNEL_NAME -n ${CHAINCODE_NAME} -v ${VERSION} -c '{"Args":["init", "ARGS"...]}
```








