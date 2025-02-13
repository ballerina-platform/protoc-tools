/*
 *  Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.ballerina.protoc.core;

import com.google.protobuf.DescriptorProtos;

/**
 * Util methods to generate protobuf message.
 *
 * @since 1.0.0
 */
public class MessageUtils {


    /**
     * Returns Custom Caller Type name using service name and return type.
     *
     * @param serviceName Service name defined in the contract.
     * @param returnType output type.
     * @return Caller type name.
     */
    public static String getCallerTypeName(String serviceName, String returnType) {
        if (returnType != null) {
            if (returnType.equals("time:Utc")) {
                returnType = "Timestamp";
            }
            if (returnType.equals("time:Seconds")) {
                returnType = "Duration";
            }
            if (returnType.equals("map<anydata>")) {
                returnType = "Struct";
            }
            if (returnType.equals("'any:Any")) {
                returnType = "Any";
            }
            returnType = returnType.replaceAll("[^a-zA-Z0-9]", "");
            return serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1) +
                    returnType.substring(0, 1).toUpperCase() + returnType.substring(1) +
                    "Caller";
        } else {
            return serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1) + "NilCaller";
        }
    }

    /**
     * Util method to get method type.
     *
     * @param methodDescriptorProto method descriptor proto.
     * @return service method type.
     */
    public static MethodDescriptor.MethodType getMethodType(DescriptorProtos.MethodDescriptorProto
                                                                    methodDescriptorProto) {
        if (methodDescriptorProto.getClientStreaming() && methodDescriptorProto.getServerStreaming()) {
            return MethodDescriptor.MethodType.BIDI_STREAMING;
        } else if (!(methodDescriptorProto.getClientStreaming() || methodDescriptorProto.getServerStreaming())) {
            return MethodDescriptor.MethodType.UNARY;
        } else if (methodDescriptorProto.getServerStreaming()) {
            return MethodDescriptor.MethodType.SERVER_STREAMING;
        } else {
            return MethodDescriptor.MethodType.CLIENT_STREAMING;
        }
    }

    private MessageUtils() {
    }
}
