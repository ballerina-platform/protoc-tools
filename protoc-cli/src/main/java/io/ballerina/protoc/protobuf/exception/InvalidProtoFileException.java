package io.ballerina.protoc.protobuf.exception;

/**
 * Thrown to indicate that the given proto file is not valid.
 *
 * @since 1.0.0
 */
public class InvalidProtoFileException extends Exception {

    public InvalidProtoFileException(String message) {
        super(message);
    }

    public InvalidProtoFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
