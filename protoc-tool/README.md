Ballerina Protoc-Tools
===================

Protoc-tools provides a CLI command to convert a protoc definition to a Ballerina representation that can be then used for connecting and interacting with gRPC endpoints.

gRPC is an inter-process communication technology that allows you to connect, invoke, and operate distributed, heterogeneous applications as easily as making a local function call. The gRPC protocol is layered over HTTP/2 and uses Protocol Buffers for marshaling/unmarshaling messages. This makes gRPC highly efficient on wire and a simple service definition framework.

When you develop a gRPC application, the first thing you do is define a service definition using Protocol Buffers.

## Protocol buffers

This is a mechanism to serialize the structured data introduced by Google and used by the gRPC framework. Defining the service using Protocol Buffers includes defining remote methods in the service and defining message types that are sent across the network. A sample service definition is shown below.

```proto
syntax = "proto3";

service Helloworld {
    rpc hello(HelloRequest) returns (HelloResponse);
}

message HelloRequest {
    string name = 1;
}

message HelloResponse {
    string message = 1;
}
```

gRPC allows client applications to directly call the server-side methods using the auto-generated stubs. Protocol
Buffer compiler is used to generate the stubs for the specified language. In Ballerina, the stubs are generated using the built-in 'Protocol Buffers to Ballerina' tool.
