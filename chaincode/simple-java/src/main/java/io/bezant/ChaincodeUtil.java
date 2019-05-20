package io.bezant;

import com.google.common.base.Strings;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChaincodeUtil {
    private static final String PREFIX_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String CERT_PARSING_ERROR = "Certificate parsing error occured";
    private static final String CERT_INVOKER_INCORRECT_ERROR = "The information of the invoker's certificate is wrong";

    public static String getEnrollmentID(ChaincodeStub stub) {
        String creator = new String(stub.getCreator(), UTF_8);
        int startIndex = creator.indexOf(PREFIX_CERTIFICATE);
        String pem = creator.substring(startIndex);

        X509Certificate certificate;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(pem.getBytes(UTF_8)));
        } catch (CertificateException ce) {
            throw new RuntimeException(CERT_PARSING_ERROR);
        }

        String enrollmentID = certificate.getSubjectDN().getName().split(",")[0].split("CN=")[1];
        if (Strings.isNullOrEmpty(enrollmentID)) {
            throw new RuntimeException(CERT_INVOKER_INCORRECT_ERROR);
        }

        return enrollmentID;
    }
}