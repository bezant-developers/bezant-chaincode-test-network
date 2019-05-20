# `Go Chaincode` simple code
This tutorial will explain how to write `Hyperledger Fabric` chain code based on `Go`

# Environment
+ `Go`
+ `Hyperledger Fabric`


# `Chaincode` development example
Writing your own chain code requires an understanding of the `Fabric` platform, `Go`. An application is a basic example chain code that creates assets (key-value pairs) on a ledger.

## Download code
```sh
$ git clone https://github.com/bezant-developers/bezant-chaincode-samples-go.git
```

## Basic code
```go
package main

import (
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type SimpleChaincode struct {
}

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
```

## Function
Init is called during chaincode instantiation to initialize and chaincode upgrade also calls this function.
```go
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("========= Init =========")
	return shim.Success(nil)
}
```

The Invoke method is called in response to receiving an invoke transaction to process transaction proposals.
```go
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("========= Invoke =========")
	function, args := stub.GetFunctionAndParameters()

	if function == "put" {
		return t.put(stub, args)
	} else if function == "get" {
		return t.get(stub, args)
	}

	return shim.Error("No function name : " + function + " found")
}
```

Save the keys and values to the ledger.
```go
func (t *SimpleChaincode) put(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error
	var keyString, valString string

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	keyString = args[0]
	valString = args[1]

	err = stub.PutState(keyString, []byte(valString))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}
```

Get returns the value of the specified asset key
```go
func (t *SimpleChaincode) get(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var keyString string
	var err error

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	keyString = args[0]

	// Get the state from the ledger
	resultValueBytes, err := stub.GetState(keyString)
	if err != nil {
		return shim.Error("Failed to get state for" + keyString)
	}

	if resultValueBytes == nil {
		return shim.Error("Failed to get state for" + keyString)
	}

	return shim.Success(resultValueBytes)
}
```

Start the chaincode process and listen for incoming endorsement requests
```go
func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
```

## Managing external dependencies for chaincode written in Go
If your chaincode requires packages not provided by the Go standard library, you will need to include those packages with your chaincode. It is also a good practice to add the shim and any extension libraries to your chaincode as a dependency.

There are [many tools available](https://github.com/golang/go/wiki/PackageManagementTools) for managing (or “vendoring”) these dependencies. The following demonstrates how to use govendor:

``` 
govendor init
govendor add +external  // Add all external package, or
govendor add github.com/external/pkg // Add specific external package
```
This imports the external dependencies into a local vendor directory. If you are vendoring the Fabric shim or shim extensions, clone the Fabric repository to your $GOPATH/src/github.com/hyperledger directory, before executing the govendor commands.

## Compress go files cli
``` console
zip -r chaincode.zip vendor simpleChaincode.go chaincodeUtil.go 
```

## Local environment test
[bezant-chaincode-test-network link](https://github.com/bezant-developers/bezant-chaincode-test-network)

``Put``
```bash
docker exec cli peer chaincode invoke -o orderer.example.com:7050 -C bezant-channel -n simple-go --peerAddresses peer0.bezant.example.com:7051 -c '{"Args":["put", "a", "10"]}'
```

``Get``
```bash
docker exec cli peer chaincode query -C bezant-channel -n simple-go --peerAddresses peer0.bezant.example.com:7051 -c '{"Args":["get", "a"]}'
```

``Put and get enrollmentId``
```bash
docker exec cli peer chaincode invoke -o orderer.example.com:7050 -C bezant-channel -n simple-go --peerAddresses peer0.bezant.example.com:7051 -c '{"Args":["putAndGetEnrollmentId", "a", "10"]}'
```

``Instantiate``
```bash
docker exec cli peer chaincode install -n simple-go -v 1.0 -p simple-go
docker exec cli2 peer chaincode install -n simple-go -v 1.0 -p simple-go                                                                                            
docker exec cli peer chaincode instantiate -o orderer.example.com:7050 -C bezant-channel -n simple-go -v 1.0 -c '{"Args":["init"]}'               
```

``Upgrade``
```bash
docker exec cli peer chaincode install -n simple-go -v 1.1 -p simple-go
docker exec cli2 peer chaincode install -n simple-go -v 1.1 -p simple-go                                                                                            
docker exec cli peer chaincode upgrade -o orderer.example.com:7050 -C bezant-channel -n simple-go -v 1.1 -c '{"Args":["init"]}'               
```