# Bezant Chaincode Test Network

## How to start network

1.make folder under ./chaincode (folder name is the same as chaincode name)

2.copy chaincode files under the folder.

[example]

``` 
mkdir ./chaincode/sample-cc
cp YOUR_CHAINCODE_FILES ./chaincode/sample-cc

```

[folder structure example]
```
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

```
java chaincode : src, build.gradle(pom.xml)
node chaincode : *.js, package.json
go chaincode : all files
```

3.excute start.sh
```
./start.sh
```

4.input required parameters

[example]
```
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


## How to invoke, query chaincode


1.invoke chaincode

```
docker exec cli peer chaincode invoke -C bezant-channel -n CHAINCODE_NAME -c '{"Args":["FUNCTION_NAME","REQUIRED_ARG"...]}'
```

2.query chaincode

```
docker exec cli peer chaincode query -C bezant-channel -n CHAINCODE_NAME -c '{"Args":["FUNCTION_NAME","REQUIRED_ARG"...]}'
```

[example]
```
docker exec cli peer chaincode invoke -C bezant-channel -n sample-cc -c '{"Args":["transfer", "a", "b", "10"]}'

docker exec cli peer chainocde query -C bezant-channel -n sample-cc -c '{"Args":["get","a"]}'
```


3.get block height

```
docker exec cli peer channel getinfo -c bezant-channel
```


## Stop network

excute stop.sh

```
./stop.sh
```










