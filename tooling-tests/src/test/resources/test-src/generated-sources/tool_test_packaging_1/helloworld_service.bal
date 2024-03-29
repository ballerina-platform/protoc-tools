import ballerina/grpc;

listener grpc:Listener ep = new (9090);

@grpc:Descriptor {value: SIMPLEPACKAGE_DESC}
service "helloWorld" on ep {

    remote function hello(boolean value) returns boolean|error {
    }
    remote function hi(HelloRequest value) returns HelloResponse|error {
    }
}

