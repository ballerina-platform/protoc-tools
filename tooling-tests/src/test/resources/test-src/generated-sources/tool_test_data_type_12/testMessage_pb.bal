import ballerina/lang.'int;
import ballerina/protobuf;
import ballerina/time;
import ballerina/protobuf.types.'any;

public const string TESTMESSAGE_DESC = "0A11746573744D6573736167652E70726F746F1A1F676F6F676C652F70726F746F6275662F74696D657374616D702E70726F746F1A19676F6F676C652F70726F746F6275662F616E792E70726F746F1A1C676F6F676C652F70726F746F6275662F7374727563742E70726F746F1A1E676F6F676C652F70726F746F6275662F6475726174696F6E2E70726F746F1A1E676F6F676C652F70726F746F6275662F77726170706572732E70726F746F221B0A05546573743112120A046E616D6518012001280952046E616D652285010A055465737432120C0A0161180120012809520161120C0A0162180220012801520162120C0A0163180320012802520163120C0A0164180420012805520164120C0A0165180520012803520165120C0A016618062001280D520166120C0A0167180720012804520167120C0A0168180820012807520168120C0A01691809200128065201692285010A055465737433120C0A0161180120032809520161120C0A0162180220032801520162120C0A0163180320032802520163120C0A0164180420032805520164120C0A0165180520032803520165120C0A016618062003280D520166120C0A0167180720032804520167120C0A0168180820032807520168120C0A016918092003280652016922FF010A055465737434123C0A0B74696D6544657461696C7318012001280B321A2E676F6F676C652E70726F746F6275662E54696D657374616D70520B74696D6544657461696C7312340A0A616E7944657461696C7318022001280B32142E676F6F676C652E70726F746F6275662E416E79520A616E7944657461696C73123D0A0D73747275637444657461696C7318032001280B32172E676F6F676C652E70726F746F6275662E537472756374520D73747275637444657461696C7312430A0F6475726174696F6E44657461696C7318042001280B32192E676F6F676C652E70726F746F6275662E4475726174696F6E520F6475726174696F6E44657461696C7322FF010A055465737435123C0A0B74696D6544657461696C7318012003280B321A2E676F6F676C652E70726F746F6275662E54696D657374616D70520B74696D6544657461696C7312340A0A616E7944657461696C7318022003280B32142E676F6F676C652E70726F746F6275662E416E79520A616E7944657461696C73123D0A0D73747275637444657461696C7318032003280B32172E676F6F676C652E70726F746F6275662E537472756374520D73747275637444657461696C7312430A0F6475726174696F6E44657461696C7318042003280B32192E676F6F676C652E70726F746F6275662E4475726174696F6E520F6475726174696F6E44657461696C73228D030A055465737436122A0A016118012001280B321C2E676F6F676C652E70726F746F6275662E537472696E6756616C756552016112290A016218022001280B321B2E676F6F676C652E70726F746F6275662E496E74333256616C7565520162122A0A016318032001280B321C2E676F6F676C652E70726F746F6275662E55496E74333256616C756552016312290A016418042001280B321B2E676F6F676C652E70726F746F6275662E496E74363456616C7565520164122A0A016518052001280B321C2E676F6F676C652E70726F746F6275662E55496E74363456616C7565520165122A0A016618062001280B321C2E676F6F676C652E70726F746F6275662E446F75626C6556616C756552016612290A016718072001280B321B2E676F6F676C652E70726F746F6275662E466C6F617456616C756552016712280A016818082001280B321A2E676F6F676C652E70726F746F6275662E426F6F6C56616C756552016812290A016918092001280B321B2E676F6F676C652E70726F746F6275662E427974657356616C7565520169620670726F746F33";

@protobuf:Descriptor {value: TESTMESSAGE_DESC}
public type Test1 record {|
    string name = "";
|};

@protobuf:Descriptor {value: TESTMESSAGE_DESC}
public type Test6 record {|
    string a = "";
    int b = 0;
    int c = 0;
    int d = 0;
    int e = 0;
    float f = 0;
    float g = 0;
    boolean h = false;
    byte[] i = [];
|};

@protobuf:Descriptor {value: TESTMESSAGE_DESC}
public type Test5 record {|
    time:Utc[] timeDetails = [];
    'any:Any[] anyDetails = [];
    map<anydata>[] structDetails = [];
    time:Seconds[] durationDetails = [];
|};

@protobuf:Descriptor {value: TESTMESSAGE_DESC}
public type Test4 record {|
    time:Utc timeDetails = [0, 0.0d];
    'any:Any anyDetails = {};
    map<anydata> structDetails = {};
    time:Seconds durationDetails = 0.0d;
|};

@protobuf:Descriptor {value: TESTMESSAGE_DESC}
public type Test3 record {|
    string[] a = [];
    float[] b = [];
    float[] c = [];
    int:Signed32[] d = [];
    int[] e = [];
    int:Unsigned32[] f = [];
    int[] g = [];
    int[] h = [];
    int[] i = [];
|};

@protobuf:Descriptor {value: TESTMESSAGE_DESC}
public type Test2 record {|
    string a = "";
    float b = 0.0;
    float c = 0.0;
    int:Signed32 d = 0;
    int e = 0;
    int:Unsigned32 f = 0;
    int g = 0;
    int h = 0;
    int i = 0;
|};

