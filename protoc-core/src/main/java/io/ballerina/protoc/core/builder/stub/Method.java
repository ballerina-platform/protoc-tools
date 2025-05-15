/*
 *  Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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
package io.ballerina.protoc.core.builder.stub;

import com.google.protobuf.DescriptorProtos;
import io.ballerina.protoc.core.MessageUtils;
import io.ballerina.protoc.core.MethodDescriptor;

import java.util.Map;

import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.componentsModuleMap;
import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.protofileModuleMap;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.PACKAGE_SEPARATOR;
import static io.ballerina.protoc.core.builder.balgen.BalGenerationUtils.getMappingBalType;

/**
 * Method definition bean class.
 *
 * @since 0.1.0
 */
public class Method {
    private final String methodName;
    private final String methodId;
    private final String inputType;
    private final String outputType;
    private final MethodDescriptor.MethodType methodType;
    private final String inputPackagePrefix;
    private final String outputPackagePrefix;

    private Method(String methodName, String methodId, String inputType, String outputType,
                   MethodDescriptor.MethodType methodType, String inputPackagePrefix, String outputPackagePrefix) {
        this.methodName = methodName;
        this.methodType = methodType;
        this.methodId = methodId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.inputPackagePrefix = inputPackagePrefix;
        this.outputPackagePrefix = outputPackagePrefix;
    }

    public static Builder newBuilder(String methodId) {
        return new Builder(methodId);
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodId() {
        return methodId;
    }

    public String getInputType() {
        return inputType;
    }

    public String getOutputType() {
        return outputType;
    }

    public MethodDescriptor.MethodType getMethodType() {
        return methodType;
    }

    public String getInputPackagePrefix(String filename) {
        if (!inputPackagePrefix.isEmpty() && protofileModuleMap.containsKey(filename)) {
            if (!protofileModuleMap.get(filename).substring(protofileModuleMap.get(filename)
                    .lastIndexOf(PACKAGE_SEPARATOR) + 1).equals(inputPackagePrefix)) {
                return inputPackagePrefix + COLON;
            }
        }
        return "";
    }

    public String getOutputPackageType(String filename) {
        if (!outputPackagePrefix.isEmpty() && protofileModuleMap.containsKey(filename)) {
            if (!protofileModuleMap.get(filename).substring(protofileModuleMap.get(filename)
                    .lastIndexOf(PACKAGE_SEPARATOR) + 1).equals(outputPackagePrefix)) {
                return outputPackagePrefix + COLON;
            }
        }
        return "";
    }

    public boolean containsEmptyType() {
        return inputType == null || outputType == null;
    }

    /**
     * Method Definition.Builder.
     */
    public static class Builder {
        String methodId;
        DescriptorProtos.MethodDescriptorProto methodDescriptor;
        Map<String, Message> messageMap;

        private Builder(String methodId) {
            this.methodId = methodId;
        }

        public Builder setMethodDescriptor(DescriptorProtos.MethodDescriptorProto methodDescriptor) {
            this.methodDescriptor = methodDescriptor;
            return this;
        }

        public Builder setMessageMap(Map<String, Message> messageMap) {
            this.messageMap = messageMap;
            return this;
        }

        public Method build() {
            MethodDescriptor.MethodType methodType = MessageUtils.getMethodType(methodDescriptor);
            String methodName = methodDescriptor.getName();
            String inputType = methodDescriptor.getInputType();
            inputType = getMappingBalType(inputType);
            String outputType = methodDescriptor.getOutputType();
            outputType = getMappingBalType(outputType);
            String inputPackageType = "";
            if (componentsModuleMap.containsKey(inputType)) {
                inputPackageType = componentsModuleMap.get(inputType).substring(componentsModuleMap
                        .get(inputType).lastIndexOf(PACKAGE_SEPARATOR) + 1);
            }
            String outputPackageType = "";
            if (componentsModuleMap.containsKey(outputType)) {
                outputPackageType = componentsModuleMap.get(outputType).substring(componentsModuleMap
                        .get(outputType).lastIndexOf(PACKAGE_SEPARATOR) + 1);
            }
            return new Method(methodName, methodId, inputType, outputType, methodType,
                    inputPackageType, outputPackageType);
        }
    }
}
