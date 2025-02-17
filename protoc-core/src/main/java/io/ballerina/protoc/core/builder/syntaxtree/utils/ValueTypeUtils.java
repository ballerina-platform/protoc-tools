/*
 *  Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.protoc.core.builder.syntaxtree.utils;

import io.ballerina.protoc.core.builder.syntaxtree.components.Record;
import io.ballerina.protoc.core.builder.syntaxtree.components.Type;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.componentsModuleMap;
import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.protofileModuleMap;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.PACKAGE_SEPARATOR;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getUnionTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.CONTENT;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.HEADERS;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.capitalize;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.isBallerinaBasicType;

/**
 * Utility functions related to ValueType.
 *
 @since 0.1.0
 */
public class ValueTypeUtils {

    private ValueTypeUtils() {

    }

    public static Type getValueTypeStream(String key, String filename) {
        String typeName;
        switch (key) {
            case "byte[]":
                typeName = "ContextBytesStream";
                break;
            case "time:Utc":
                typeName = "ContextTimestampStream";
                break;
            case "time:Seconds":
                typeName = "ContextDurationStream";
                break;
            case "map<anydata>":
                typeName = "ContextStructStream";
                break;
            case "'any:Any":
                typeName = "ContextAnyStream";
                break;
            default:
                typeName = "Context" + capitalize(key) + "Stream";
                break;
        }
        Record contextStream = new Record();
        if (componentsModuleMap.containsKey(key) && protofileModuleMap.containsKey(filename) &&
                !componentsModuleMap.get(key).equals(protofileModuleMap.get(filename))) {
            contextStream.addStreamField(componentsModuleMap.get(key).substring(componentsModuleMap.get(key)
                    .lastIndexOf(PACKAGE_SEPARATOR) + 1) + COLON + key, CONTENT);
        } else {
            contextStream.addStreamField(key, CONTENT);
        }
        contextStream.addMapField(
                HEADERS,
                getUnionTypeDescriptorNode(
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING,
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING_ARRAY
                )
        );
        if (protofileModuleMap.containsKey(filename)) {
            componentsModuleMap.put(typeName, protofileModuleMap.get(filename));
        }
        return new Type(
                true,
                typeName,
                contextStream.getRecordTypeDescriptorNode()
        );
    }

    public static Type getValueType(String key, String filename) {
        String typeName;
        Record contextString = new Record();
        if (key == null) {
            typeName = "ContextNil";
        } else {
            switch (key) {
                case "byte[]":
                    typeName = "ContextBytes";
                    break;
                case "time:Utc":
                    typeName = "ContextTimestamp";
                    break;
                case "time:Seconds":
                    typeName = "ContextDuration";
                    break;
                case "map<anydata>":
                    typeName = "ContextStruct";
                    break;
                case "'any:Any":
                    typeName = "ContextAny";
                    break;
                default:
                    typeName = "Context" + capitalize(key);
                    break;
            }
            if (isBallerinaBasicType(key)) {
                contextString.addBasicField(key, CONTENT);
            } else if (componentsModuleMap.containsKey(key) && protofileModuleMap.containsKey(filename) &&
                    !componentsModuleMap.get(key).equals(protofileModuleMap.get(filename))) {
                contextString.addCustomField(componentsModuleMap.get(key).substring(componentsModuleMap.get(key)
                        .lastIndexOf(PACKAGE_SEPARATOR) + 1) + COLON + key, CONTENT);
            } else {
                contextString.addCustomField(key, CONTENT);
            }
        }
        contextString.addMapField(
                HEADERS,
                getUnionTypeDescriptorNode(
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING,
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING_ARRAY
                )
        );
        if (protofileModuleMap.containsKey(filename)) {
            componentsModuleMap.put(typeName, protofileModuleMap.get(filename));
        }
        return new Type(
                true,
                typeName,
                contextString.getRecordTypeDescriptorNode()
        );
    }
}
