import ballerina/grpc;
import ballerina/lang.'int;
import ballerina/protobuf;

public const string HELLOWORLDWITHRESERVEDNAMES_DESC = "0A2168656C6C6F576F726C645769746852657365727665644E616D65732E70726F746F22A4140A0C48656C6C6F5265717565737412180A07636F6E74726F6C1801200128095207636F6E74726F6C12120A0464617461180220012809520464617461120E0A02696E1804200128095202696E12160A0672657475726E180520012809520672657475726E12120A04656E756D1806200128095204656E756D12160A06696D706F72741807200128095206696D706F7274120E0A0261731808200128095202617312160A067075626C696318092001280952067075626C696312180A0770726976617465180A20012809520770726976617465121A0A0865787465726E616C180B20012809520865787465726E616C12140A0566696E616C180C20012809520566696E616C12180A0773657276696365180D20012809520773657276696365121A0A087265736F75726365180E2001280952087265736F75726365121A0A0866756E6374696F6E180F20012809520866756E6374696F6E12160A066F626A65637418102001280952066F626A65637412160A067265636F726418112001280952067265636F7264121E0A0A616E6E6F746174696F6E181220012809520A616E6E6F746174696F6E121C0A09706172616D657465721813200128095209706172616D6574657212200A0B7472616E73666F726D6572181420012809520B7472616E73666F726D657212160A06776F726B65721815200128095206776F726B6572121A0A086C697374656E657218162001280952086C697374656E657212160A0672656D6F7465181720012809520672656D6F746512140A05786D6C6E731818200128095205786D6C6E7312180A0772657475726E73181920012809520772657475726E7312180A0776657273696F6E181A20012809520776657273696F6E12180A076368616E6E656C181B2001280952076368616E6E656C121A0A086162737472616374181C200128095208616273747261637412160A06636C69656E74181D200128095206636C69656E7412140A05636F6E7374181E200128095205636F6E737412160A06747970656F66181F200128095206747970656F6612160A06736F757263651820200128095206736F7572636512120A0466726F6D182120012809520466726F6D120E0A026F6E18222001280952026F6E12140A0567726F7570182320012809520567726F7570120E0A0262791824200128095202627912160A06686176696E671825200128095206686176696E6712140A056F7264657218262001280952056F7264657212140A05776865726518272001280952057768657265121A0A08666F6C6C6F7765641828200128095208666F6C6C6F77656412100A03666F721829200128095203666F7212160A0677696E646F77182A20012809520677696E646F7712140A056576657279182B200128095205657665727912160A0677697468696E182C20012809520677697468696E121A0A08736E617073686F74182D200128095208736E617073686F7412140A05696E6E6572182E200128095205696E6E657212140A056F75746572182F2001280952056F7574657212140A0572696768741830200128095205726967687412120A046C65667418312001280952046C65667412120A0466756C6C183220012809520466756C6C12260A0E756E69646972656374696F6E616C183320012809520E756E69646972656374696F6E616C12180A07666F72657665721834200128095207666F726576657212140A056C696D697418352001280952056C696D6974121C0A09617363656E64696E671836200128095209617363656E64696E67121E0A0A64657363656E64696E67183720012809520A64657363656E64696E6712100A03696E741838200128095203696E7412120A046279746518392001280952046279746512140A05666C6F6174183A200128095205666C6F617412180A07646563696D616C183B200128095207646563696D616C12180A07626F6F6C65616E183C200128095207626F6F6C65616E12160A06737472696E67183D200128095206737472696E6712100A036D6170183E2001280952036D617012100A03786D6C1840200128095203786D6C12140A057461626C6518412001280952057461626C6512160A0673747265616D184220012809520673747265616D12100A03616E791843200128095203616E79121A0A0874797065646573631844200128095208747970656465736312120A047479706518452001280952047479706512160A06667574757265184620012809520666757475726512160A0668616E646C65184820012809520668616E646C6512100A03766172184920012809520376617212100A036E6577184A2001280952036E657712120A04696E6974184B200128095204696E6974120E0A026966184C200128095202696612140A056D61746368184D2001280952056D6174636812120A04656C7365184E200128095204656C736512180A07666F7265616368184F200128095207666F726561636812140A057768696C6518502001280952057768696C65121A0A08636F6E74696E75651851200128095208636F6E74696E756512140A05627265616B1852200128095205627265616B12120A04666F726B1853200128095204666F726B12120A046A6F696E18542001280952046A6F696E12120A04736F6D651855200128095204736F6D6512100A03616C6C1856200128095203616C6C12100A03747279185720012809520374727912140A0563617463681858200128095205636174636812180A0766696E616C6C79185920012809520766696E616C6C7912140A057468726F77185A2001280952057468726F7712140A0570616E6963185B20012809520570616E696312120A0474726170185C2001280952047472617012200A0B7472616E73616374696F6E185D20012809520B7472616E73616374696F6E12140A0561626F7274185E20012809520561626F727412140A057265747279185F200128095205726574727912180A076F6E726574727918602001280952076F6E726574727912180A0772657472696573186120012809520772657472696573121C0A09636F6D6D69747465641862200128095209636F6D6D697474656412180A0761626F72746564186320012809520761626F7274656412120A047769746818642001280952047769746812120A046C6F636B18652001280952046C6F636B12180A07756E7461696E741866200128095207756E7461696E7412140A0573746172741867200128095205737461727412100A03627574186820012809520362757412140A05636865636B1869200128095205636865636B121E0A0A636865636B70616E6963186A20012809520A636865636B70616E6963121E0A0A7072696D6172796B6579186B20012809520A7072696D6172796B6579120E0A026973186C200128095202697312140A05666C757368186D200128095205666C75736812120A0477616974186E2001280952047761697412180A0764656661756C74186F20012809520764656661756C7412180A07756E6B6E6F776E1870200128095207756E6B6E6F776E121C0A05747970657318712001280E32062E54797065735205747970657322290A0D48656C6C6F526573706F6E736512180A076D65737361676518012001280952076D65737361676522220A0A4279655265717565737412140A05677265657418012001280952056772656574221F0A0B427965526573706F6E736512100A03736179180120012809520373617922360A0A4669656C6452756C657312200A04656E756D18102001280B320A2E456E756D52756C657348005204656E756D42060A0474797065226B0A09456E756D52756C657312140A05636F6E73741801200128055205636F6E737412210A0C646566696E65645F6F6E6C79180220012808520B646566696E65644F6E6C79120E0A02696E1803200328055202696E12150A066E6F745F696E18042003280552056E6F74496E2A830A0A055479706573120B0A07756E6B6E6F776E1000120B0A07636F6E74726F6C100112080A0464617461100212060A02696E1004120A0A0672657475726E100512080A04656E756D1006120A0A06696D706F7274100712060A0261731008120A0A067075626C69631009120B0A0770726976617465100A120C0A0865787465726E616C100B12090A0566696E616C100C120B0A0773657276696365100D120C0A087265736F75726365100E120C0A0866756E6374696F6E100F120A0A066F626A6563741010120A0A067265636F72641011120E0A0A616E6E6F746174696F6E1012120D0A09706172616D657465721013120F0A0B7472616E73666F726D65721014120A0A06776F726B65721015120C0A086C697374656E65721016120A0A0672656D6F7465101712090A05786D6C6E731018120B0A0772657475726E731019120B0A0776657273696F6E101A120B0A076368616E6E656C101B120C0A086162737472616374101C120A0A06636C69656E74101D12090A05636F6E7374101E120A0A06747970656F66101F120A0A06736F75726365102012080A0466726F6D102112060A026F6E102212090A0567726F7570102312060A0262791024120A0A06686176696E67102512090A056F72646572102612090A0577686572651027120C0A08666F6C6C6F776564102812070A03666F721029120A0A0677696E646F77102A12090A056576657279102B120A0A0677697468696E102C120C0A08736E617073686F74102D12090A05696E6E6572102E12090A056F75746572102F12090A057269676874103012080A046C656674103112080A0466756C6C103212120A0E756E69646972656374696F6E616C1033120B0A07666F7265766572103412090A056C696D69741035120D0A09617363656E64696E671036120E0A0A64657363656E64696E67103712070A03696E74103812080A0462797465103912090A05666C6F6174103A120B0A07646563696D616C103B120B0A07626F6F6C65616E103C120A0A06737472696E67103D12070A036D6170103E12070A03786D6C104012090A057461626C651041120A0A0673747265616D104212070A03616E791043120C0A087479706564657363104412080A04747970651045120A0A066675747572651046120A0A0668616E646C65104812070A03766172104912070A036E6577104A12080A04696E6974104B12060A026966104C12090A056D61746368104D12080A04656C7365104E120B0A07666F7265616368104F12090A057768696C651050120C0A08636F6E74696E7565105112090A05627265616B105212080A04666F726B105312080A046A6F696E105412080A04736F6D65105512070A03616C6C105612070A03747279105712090A0563617463681058120B0A0766696E616C6C79105912090A057468726F77105A12090A0570616E6963105B12080A0474726170105C120F0A0B7472616E73616374696F6E105D12090A0561626F7274105E12090A057265747279105F120B0A076F6E72657472791060120B0A07726574726965731061120D0A09636F6D6D69747465641062120B0A0761626F72746564106312080A0477697468106412080A046C6F636B1065120B0A07756E7461696E74106612090A057374617274106712070A03627574106812090A05636865636B1069120E0A0A636865636B70616E6963106A120E0A0A7072696D6172796B6579106B12060A026973106C12090A05666C757368106D12080A0477616974106E120B0A0764656661756C74106F32670A1B68656C6C6F576F726C645769746852657365727665644E616D657312260A0568656C6C6F120D2E48656C6C6F526571756573741A0E2E48656C6C6F526573706F6E736512200A03627965120B2E427965526571756573741A0C2E427965526573706F6E7365620670726F746F33";

