import ballerina/grpc;

listener grpc:Listener ep = new (9090);

@grpc:Descriptor {value: EMPTYFIELDMESSAGE_DESC}
service "TestService" on ep {

    remote function TestRPC(RequestMessage value) returns ResponseMessage|error {
    }
}

