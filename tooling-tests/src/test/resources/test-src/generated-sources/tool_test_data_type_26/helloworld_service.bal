import ballerina/grpc;

listener grpc:Listener ep = new (9090);

@grpc:Descriptor {value: CHILD_DESC, descMap: descriptorMap}
service "HelloWorld" on ep {

    remote function hello(Hello value) returns Hello|error {
    }
}

