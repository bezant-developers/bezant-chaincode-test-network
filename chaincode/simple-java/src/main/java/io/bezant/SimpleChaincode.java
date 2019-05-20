package io.bezant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SimpleChaincode extends ChaincodeBase {
    private static final Log log = LogFactory.getLog(SimpleChaincode.class);

    public static void main(String[] args) {
        new SimpleChaincode().start(args);
    }

    @Override
    public Response init(ChaincodeStub stub) {
        log.info("========= Init =========");
        return newSuccessResponse();
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        log.info("========= Invoke =========");
        try {
            String func = stub.getFunction();
            List<String> args = stub.getParameters();

            switch (func) {
                case "get" : return get(stub, args);
                case "put" : return put(stub, args);
                case "putAndGetEnrollmentId" : return putAndGetEnrollmentId(stub, args);
                default: return newErrorResponse("No function name :" + func + " found");
            }
        }
        catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    private Response put(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2");
        }

        stub.putStringState(args.get(0), args.get(1));

        return newSuccessResponse();
    }

    private Response get(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }

        byte[] resultValueBytes = stub.getState(args.get(0));

        if (resultValueBytes.length == 0) {
            return newErrorResponse("Failed to get state for " + args.get(0));
        }

        return newSuccessResponse(resultValueBytes);
    }

    private Response putAndGetEnrollmentId(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2");
        }

        stub.putStringState(args.get(0), args.get(1));

        String enrollmentId = ChaincodeUtil.getEnrollmentID(stub);
        return newSuccessResponse(enrollmentId.getBytes(UTF_8));
    }
}