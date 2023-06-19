import ballerina/grpc;
import ballerina/protobuf;
import tool_test_packaging_with_enum_imports.messages;

public const string PACKAGEWITHENUMIMPORTS_DESC = "0A1C7061636B61676557697468456E756D496D706F7274732E70726F746F12097061636B6167696E671A2362616C6C6572696E612F70726F746F6275662F64657363726970746F722E70726F746F1A11656E756D4D6573736167652E70726F746F22460A074D65737361676512100A036D736718012001280952036D736712290A04656E756D18022001280E32152E7061636B6167696E672E53696D706C65456E756D5204656E756D32DC010A0A68656C6C6F576F726C6412300A0668656C6C6F3112122E7061636B6167696E672E4D6573736167651A122E7061636B6167696E672E4D65737361676512320A0668656C6C6F3212122E7061636B6167696E672E4D6573736167651A122E7061636B6167696E672E4D657373616765300112320A0668656C6C6F3312122E7061636B6167696E672E4D6573736167651A122E7061636B6167696E672E4D657373616765280112340A0668656C6C6F3412122E7061636B6167696E672E4D6573736167651A122E7061636B6167696E672E4D657373616765280130014228E24725746F6F6C5F746573745F7061636B6167696E675F776974685F656E756D5F696D706F727473620670726F746F33";
public const map<string> descriptorMap = {"enumMessage.proto": messages:ENUMMESSAGE_DESC};

public isolated client class helloWorldClient {
    *grpc:AbstractClientEndpoint;

    private final grpc:Client grpcClient;

    public isolated function init(string url, *grpc:ClientConfiguration config) returns grpc:Error? {
        self.grpcClient = check new (url, config);
        check self.grpcClient.initStub(self, PACKAGEWITHENUMIMPORTS_DESC, descriptorMap);
    }

    isolated remote function hello1(Message|ContextMessage req) returns Message|grpc:Error {
        map<string|string[]> headers = {};
        Message message;
        if req is ContextMessage {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("packaging.helloWorld/hello1", message, headers);
        [anydata, map<string|string[]>] [result, _] = payload;
        return <Message>result;
    }

    isolated remote function hello1Context(Message|ContextMessage req) returns ContextMessage|grpc:Error {
        map<string|string[]> headers = {};
        Message message;
        if req is ContextMessage {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("packaging.helloWorld/hello1", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <Message>result, headers: respHeaders};
    }

    isolated remote function hello3() returns Hello3StreamingClient|grpc:Error {
        grpc:StreamingClient sClient = check self.grpcClient->executeClientStreaming("packaging.helloWorld/hello3");
        return new Hello3StreamingClient(sClient);
    }

    isolated remote function hello2(Message|ContextMessage req) returns stream<Message, grpc:Error?>|grpc:Error {
        map<string|string[]> headers = {};
        Message message;
        if req is ContextMessage {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeServerStreaming("packaging.helloWorld/hello2", message, headers);
        [stream<anydata, grpc:Error?>, map<string|string[]>] [result, _] = payload;
        MessageStream outputStream = new MessageStream(result);
        return new stream<Message, grpc:Error?>(outputStream);
    }

    isolated remote function hello2Context(Message|ContextMessage req) returns ContextMessageStream|grpc:Error {
        map<string|string[]> headers = {};
        Message message;
        if req is ContextMessage {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeServerStreaming("packaging.helloWorld/hello2", message, headers);
        [stream<anydata, grpc:Error?>, map<string|string[]>] [result, respHeaders] = payload;
        MessageStream outputStream = new MessageStream(result);
        return {content: new stream<Message, grpc:Error?>(outputStream), headers: respHeaders};
    }

    isolated remote function hello4() returns Hello4StreamingClient|grpc:Error {
        grpc:StreamingClient sClient = check self.grpcClient->executeBidirectionalStreaming("packaging.helloWorld/hello4");
        return new Hello4StreamingClient(sClient);
    }
}

public client class Hello3StreamingClient {
    private grpc:StreamingClient sClient;

    isolated function init(grpc:StreamingClient sClient) {
        self.sClient = sClient;
    }

    isolated remote function sendMessage(Message message) returns grpc:Error? {
        return self.sClient->send(message);
    }

    isolated remote function sendContextMessage(ContextMessage message) returns grpc:Error? {
        return self.sClient->send(message);
    }

    isolated remote function receiveMessage() returns Message|grpc:Error? {
        var response = check self.sClient->receive();
        if response is () {
            return response;
        } else {
            [anydata, map<string|string[]>] [payload, _] = response;
            return <Message>payload;
        }
    }

    isolated remote function receiveContextMessage() returns ContextMessage|grpc:Error? {
        var response = check self.sClient->receive();
        if response is () {
            return response;
        } else {
            [anydata, map<string|string[]>] [payload, headers] = response;
            return {content: <Message>payload, headers: headers};
        }
    }

    isolated remote function sendError(grpc:Error response) returns grpc:Error? {
        return self.sClient->sendError(response);
    }

    isolated remote function complete() returns grpc:Error? {
        return self.sClient->complete();
    }
}

public class MessageStream {
    private stream<anydata, grpc:Error?> anydataStream;

    public isolated function init(stream<anydata, grpc:Error?> anydataStream) {
        self.anydataStream = anydataStream;
    }

    public isolated function next() returns record {|Message value;|}|grpc:Error? {
        var streamValue = self.anydataStream.next();
        if (streamValue is ()) {
            return streamValue;
        } else if (streamValue is grpc:Error) {
            return streamValue;
        } else {
            record {|Message value;|} nextRecord = {value: <Message>streamValue.value};
            return nextRecord;
        }
    }

    public isolated function close() returns grpc:Error? {
        return self.anydataStream.close();
    }
}

public client class Hello4StreamingClient {
    private grpc:StreamingClient sClient;

    isolated function init(grpc:StreamingClient sClient) {
        self.sClient = sClient;
    }

    isolated remote function sendMessage(Message message) returns grpc:Error? {
        return self.sClient->send(message);
    }

    isolated remote function sendContextMessage(ContextMessage message) returns grpc:Error? {
        return self.sClient->send(message);
    }

    isolated remote function receiveMessage() returns Message|grpc:Error? {
        var response = check self.sClient->receive();
        if response is () {
            return response;
        } else {
            [anydata, map<string|string[]>] [payload, _] = response;
            return <Message>payload;
        }
    }

    isolated remote function receiveContextMessage() returns ContextMessage|grpc:Error? {
        var response = check self.sClient->receive();
        if response is () {
            return response;
        } else {
            [anydata, map<string|string[]>] [payload, headers] = response;
            return {content: <Message>payload, headers: headers};
        }
    }

    isolated remote function sendError(grpc:Error response) returns grpc:Error? {
        return self.sClient->sendError(response);
    }

    isolated remote function complete() returns grpc:Error? {
        return self.sClient->complete();
    }
}

public type ContextMessageStream record {|
    stream<Message, error?> content;
    map<string|string[]> headers;
|};

public type ContextMessage record {|
    Message content;
    map<string|string[]> headers;
|};

@protobuf:Descriptor {value: PACKAGEWITHENUMIMPORTS_DESC}
public type Message record {|
    string msg = "";
    messages:SimpleEnum 'enum = messages:a;
|};

