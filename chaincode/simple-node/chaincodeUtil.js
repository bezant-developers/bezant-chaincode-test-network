const shim = require('fabric-shim');

exports.getEnrollmentId = (stub) => {
    const cid = new shim.ClientIdentity(stub);
    const x509 = cid.getX509Certificate();
    return x509.subject.commonName;
};