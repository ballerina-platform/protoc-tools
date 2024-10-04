import ballerina/grpc;
import ballerina/protobuf;
import ballerina/protobuf.types.empty;

public const string EMPTYFIELDMESSAGE_DESC = "0A17656D7074794669656C644D6573736167652E70726F746F1204746573741A1B676F6F676C652F70726F746F6275662F656D7074792E70726F746F22410A0B436F6D706C65785479706512100A03666F6F18012001280D5203666F6F12200A0362617218022001280E320E2E746573742E54657374456E756D520362617222790A134F7074696F6E616C436F6D706C65785479706512360A0D636F6D706C65785F76616C756518012001280B32112E746573742E436F6D706C657854797065520C636F6D706C657856616C7565122A0A046E6F6E6518022001280B32162E676F6F676C652E70726F746F6275662E456D70747952046E6F6E6522500A0E4F7074696F6E616C537472696E6712120A046E616D6518012001280952046E616D65122A0A046E6F6E6518022001280B32162E676F6F676C652E70726F746F6275662E456D70747952046E6F6E65223D0A0E526571756573744D657373616765122B0A0372657118012003280B32192E746573742E4F7074696F6E616C436F6D706C6578547970655203726571223B0A0F526573706F6E73654D65737361676512280A047265737018012003280B32142E746573742E4F7074696F6E616C537472696E675204726573702A280A0854657374456E756D120D0A09454E5452595F4F4E451000120D0A09454E5452595F54574F100132450A0B546573745365727669636512360A075465737452504312142E746573742E526571756573744D6573736167651A152E746573742E526573706F6E73654D657373616765620670726F746F33";

public isolated client class TestServiceClient {
    *grpc:AbstractClientEndpoint;

    private final grpc:Client grpcClient;

    public isolated function init(string url, *grpc:ClientConfiguration config) returns grpc:Error? {
        self.grpcClient = check new (url, config);
        check self.grpcClient.initStub(self, EMPTYFIELDMESSAGE_DESC);
    }

    isolated remote function TestRPC(RequestMessage|ContextRequestMessage req) returns ResponseMessage|grpc:Error {
        map<string|string[]> headers = {};
        RequestMessage message;
        if req is ContextRequestMessage {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("test.TestService/TestRPC", message, headers);
        [anydata, map<string|string[]>] [result, _] = payload;
        return <ResponseMessage>result;
    }

    isolated remote function TestRPCContext(RequestMessage|ContextRequestMessage req) returns ContextResponseMessage|grpc:Error {
        map<string|string[]> headers = {};
        RequestMessage message;
        if req is ContextRequestMessage {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("test.TestService/TestRPC", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <ResponseMessage>result, headers: respHeaders};
    }
}

public isolated client class TestServiceResponseMessageCaller {
    private final grpc:Caller caller;

    public isolated function init(grpc:Caller caller) {
        self.caller = caller;
    }

    public isolated function getId() returns int {
        return self.caller.getId();
    }

    isolated remote function sendResponseMessage(ResponseMessage response) returns grpc:Error? {
        return self.caller->send(response);
    }

    isolated remote function sendContextResponseMessage(ContextResponseMessage response) returns grpc:Error? {
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

public type ContextRequestMessage record {|
    RequestMessage content;
    map<string|string[]> headers;
|};

public type ContextResponseMessage record {|
    ResponseMessage content;
    map<string|string[]> headers;
|};

@protobuf:Descriptor {value: EMPTYFIELDMESSAGE_DESC}
public type RequestMessage record {|
    OptionalComplexType[] req = [];
|};

@protobuf:Descriptor {value: EMPTYFIELDMESSAGE_DESC}
public type OptionalComplexType record {|
    ComplexType complex_value = {};
    empty:Empty none = {};
|};

@protobuf:Descriptor {value: EMPTYFIELDMESSAGE_DESC}
public type ResponseMessage record {|
    OptionalString[] resp = [];
|};

@protobuf:Descriptor {value: EMPTYFIELDMESSAGE_DESC}
public type OptionalString record {|
    string name = "";
    empty:Empty none = {};
|};

@protobuf:Descriptor {value: EMPTYFIELDMESSAGE_DESC}
public type ComplexType record {|
    int:Unsigned32 foo = 0;
    TestEnum bar = ENTRY_ONE;
|};

public enum TestEnum {
    ENTRY_ONE, ENTRY_TWO
}

