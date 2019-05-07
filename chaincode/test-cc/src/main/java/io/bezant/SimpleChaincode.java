package io.bezant;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SimpleChaincode extends ChaincodeBase {
    private static final Log log = LogFactory.getLog(SimpleChaincode.class);

    public static void main(String[] args) {
        new SimpleChaincode().start(args);
    }

    @Override
    public Response init(ChaincodeStub stub) {
        log.info("========= Init =========");
        stub.putStringState("a", "100");
        stub.putStringState("b", "1000");
        stub.putStringState("c", "200");

        return newSuccessResponse();
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        log.info("========= Invoke =========");
        try {
            String func = stub.getFunction();
            List<String> args = stub.getParameters();

            if ("get".equals(func)) {
                return get(stub, args);
            } else if ("put".equals(func)) {
                return put(stub, args);
            } else if ("history".equals(func)) {
                return history(stub, args);
            } else if ("del".equals(func)) {
                return del(stub, args);
            } else if ("compositePut".equals(func)) {
                return compositePut(stub, args);
            } else if ("rangeQuery".equals(func)) {
                return rangeQuery(stub, args);
            }

            return newErrorResponse(("No function name :" + func + " found"));
        }
        catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    private Response rangeQuery(ChaincodeStub stub, List<String> args) {
        return newSuccessResponse();
    }

    private Response compositePut(ChaincodeStub stub, List<String> args) {

        CompositeKey keys = stub.createCompositeKey("Trade", "1","2");
        System.out.println(keys.toString());
        stub.putStringState(keys.toString(), "aa");

        return newSuccessResponse();
    }

    private Response del(ChaincodeStub stub, List<String> args) {

        stub.delState(args.get(0));

        return newSuccessResponse();
    }

    private Response history(ChaincodeStub stub, List<String> args) throws IOException {

        QueryResultsIterator<KeyModification> results =  stub.getHistoryForKey(args.get(0));
        List<History> historyResult = new ArrayList<>();
        results.forEach(p -> {
            History h = new History();
            h.setTxId(p.getTxId());
            h.setValue(p.getStringValue());
            h.setTimeStamp(p.getTimestamp().toString());
            historyResult.add(h);
        });
        String json = new Gson().toJson(historyResult);
        return newSuccessResponse(json.getBytes());
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
}