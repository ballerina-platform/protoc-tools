/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.protoc;

/**
 * Descriptor for a service method.
 * <p>
 * Referenced from grpc-java implementation.
 * <p>
 *
 * @since 0.980.0
 */
public final class MethodDescriptor {

    private final MethodType type;
    private final String fullMethodName;
    private final com.google.protobuf.Descriptors.MethodDescriptor schemaDescriptor;

    /**
     * The call type of a method.
     */
    public enum MethodType {
        /**
         * One request message followed by one response message.
         */
        UNARY,

        /**
         * Zero or more request messages followed by one response message.
         */
        CLIENT_STREAMING,

        /**
         * One request message followed by zero or more response messages.
         */
        SERVER_STREAMING,

        /**
         * Zero or more request and response messages arbitrarily interleaved in time.
         */
        BIDI_STREAMING,

        /**
         * Unknown. something wrong in service definition.
         */
        UNKNOWN;

        /**
         * Returns where the client send one request message to the server.
         *
         * @return true, if client send one message. false otherwise.
         */
        public final boolean clientSendsOneMessage() {
            return this == UNARY || this == SERVER_STREAMING;
        }

        /**
         * Returns whether the server send one response message to the client.
         *
         * @return true, if server send one message. false otherwise.
         */
        public final boolean serverSendsOneMessage() {
            return this == UNARY || this == CLIENT_STREAMING;
        }
    }


    private MethodDescriptor(
            MethodType type,
            String fullMethodName,
            com.google.protobuf.Descriptors.MethodDescriptor schemaDescriptor) {

        this.type = type;
        this.fullMethodName = fullMethodName;
        this.schemaDescriptor = schemaDescriptor;
    }

    /**
     * Returns type of the method.
     *
     * @return method type.
     */
    public MethodType getType() {
        return type;
    }

    /**
     * Returns fully qualified name of the method.
     *
     * @return method full name
     */
    public String getFullMethodName() {
        return fullMethodName;
    }


    /**
     * Creates a new builder for a {@link MethodDescriptor}.
     *
     * @return new builder instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder for a {@link MethodDescriptor}.
     *
     */
    public static final class Builder {

        private MethodType type;
        private String fullMethodName;
        private com.google.protobuf.Descriptors.MethodDescriptor schemaDescriptor;

        private Builder() {
        }


        /**
         * Sets the method type.
         *
         * @param type the type of the method.
         * @return builder instance.
         */
        public Builder setType(MethodType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the fully qualified (service and method) method name.
         *
         * @param fullMethodName method name
         * @return builder instance.
         */
        public Builder setFullMethodName(String fullMethodName) {
            this.fullMethodName = fullMethodName;
            return this;
        }

        /**
         * Sets the schema descriptor for this builder.
         *
         * @param schemaDescriptor an object that describes the service structure.  Should be immutable.
         * @return builder instance.
         */
        public Builder setSchemaDescriptor(com.google.protobuf.Descriptors.MethodDescriptor
                                                   schemaDescriptor) {
            this.schemaDescriptor = schemaDescriptor;
            return this;
        }


        /**
         * Builds the method descriptor.
         *
         * @return new {@link MethodDescriptor} instance.
         */
        public MethodDescriptor build() {
            return new MethodDescriptor(
                    type,
                    fullMethodName,
                    schemaDescriptor);
        }
    }

}
