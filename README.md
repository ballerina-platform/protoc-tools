Ballerina Protoc-Tools
===================

  [![Build](https://github.com/ballerina-platform/protoc-tools/actions/workflows/build-timestamped-master.yml/badge.svg)](https://github.com/ballerina-platform/protoc-tools/actions/workflows/build-timestamped-master.yml)
  [![Trivy](https://github.com/ballerina-platform/protoc-tools/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/protoc-tools/actions/workflows/trivy-scan.yml)
  [![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/protoc-tools.svg)](https://github.com/ballerina-platform/protoc-tools/commits/master)
  [![codecov](https://codecov.io/gh/ballerina-platform/protoc-tools/branch/master/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/protoc-tools) 

Protoc-tools provides a CLI command to convert a protoc definition to a Ballerina representation that can be then used for connecting and interacting with gRPC endpoints.

gRPC is an inter-process communication technology that allows you to connect, invoke, and operate distributed, heterogeneous applications as easily as making a local function call. The gRPC protocol is layered over HTTP/2 and uses Protocol Buffers for marshaling/unmarshaling messages. This makes gRPC highly efficient on wire and a simple service definition framework.

When you develop a gRPC application, the first thing you do is define a service definition using Protocol Buffers.

### Protocol buffers
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

For information on how to generate Ballerina code for Protocol Buffers definition, see [Write a gRPC service with Ballerina](https://ballerina.io/learn/write-a-grpc-service-with-ballerina/).

## Build from the source

### Set Up the prerequisites

* Download and install Java SE Development Kit (JDK) version 11 (from one of the following locations).

   * [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

   * [OpenJDK](https://adoptopenjdk.net/)

        > **Note:** Set the JAVA_HOME environment variable to the path name of the directory into which you installed JDK.

### Build the source

Execute the commands below to build from source.

1. To build the library:
        
        ./gradlew clean build

2. To run the integration tests:

        ./gradlew clean test

3. To build the module without the tests:

        ./gradlew clean build -x test

5. To debug the Ballerina implementation against the native implementation:
   ```
   ./gradlew clean build -Pdebug=<port>
   ./gradlew clean test -Pdebug=<port>
   ```

6. To debug the Ballerina implementation against the Ballerina language:
   ```
   ./gradlew clean build -PbalJavaDebug=<port>
   ./gradlew clean test -PbalJavaDebug=<port>
   ```

7. Publish the generated artifacts to the local Ballerina central repository:
    ```
    ./gradlew clean build -PpublishToLocalCentral=true
    ```
8. Publish the generated artifacts to the Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToCentral=true
   ```

## Contribute to Ballerina

As an open source project, Ballerina welcomes contributions from the community. 

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

* Chat live with us via our [Slack channel](https://ballerina.io/community/slack/).
* Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
* View the [Ballerina performance test results](https://github.com/ballerina-platform/ballerina-lang/blob/master/performance/benchmarks/summary.md).
* For example demonstrations of the usage, go to [Ballerina By Examples](https://ballerina.io/learn/by-example/).
