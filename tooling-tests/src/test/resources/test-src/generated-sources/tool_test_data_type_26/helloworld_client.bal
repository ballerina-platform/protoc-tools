import ballerina/io;

HelloWorldClient ep = check new ("http://localhost:9090");

public function main() returns error? {
    Hello helloRequest = {name: "ballerina", location: "A"};
    Hello helloResponse = check ep->hello(helloRequest);
    io:println(helloResponse);
}

