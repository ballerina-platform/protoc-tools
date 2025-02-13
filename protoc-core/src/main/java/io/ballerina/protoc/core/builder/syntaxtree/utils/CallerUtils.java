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

import io.ballerina.protoc.core.builder.syntaxtree.components.Class;
import io.ballerina.protoc.core.builder.syntaxtree.components.Function;
import io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.componentsModuleMap;
import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.protofileModuleMap;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.PACKAGE_SEPARATOR;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getFieldAccessExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getMethodCallExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getRemoteMethodCallActionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getObjectFieldNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getQualifiedNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.capitalize;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getModulePrefix;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getProtobufType;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.isBallerinaProtobufType;

/**
 * Utility functions related to Caller.
 *
 @since 0.1.0
 */
public class CallerUtils {

    private CallerUtils() {

    }

    public static Class getCallerClass(String key, String value, String filename) {
        Class caller = new Class(key, true);
        caller.addQualifiers(new String[]{"isolated", "client"});

        caller.addMember(
                getObjectFieldNode(
                        "private",
                        new String[]{"final"},
                        getQualifiedNameReferenceNode("grpc", "Caller"),
                        "caller"
                )
        );
        Function init = new Function("init");
        init.addRequiredParameter(
                TypeDescriptor.getQualifiedNameReferenceNode("grpc", "Caller"),
                "caller"
        );
        init.addAssignmentStatement(
                getFieldAccessExpressionNode("self", "caller"),
                getSimpleNameReferenceNode("caller")
        );
        init.addQualifiers(new String[]{"public", "isolated"});
        caller.addMember(init.getFunctionDefinitionNode());

        Function getId = new Function("getId");
        getId.addReturns(TypeDescriptor.getBuiltinSimpleNameReferenceNode("int"));
        getId.addReturnStatement(
                getMethodCallExpressionNode(
                        getFieldAccessExpressionNode("self", "caller"),
                        "getId"
                )
        );
        getId.addQualifiers(new String[]{"public", "isolated"});
        caller.addMember(getId.getFunctionDefinitionNode());

        if (value != null) {
            String valueCap;
            switch (value) {
                case "byte[]":
                    valueCap = "Bytes";
                    break;
                case "time:Utc":
                    valueCap = "Timestamp";
                    break;
                case "time:Seconds":
                    valueCap = "Duration";
                    break;
                case "map<anydata>":
                    valueCap = "Struct";
                    break;
                case "'any:Any":
                    valueCap = "Any";
                    break;
                default:
                    valueCap = capitalize(value);
                    break;
            }
            if (protofileModuleMap.containsKey(filename) && componentsModuleMap.containsKey(value)) {
                String componentModule = componentsModuleMap.get(value);
                if (!protofileModuleMap.get(filename).equals(componentModule)) {
                    value = componentModule.substring(componentModule.lastIndexOf(PACKAGE_SEPARATOR) + 1) + COLON
                            + value;
                }
            }
            Function send = new Function("send" + valueCap);
            send.addRequiredParameter(
                    getSimpleNameReferenceNode(value),
                    "response"
            );
            send.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
            send.addReturnStatement(
                    getRemoteMethodCallActionNode(
                            getFieldAccessExpressionNode("self", "caller"),
                            "send",
                            "response")
            );
            send.addQualifiers(new String[]{"isolated", "remote"});
            caller.addMember(send.getFunctionDefinitionNode());

            String contextParam = "Context" + valueCap;
            if (isBallerinaProtobufType(value)) {
                contextParam = getProtobufType(value) + COLON + contextParam;
            } else {
                contextParam = getModulePrefix(contextParam, filename) + contextParam;
            }
            Function sendContext = new Function("sendContext" + valueCap);
            sendContext.addRequiredParameter(
                    getSimpleNameReferenceNode(contextParam),
                    "response"
            );
            sendContext.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
            sendContext.addReturnStatement(
                    getRemoteMethodCallActionNode(
                            getFieldAccessExpressionNode("self", "caller"),
                            "send",
                            "response")
            );
            sendContext.addQualifiers(new String[]{"isolated", "remote"});
            caller.addMember(sendContext.getFunctionDefinitionNode());
        }

        Function sendError = new Function("sendError");
        sendError.addRequiredParameter(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR, "response");
        sendError.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        sendError.addReturnStatement(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "caller"),
                        "sendError",
                        "response")
        );
        sendError.addQualifiers(new String[]{"isolated", "remote"});
        caller.addMember(sendError.getFunctionDefinitionNode());

        Function complete = new Function("complete");
        complete.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        complete.addReturnStatement(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "caller"),
                        "complete"
                )
        );
        complete.addQualifiers(new String[]{"isolated", "remote"});
        caller.addMember(complete.getFunctionDefinitionNode());

        Function isCancelled = new Function("isCancelled");
        isCancelled.addReturns(TypeDescriptor.getBuiltinSimpleNameReferenceNode("boolean"));
        isCancelled.addReturnStatement(
                getMethodCallExpressionNode(
                        getFieldAccessExpressionNode("self", "caller"),
                        "isCancelled"
                )
        );
        isCancelled.addQualifiers(new String[]{"public", "isolated"});
        caller.addMember(isCancelled.getFunctionDefinitionNode());

        return caller;
    }
}
