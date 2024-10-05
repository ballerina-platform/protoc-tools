import ballerina/io;

TestServiceClient ep = check new ("http://localhost:9090");

public function main() returns error? {
    RequestMessage testRPCRequest = {req: [{complex_value: {foo: 1, bar: "ENTRY_ONE"}, none: {}}]};
    ResponseMessage testRPCResponse = check ep->TestRPC(testRPCRequest);
    io:println(testRPCResponse);
}

