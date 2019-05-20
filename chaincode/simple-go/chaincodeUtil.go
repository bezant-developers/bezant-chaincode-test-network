package main

import (
	"github.com/hyperledger/fabric/core/chaincode/lib/cid"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

func getEnrollmentId(stub shim.ChaincodeStubInterface) (string, error) {
	cert, err := cid.GetX509Certificate(stub)

	if err != nil {
		return "", err
	}

	return cert.Subject.CommonName, err
}
