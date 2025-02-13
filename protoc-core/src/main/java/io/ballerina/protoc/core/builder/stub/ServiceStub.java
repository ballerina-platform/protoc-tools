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

import io.ballerina.protoc.core.exception.CodeBuilderException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.ballerina.protoc.core.MessageUtils.getCallerTypeName;

/**
 * Service stub definition bean class.
 *
 * @since 0.1.0
 */
public class ServiceStub {
    private final String serviceName;
    private final List<Method> unaryFunctions = new ArrayList<>();
    private final List<Method> serverStreamingFunctions = new ArrayList<>();
    //both client streaming and bidirectional streaming have same client side behaviour.
    private final List<Method> clientStreamingFunctions = new ArrayList<>();

    private final List<Method> bidiStreamingFunctions = new ArrayList<>();
    private final Map<String, String> callerMap = new HashMap<>();
    // this map uses to generate content context record types. Boolean value contains whether the content is stream
    // type or not.
    private final Map<String, Boolean> valueTypeMap = new HashMap<>();

    private ServiceStub(String serviceName) {
        this.serviceName = serviceName;
    }

    public static Builder newBuilder(String serviceName) {
        return new Builder(serviceName);
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<Method> getUnaryFunctions() {
        return Collections.unmodifiableList(unaryFunctions);
    }

    public List<Method> getServerStreamingFunctions() {
        return Collections.unmodifiableList(serverStreamingFunctions);
    }

    public List<Method> getClientStreamingFunctions() {
        return Collections.unmodifiableList(clientStreamingFunctions);
    }

    public List<Method> getBidiStreamingFunctions() {
        return Collections.unmodifiableList(bidiStreamingFunctions);
    }

    public Map<String, Boolean> getValueTypeMap() {
        return Collections.unmodifiableMap(valueTypeMap);
    }

    public Map<String, String> getCallerMap() {
        return Collections.unmodifiableMap(callerMap);
    }

    /**
     * Service stub definition builder.
     */
    public static class Builder {
        String serviceName;
        List<Method> methodList = new ArrayList<>();

        private Builder(String serviceName) {
            this.serviceName = serviceName;
        }

        public void addMethod(Method method) {
            methodList.add(method);
        }

        public ServiceStub build() throws CodeBuilderException {
            ServiceStub serviceStub = new ServiceStub(serviceName);
            for (Method method : methodList) {
                String callerTypeName = getCallerTypeName(serviceName, method.getOutputType());
                serviceStub.callerMap.put(callerTypeName, method.getOutputType());
                switch (method.getMethodType()) {
                    case UNARY:
                        updateValueTypeMap(serviceStub, method.getInputType(), Boolean.FALSE);
                        updateValueTypeMap(serviceStub, method.getOutputType(), Boolean.FALSE);
                        serviceStub.unaryFunctions.add(method);
                        break;
                    case SERVER_STREAMING:
                        updateValueTypeMap(serviceStub, method.getInputType(), Boolean.FALSE);
                        updateValueTypeMap(serviceStub, method.getOutputType(), Boolean.TRUE);
                        serviceStub.serverStreamingFunctions.add(method);
                        break;
                    case CLIENT_STREAMING:
                        updateValueTypeMap(serviceStub, method.getInputType(), Boolean.TRUE);
                        updateValueTypeMap(serviceStub, method.getOutputType(), Boolean.FALSE);
                        serviceStub.clientStreamingFunctions.add(method);
                        break;
                    case BIDI_STREAMING:
                        updateValueTypeMap(serviceStub, method.getInputType(), Boolean.TRUE);
                        updateValueTypeMap(serviceStub, method.getOutputType(), Boolean.TRUE);
                        serviceStub.bidiStreamingFunctions.add(method);
                        break;
                    default:
                        throw new CodeBuilderException("Method type is unknown or not supported.");
                }
            }
            return serviceStub;
        }

        private void updateValueTypeMap(ServiceStub serviceStub, String key, Boolean value) {
            if (!serviceStub.valueTypeMap.containsKey(key)) {
                serviceStub.valueTypeMap.put(key, value);
            } else {
                if (!serviceStub.valueTypeMap.get(key)) {
                    serviceStub.valueTypeMap.put(key, value);
                }
            }
        }
    }
}
