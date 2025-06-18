import ballerina/grpc;
import ballerina/protobuf;

public const string HELLOWORLDWITHMAP_DESC = "0A1768656C6C6F576F726C64576974684D61702E70726F746F2288010A0C48656C6C6F5265717565737412120A046E616D6518012001280952046E616D65122B0A047461677318042003280B32172E48656C6C6F526571756573742E54616773456E7472795204746167731A370A0954616773456E74727912100A036B657918012001280952036B657912140A0576616C7565180220012809520576616C75653A0238012290010A0D48656C6C6F526573706F6E736512180A076D65737361676518012001280952076D657373616765122C0A047461677318042003280B32182E48656C6C6F526573706F6E73652E54616773456E7472795204746167731A370A0954616773456E74727912100A036B657918012001280952036B657912140A0576616C7565180220012809520576616C75653A02380122390A154D61707065645065726D697373696F6E734C69737412200A0B7065726D697373696F6E73180220032809520B7065726D697373696F6E7322FB010A294765744D61707065645065726D697373696F6E734F665573657241744C6576656C526573706F6E736512700A126D61707065645F7065726D697373696F6E7318012003280B32412E4765744D61707065645065726D697373696F6E734F665573657241744C6576656C526573706F6E73652E4D61707065645065726D697373696F6E73456E74727952116D61707065645065726D697373696F6E731A5C0A164D61707065645065726D697373696F6E73456E74727912100A036B657918012001280952036B6579122C0A0576616C756518022001280B32162E4D61707065645065726D697373696F6E734C697374520576616C75653A02380122070A05456D70747932720A0A68656C6C6F576F726C6412260A0568656C6C6F120D2E48656C6C6F526571756573741A0E2E48656C6C6F526573706F6E7365123C0A064765744D617012062E456D7074791A2A2E4765744D61707065645065726D697373696F6E734F665573657241744C6576656C526573706F6E7365620670726F746F33";

public isolated client class helloWorldClient {
    *grpc:AbstractClientEndpoint;

    private final grpc:Client grpcClient;

    public isolated function init(string url, *grpc:ClientConfiguration config) returns grpc:Error? {
        self.grpcClient = check new (url, config);
        check self.grpcClient.initStub(self, HELLOWORLDWITHMAP_DESC);
    }

    isolated remote function hello(HelloRequest|ContextHelloRequest req) returns HelloResponse|grpc:Error {
        map<string|string[]> headers = {};
        HelloRequest message;
        if req is ContextHelloRequest {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("helloWorld/hello", message, headers);
        [anydata, map<string|string[]>] [result, _] = payload;
        return <HelloResponse>result;
    }

    isolated remote function helloContext(HelloRequest|ContextHelloRequest req) returns ContextHelloResponse|grpc:Error {
        map<string|string[]> headers = {};
        HelloRequest message;
        if req is ContextHelloRequest {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("helloWorld/hello", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <HelloResponse>result, headers: respHeaders};
    }

    isolated remote function GetMap(Empty|ContextEmpty req) returns GetMappedPermissionsOfUserAtLevelResponse|grpc:Error {
        map<string|string[]> headers = {};
        Empty message;
        if req is ContextEmpty {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("helloWorld/GetMap", message, headers);
        [anydata, map<string|string[]>] [result, _] = payload;
        return <GetMappedPermissionsOfUserAtLevelResponse>result;
    }

    isolated remote function GetMapContext(Empty|ContextEmpty req) returns ContextGetMappedPermissionsOfUserAtLevelResponse|grpc:Error {
        map<string|string[]> headers = {};
        Empty message;
        if req is ContextEmpty {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("helloWorld/GetMap", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <GetMappedPermissionsOfUserAtLevelResponse>result, headers: respHeaders};
    }
}

public isolated client class HelloWorldHelloResponseCaller {
    private final grpc:Caller caller;

    public isolated function init(grpc:Caller caller) {
        self.caller = caller;
    }

    public isolated function getId() returns int {
        return self.caller.getId();
    }

    isolated remote function sendHelloResponse(HelloResponse response) returns grpc:Error? {
        return self.caller->send(response);
    }

    isolated remote function sendContextHelloResponse(ContextHelloResponse response) returns grpc:Error? {
        return self.caller->send(response);
    }

    isolated remote function sendError(grpc:Error response) returns grpc:Error? {
        return self.caller->sendError(response);
    }

    isolated remote function complete() returns grpc:Error? {
        return self.caller->complete();
    }

    public isolated function isCancelled() returns boolean {
        return self.caller.isCancelled();
    }
}

public isolated client class HelloWorldGetMappedPermissionsOfUserAtLevelResponseCaller {
    private final grpc:Caller caller;

    public isolated function init(grpc:Caller caller) {
        self.caller = caller;
    }

    public isolated function getId() returns int {
        return self.caller.getId();
    }

    isolated remote function sendGetMappedPermissionsOfUserAtLevelResponse(GetMappedPermissionsOfUserAtLevelResponse response) returns grpc:Error? {
        return self.caller->send(response);
    }

    isolated remote function sendContextGetMappedPermissionsOfUserAtLevelResponse(ContextGetMappedPermissionsOfUserAtLevelResponse response) returns grpc:Error? {
        return self.caller->send(response);
    }

    isolated remote function sendError(grpc:Error response) returns grpc:Error? {
        return self.caller->sendError(response);
    }

    isolated remote function complete() returns grpc:Error? {
        return self.caller->complete();
    }

    public isolated function isCancelled() returns boolean {
        return self.caller.isCancelled();
    }
}

public type ContextEmpty record {|
    Empty content;
    map<string|string[]> headers;
|};

public type ContextHelloResponse record {|
    HelloResponse content;
    map<string|string[]> headers;
|};

public type ContextHelloRequest record {|
    HelloRequest content;
    map<string|string[]> headers;
|};

public type ContextGetMappedPermissionsOfUserAtLevelResponse record {|
    GetMappedPermissionsOfUserAtLevelResponse content;
    map<string|string[]> headers;
|};

@protobuf:Descriptor {value: HELLOWORLDWITHMAP_DESC}
public type Empty record {|
|};

@protobuf:Descriptor {value: HELLOWORLDWITHMAP_DESC}
public type HelloResponse record {|
    string message = "";
    record {|string key; string value;|}[] tags = [];
|};

@protobuf:Descriptor {value: HELLOWORLDWITHMAP_DESC}
public type HelloRequest record {|
    string name = "";
    record {|string key; string value;|}[] tags = [];
|};

@protobuf:Descriptor {value: HELLOWORLDWITHMAP_DESC}
public type MappedPermissionsList record {|
    string[] permissions = [];
|};

@protobuf:Descriptor {value: HELLOWORLDWITHMAP_DESC}
public type GetMappedPermissionsOfUserAtLevelResponse record {|
    record {|string key; MappedPermissionsList value;|}[] mapped_permissions = [];
|};
