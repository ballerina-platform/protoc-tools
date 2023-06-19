import ballerina/grpc;

listener grpc:Listener ep = new (9090);

@grpc:Descriptor {value: DEPENDINGSERVICE_DESC, descMap: DEPENDINGSERVICE_DESCRIPTOR_MAP}
service "helloWorld" on ep {

    remote function hello(ReqMessage value) returns stream<ResMessage, error?>|error {
    }
}

