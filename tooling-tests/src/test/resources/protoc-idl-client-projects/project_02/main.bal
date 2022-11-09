client "./../client-generation-resources/helloworld.proto" as foo;
client "./../client-generation-resources/google.proto" as bar;

public function main() {
    foo:client x;
    bar:client y;
}
