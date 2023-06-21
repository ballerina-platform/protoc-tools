import ballerina/grpc;

listener grpc:Listener ep = new (9090);

@grpc:Descriptor {value: PACKAGEWITHENUMIMPORTS_DESC, descMap: PACKAGEWITHENUMIMPORTS_DESCRIPTOR_MAP}
service "helloWorld" on ep {

    remote function hello1(Message value) returns Message|error {
    }
    remote function hello3(stream<Message, grpc:Error?> clientStream) returns Message|error {
    }
    remote function hello2(Message value) returns stream<Message, error?>|error {
    }
    remote function hello4(stream<Message, grpc:Error?> clientStream) returns stream<Message, error?>|error {
    }
}

