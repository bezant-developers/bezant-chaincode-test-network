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

[Fabric binaries]
excute below command under `/Users/USER_NAME (for windows C:/Users/USER_NAME)`
```bash
curl -sSL http://bit.ly/2ysbOFE | bash -s -- 1.4.1 1.4.1 0.4.15
```

[example]
```bash
 on my local, fabric-samples files is downloaded under `/Users/philip/fabric-samples`
```

For more detail, visit https://hyperledger-fabric.readthedocs.io/en/release-1.4/getting_started.html


* * *

## How to start network

1.make folder under ./chaincode (folder name is the same as chaincode name)

2.copy chaincode files under the folder.

[example]

```bash
mkdir ./chaincode/sample-cc
cp YOUR_CHAINCODE_FILES ./chaincode/sample-cc

```

[folder structure example]
```bash
sample-cc
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
Please input chaincode name : sample-cc
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
docker exec cli peer chaincode invoke -C bezant-channel -n sample-cc -c '{"Args":["transfer", "a", "b", "10"]}'

docker exec cli peer chainocde query -C bezant-channel -n sample-cc -c '{"Args":["get","a"]}'
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