public isolated client class helloWorldWithReservedNamesClient {
    *grpc:AbstractClientEndpoint;

    private final grpc:Client grpcClient;

    public isolated function init(string url, *grpc:ClientConfiguration config) returns grpc:Error? {
        self.grpcClient = check new (url, config);
        check self.grpcClient.initStub(self, HELLOWORLDWITHRESERVEDNAMES_DESC);
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
        var payload = check self.grpcClient->executeSimpleRPC("helloWorldWithReservedNames/hello", message, headers);
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
        var payload = check self.grpcClient->executeSimpleRPC("helloWorldWithReservedNames/hello", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <HelloResponse>result, headers: respHeaders};
    }

    isolated remote function bye(ByeRequest|ContextByeRequest req) returns ByeResponse|grpc:Error {
        map<string|string[]> headers = {};
        ByeRequest message;
        if req is ContextByeRequest {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("helloWorldWithReservedNames/bye", message, headers);
        [anydata, map<string|string[]>] [result, _] = payload;
        return <ByeResponse>result;
    }

    isolated remote function byeContext(ByeRequest|ContextByeRequest req) returns ContextByeResponse|grpc:Error {
        map<string|string[]> headers = {};
        ByeRequest message;
        if req is ContextByeRequest {
            message = req.content;
            headers = req.headers;
        } else {
            message = req;
        }
        var payload = check self.grpcClient->executeSimpleRPC("helloWorldWithReservedNames/bye", message, headers);
        [anydata, map<string|string[]>] [result, respHeaders] = payload;
        return {content: <ByeResponse>result, headers: respHeaders};
    }
}

public client class HelloWorldWithReservedNamesHelloResponseCaller {
    private grpc:Caller caller;

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

public client class HelloWorldWithReservedNamesByeResponseCaller {
    private grpc:Caller caller;

    public isolated function init(grpc:Caller caller) {
        self.caller = caller;
    }

    public isolated function getId() returns int {
        return self.caller.getId();
    }

    isolated remote function sendByeResponse(ByeResponse response) returns grpc:Error? {
        return self.caller->send(response);
    }

    isolated remote function sendContextByeResponse(ContextByeResponse response) returns grpc:Error? {
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

public type ContextByeResponse record {|
    ByeResponse content;
    map<string|string[]> headers;
|};

public type ContextByeRequest record {|
    ByeRequest content;
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

@protobuf:Descriptor {value: HELLOWORLDWITHRESERVEDNAMES_DESC}
public type FieldRules record {|
    EnumRules 'enum?;
|};

isolated function isValidFieldrules(FieldRules r) returns boolean {
    int typeCount = 0;
    if !(r?.'enum is ()) {
        typeCount += 1;
    }
    if (typeCount > 1) {
        return false;
    }
    return true;
}

isolated function setFieldRules_Enum(FieldRules r, EnumRules 'enum) {
    r.'enum = 'enum;
}

@protobuf:Descriptor {value: HELLOWORLDWITHRESERVEDNAMES_DESC}
public type EnumRules record {|
    int:Signed32 'const = 0;
    boolean defined_only = false;
    int:Signed32[] 'in = [];
    int:Signed32[] not_in = [];
|};

@protobuf:Descriptor {value: HELLOWORLDWITHRESERVEDNAMES_DESC}
public type ByeResponse record {|
    string say = "";
|};

@protobuf:Descriptor {value: HELLOWORLDWITHRESERVEDNAMES_DESC}
public type ByeRequest record {|
    string greet = "";
|};

@protobuf:Descriptor {value: HELLOWORLDWITHRESERVEDNAMES_DESC}
public type HelloResponse record {|
    string message = "";
|};

@protobuf:Descriptor {value: HELLOWORLDWITHRESERVEDNAMES_DESC}
public type HelloRequest record {|
    string control = "";
    string data = "";
    string 'in = "";
    string 'return = "";
    string 'enum = "";
    string 'import = "";
    string 'as = "";
    string 'public = "";
    string 'private = "";
    string 'external = "";
    string 'final = "";
    string 'service = "";
    string 'resource = "";
    string 'function = "";
    string 'object = "";
    string 'record = "";
    string 'annotation = "";
    string 'parameter = "";
    string 'transformer = "";
    string 'worker = "";
    string 'listener = "";
    string 'remote = "";
    string 'xmlns = "";
    string 'returns = "";
    string 'version = "";
    string 'channel = "";
    string 'abstract = "";
    string 'client = "";
    string 'const = "";
    string 'typeof = "";
    string 'source = "";
    string 'from = "";
    string 'on = "";
    string 'group = "";
    string 'by = "";
    string 'having = "";
    string 'order = "";
    string 'where = "";
    string 'followed = "";
    string 'for = "";
    string 'window = "";
    string 'every = "";
    string 'within = "";
    string 'snapshot = "";
    string 'inner = "";
    string 'outer = "";
    string 'right = "";
    string 'left = "";
    string 'full = "";
    string 'unidirectional = "";
    string 'forever = "";
    string 'limit = "";
    string 'ascending = "";
    string 'descending = "";
    string 'int = "";
    string 'byte = "";
    string 'float = "";
    string 'decimal = "";
    string 'boolean = "";
    string 'string = "";
    string 'map = "";
    string 'xml = "";
    string 'table = "";
    string 'stream = "";
    string 'any = "";
    string 'typedesc = "";
    string 'type = "";
    string 'future = "";
    string 'handle = "";
    string 'var = "";
    string 'new = "";
    string 'init = "";
    string 'if = "";
    string 'match = "";
    string 'else = "";
    string 'foreach = "";
    string 'while = "";
    string 'continue = "";
    string 'break = "";
    string 'fork = "";
    string 'join = "";
    string 'some = "";
    string 'all = "";
    string 'try = "";
    string 'catch = "";
    string 'finally = "";
    string 'throw = "";
    string 'panic = "";
    string 'trap = "";
    string 'transaction = "";
    string 'abort = "";
    string 'retry = "";
    string 'onretry = "";
    string 'retries = "";
    string 'committed = "";
    string 'aborted = "";
    string 'with = "";
    string 'lock = "";
    string 'untaint = "";
    string 'start = "";
    string 'but = "";
    string 'check = "";
    string 'checkpanic = "";
    string 'primarykey = "";
    string 'is = "";
    string 'flush = "";
    string 'wait = "";
    string 'default = "";
    string unknown = "";
    Types types = unknown;
|};

public enum Types {
    unknown, control, data, 'in, 'return, 'enum, 'import, 'as, 'public, 'private, 'external, 'final, 'service, 'resource, 'function, 'object, 'record, 'annotation, 'parameter, 'transformer, 'worker, 'listener, 'remote, 'xmlns, 'returns, 'version, 'channel, 'abstract, 'client, 'const, 'typeof, 'source, 'from, 'on, 'group, 'by, 'having, 'order, 'where, 'followed, 'for, 'window, 'every, 'within, 'snapshot, 'inner, 'outer, 'right, 'left, 'full, 'unidirectional, 'forever, 'limit, 'ascending, 'descending, 'int, 'byte, 'float, 'decimal, 'boolean, 'string, 'map, 'xml, 'table, 'stream, 'any, 'typedesc, 'type, 'future, 'handle, 'var, 'new, 'init, 'if, 'match, 'else, 'foreach, 'while, 'continue, 'break, 'fork, 'join, 'some, 'all, 'try, 'catch, 'finally, 'throw, 'panic, 'trap, 'transaction, 'abort, 'retry, 'onretry, 'retries, 'committed, 'aborted, 'with, 'lock, 'untaint, 'start, 'but, 'check, 'checkpanic, 'primarykey, 'is, 'flush, 'wait, 'default
}

