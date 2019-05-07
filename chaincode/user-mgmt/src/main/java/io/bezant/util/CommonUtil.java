package io.bezant.util;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.bezant.exception.CommonException;
import io.bezant.exception.ErrorMessage;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public class CommonUtil {
    private static final Gson gson = new Gson();

    public static <T> T convertJsonStringToObject(String jsonStr, Class<T> t) {
        if (Strings.isNullOrEmpty(jsonStr)) {
            return null;
        }

        return gson.fromJson(jsonStr, t);
    }

    public static String convertObjectToJsonString(Object object) {
        return gson.toJson(object);
    }

    public static byte[] convertObjectToJsonStringBytes(Object object) {
        return gson.toJson(object).getBytes();
    }

    public static List<String> checkAndGetParams(ChaincodeStub stub, int requiredNumber) {
        List<String> args = stub.getParameters();

        if (args.size() != requiredNumber) {
            throw new CommonException(String.format(ErrorMessage.INCORRECT_NUMBER_OF_ARGUMENTS, requiredNumber));
        }

        return args;
    }
}