import ballerina/io;

helloWorldClient ep = check new ("http://localhost:9090");

public function main() returns error? {
    Message hello1Request = {msg: "ballerina", 'enum: "a"};
    Message hello1Response = check ep->hello1(hello1Request);
    io:println(hello1Response);

    Message hello2Request = {msg: "ballerina", 'enum: "a"};
    stream<Message, error?> hello2Response = check ep->hello2(hello2Request);
    check hello2Response.forEach(function(Message value) {
        io:println(value);
    });

    Message hello3Request = {msg: "ballerina", 'enum: "a"};
    Hello3StreamingClient hello3StreamingClient = check ep->hello3();
    check hello3StreamingClient->sendMessage(hello3Request);
    check hello3StreamingClient->complete();
    Message? hello3Response = check hello3StreamingClient->receiveMessage();
    io:println(hello3Response);

    Message hello4Request = {msg: "ballerina", 'enum: "a"};
    Hello4StreamingClient hello4StreamingClient = check ep->hello4();
    check hello4StreamingClient->sendMessage(hello4Request);
    check hello4StreamingClient->complete();
    Message? hello4Response = check hello4StreamingClient->receiveMessage();
    io:println(hello4Response);
}

