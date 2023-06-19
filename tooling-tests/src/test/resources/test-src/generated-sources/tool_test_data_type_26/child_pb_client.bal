import ballerina/grpc;
import ballerina/protobuf;

public const string CHILD_DESC = "0A0B6368696C642E70726F746F120C656E756D5F696D706F7274731A0C706172656E742E70726F746F224F0A0548656C6C6F12120A046E616D6518012001280952046E616D6512320A086C6F636174696F6E18022001280E32162E656E756D5F696D706F7274732E4C6F636174696F6E52086C6F636174696F6E323F0A0A48656C6C6F576F726C6412310A0568656C6C6F12132E656E756D5F696D706F7274732E48656C6C6F1A132E656E756D5F696D706F7274732E48656C6C6F620670726F746F33";
public const map<string> descriptorMap = {"parent.proto": PARENT_DESC};

public isolated client class HelloWorldClient {
    *grpc:AbstractClientEndpoint;

    private final grpc:Client grpcClient;

    public isolated function init(string url, *grpc:ClientConfiguration config) returns grpc:Error? {
        self.grpcClient = check new (url, config);
        check self.grpcClient.initStub(self, CHILD_DESC, descriptorMap);
    }

    isolated remote function hello(Hello|ContextHello req) returns Hello|grpc:Error {
        map<string|string[]> headers = {};
        Hello message;
        if req is ContextHello {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("enum_imports.HelloWorld/hello", message, headers);
        [anydata, map<string|string[]>] [result, _] = payload;
        return <Hello>result;
    }

    isolated remote function helloContext(Hello|ContextHello req) returns ContextHello|grpc:Error {
        map<string|string[]> headers = {};
        Hello message;
        if req is ContextHello {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("enum_imports.HelloWorld/hello", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <Hello>result, headers: respHeaders};
    }
}

public type ContextHello record {|
    Hello content;
    map<string|string[]> headers;
|};

@protobuf:Descriptor {value: CHILD_DESC}
public type Hello record {|
    string name = "";
    Location location = A;
|};

