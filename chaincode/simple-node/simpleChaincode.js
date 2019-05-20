const shim = require('fabric-shim');
const chaincodeUtil = require('./chaincodeUtil');

const SimpleChaincode = class {
    async Init(stub) {
        console.info('========= Init =========');
        return shim.success();
    }

    async Invoke(stub) {
        console.info('========= Invoke =========');
        const ret = stub.getFunctionAndParameters();
        const func = this[ret.fcn];
        if (!func) {
            return shim.error('No function name :' + ret.fcn + ' found');
        }
        try {
            return await func(stub, ret.params);
        } catch (err) {
            return shim.error(err);
        }
    }

    async put(stub, args) {
        if (args.length !== 2) {
            return shim.error('Incorrect number of arguments. Expecting 2');
        }

        const key = args[0],
            value = args[1];

        await stub.putState(key, Buffer.from(value));
        return shim.success();
    }

    async get(stub, args) {
        if (args.length !== 1) {
            return shim.error('Incorrect number of arguments. Expecting 1');
        }

        const resultValueBytes = await stub.getState(args[0]);

        if (resultValueBytes.length === 0) {
            return shim.error('Failed to get state for ' + args[0]);
        }

        return shim.success(resultValueBytes);
    }

    async putAndGetEnrollmentId(stub, args) {
        if (args.length !== 2) {
            return shim.error('Incorrect number of arguments. Expecting 2');
        }

        const key = args[0],
            value = args[1];

        await stub.putState(key, Buffer.from(value));

        const enrollmentId = chaincodeUtil.getEnrollmentId(stub);
        return shim.success(Buffer.from(enrollmentId));
    }
};

shim.start(new SimpleChaincode());