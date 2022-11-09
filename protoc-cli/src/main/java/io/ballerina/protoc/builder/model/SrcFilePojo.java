package io.ballerina.protoc.builder.model;

/**
 * Model class representing generated source file information.
 */
public class SrcFilePojo {
    private String mode;
    private String fileName;
    private String content;

    public SrcFilePojo(String mode, String fileName, String content) {
        this.mode = mode;
        this.fileName = fileName;
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}
