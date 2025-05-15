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

import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.TypedBindingPatternNode;
import io.ballerina.protoc.core.builder.stub.Method;
import io.ballerina.protoc.core.builder.syntaxtree.components.Class;
import io.ballerina.protoc.core.builder.syntaxtree.components.Function;
import io.ballerina.protoc.core.builder.syntaxtree.components.IfElse;
import io.ballerina.protoc.core.builder.syntaxtree.components.Map;
import io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor;
import io.ballerina.protoc.core.builder.syntaxtree.components.VariableDeclaration;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import static io.ballerina.protoc.core.MethodDescriptor.MethodType.BIDI_STREAMING;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getCheckExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getExplicitNewExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getFieldAccessExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getMethodCallExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getRemoteMethodCallActionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getTypeTestExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Statement.getAssignmentStatementNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Statement.getReturnStatementNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getBuiltinSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getCaptureBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getListBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getMapTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getNilTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getObjectFieldNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getQualifiedNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTupleTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypeCastExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypedBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getUnionTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getWildcardBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.CONTENT;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.HEADERS;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.STREAMING_CLIENT;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING_ARRAY;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.capitalize;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getModulePrefix;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getProtobufType;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.isBallerinaProtobufType;

/**
 * Utility functions related to Client.
 *
 @since 0.1.0
 */
public class ClientUtils {

    private ClientUtils() {

    }

    public static Function getStreamingClientFunction(Method method) {
        String methodName = method.getMethodType().equals(BIDI_STREAMING) ? "executeBidirectionalStreaming" :
                "executeClientStreaming";
        String clientName = capitalize(method.getMethodName()) + STREAMING_CLIENT;
        Function function = new Function(method.getMethodName());
        function.addReturns(
                getUnionTypeDescriptorNode(
                        getSimpleNameReferenceNode(clientName),
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR
                )
        );
        VariableDeclaration sClient = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getQualifiedNameReferenceNode("grpc", STREAMING_CLIENT),
                        getCaptureBindingPatternNode("sClient")
                ),
                getCheckExpressionNode(
                        getRemoteMethodCallActionNode(
                                getFieldAccessExpressionNode("self", "grpcClient"),
                                methodName,
                                "\"" + method.getMethodId() + "\"")
                )
        );
        function.addVariableStatement(sClient.getVariableDeclarationNode());
        function.addReturnStatement(getExplicitNewExpressionNode(clientName, new String[]{"sClient"}));
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    public static Class getStreamingClientClass(Method method, String filename) {
        String name = method.getMethodName().substring(0, 1).toUpperCase() + method.getMethodName().substring(1) +
                STREAMING_CLIENT;
        Class streamingClient = new Class(name, true);
        streamingClient.addQualifiers(new String[]{"isolated", "client"});

        streamingClient.addMember(
                getObjectFieldNode(
                        "private",
                        new String[]{"final"},
                        getQualifiedNameReferenceNode("grpc", STREAMING_CLIENT),
                        "sClient"));

        streamingClient.addMember(getInitFunction().getFunctionDefinitionNode());

        streamingClient.addMember(getSendFunction(method, filename).getFunctionDefinitionNode());

        streamingClient.addMember(getSendContextFunction(method, filename).getFunctionDefinitionNode());

        streamingClient.addMember(getReceiveFunction(method, filename).getFunctionDefinitionNode());

        streamingClient.addMember(getReceiveContextFunction(method, filename).getFunctionDefinitionNode());

        streamingClient.addMember(getSendErrorFunction().getFunctionDefinitionNode());

        streamingClient.addMember(getCompleteFunction().getFunctionDefinitionNode());

        return streamingClient;
    }

    private static Function getInitFunction() {
        Function function = new Function("init");
        function.addRequiredParameter(
                TypeDescriptor.getQualifiedNameReferenceNode("grpc", STREAMING_CLIENT),
                "sClient"
        );
        function.addAssignmentStatement(
                getFieldAccessExpressionNode("self", "sClient"),
                getSimpleNameReferenceNode("sClient")
        );
        function.addQualifiers(new String[]{"isolated"});
        return function;
    }

    private static Function getSendFunction(Method method, String filename) {
        String inputCap;
        switch (method.getInputType()) {
            case "byte[]":
                inputCap = "Bytes";
                break;
            case "time:Utc":
                inputCap = "Timestamp";
                break;
            case "time:Seconds":
                inputCap = "Duration";
                break;
            case "map<anydata>":
                inputCap = "Struct";
                break;
            case "'any:Any":
                inputCap = "Any";
                break;
            default:
                inputCap = capitalize(method.getInputType());
                break;
        }
        Function function = new Function("send" + inputCap);
        function.addRequiredParameter(
                getSimpleNameReferenceNode(method.getInputPackagePrefix(filename) + method.getInputType()),
                "message"
        );
        function.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        function.addReturnStatement(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "sClient"),
                        "send",
                        "message")
        );
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    private static Function getSendContextFunction(Method method, String filename) {
        String inputCap;
        switch (method.getInputType()) {
            case "byte[]":
                inputCap = "Bytes";
                break;
            case "time:Utc":
                inputCap = "Timestamp";
                break;
            case "time:Seconds":
                inputCap = "Duration";
                break;
            case "map<anydata>":
                inputCap = "Struct";
                break;
            case "'any:Any":
                inputCap = "Any";
                break;
            default:
                inputCap = capitalize(method.getInputType());
                break;
        }
        Function function = new Function("sendContext" + inputCap);
        String contextParam = "Context" + inputCap;
        if (isBallerinaProtobufType(method.getInputType())) {
            contextParam = getProtobufType(method.getInputType()) + COLON + contextParam;
        } else {
            contextParam = getModulePrefix(contextParam, filename) + contextParam;
        }
        function.addRequiredParameter(
                getSimpleNameReferenceNode(contextParam),
                "message"
        );
        function.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        function.addReturnStatement(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "sClient"),
                        "send",
                        "message")
        );
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    private static Function getReceiveFunction(Method method, String filename) {
        String functionName = "receive";
        if (method.getOutputType() != null) {
            String outCap;
            switch (method.getOutputType()) {
                case "byte[]":
                    outCap = "Bytes";
                    break;
                case "time:Utc":
                    outCap = "Timestamp";
                    break;
                case "time:Seconds":
                    outCap = "Duration";
                    break;
                case "map<anydata>":
                    outCap = "Struct";
                    break;
                case "'any:Any":
                    outCap = "Any";
                    break;
                default:
                    outCap = capitalize(method.getOutputType());
                    break;
            }
            functionName = "receive" + outCap;
        }
        Function function = new Function(functionName);
        SeparatedNodeList<Node> receiveArgs = NodeFactory.createSeparatedNodeList(
                getBuiltinSimpleNameReferenceNode("anydata"),
                SyntaxTreeConstants.SYNTAX_TREE_COMMA,
                getMapTypeDescriptorNode(
                        getUnionTypeDescriptorNode(
                                SYNTAX_TREE_VAR_STRING,
                                SYNTAX_TREE_VAR_STRING_ARRAY
                        )
                )
        );
        if (method.getOutputType() != null) {
            function.addReturns(
                    TypeDescriptor.getUnionTypeDescriptorNode(
                            getSimpleNameReferenceNode(method.getOutputPackageType(filename) + method.getOutputType()),
                            SYNTAX_TREE_GRPC_ERROR_OPTIONAL
                    )
            );
        } else {
            function.addReturns(SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        }
        VariableDeclaration response = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getBuiltinSimpleNameReferenceNode("var"),
                        getCaptureBindingPatternNode("response")
                ),
                getCheckExpressionNode(
                        getRemoteMethodCallActionNode(
                                getFieldAccessExpressionNode("self", "sClient"),
                                "receive"
                        )
                )
        );
        function.addVariableStatement(response.getVariableDeclarationNode());
        IfElse responseCheck = new IfElse(
                getTypeTestExpressionNode(
                        getSimpleNameReferenceNode("response"),
                        getNilTypeDescriptorNode()
                )
        );
        responseCheck.addIfStatement(
                getReturnStatementNode(
                        getSimpleNameReferenceNode("response")
                )
        );

        if (method.getOutputType() == null) {
            responseCheck.addElseStatement(
                    getAssignmentStatementNode(
                            getWildcardBindingPatternNode().toSourceCode(),
                            getSimpleNameReferenceNode("response")
                    )
            );
        } else {
            responseCheck.addElseStatement(
                    new VariableDeclaration(
                            getTypedBindingPatternNode(
                                    getTupleTypeDescriptorNode(receiveArgs),
                                    getListBindingPatternNode(new String[]{"payload", "_"})
                            ),
                            getSimpleNameReferenceNode("response")
                    ).getVariableDeclarationNode()
            );
        }

        if (method.getOutputType() != null) {
            if (method.getOutputType().equals("string")) {
                responseCheck.addElseStatement(
                        getReturnStatementNode(
                                getMethodCallExpressionNode(
                                        getSimpleNameReferenceNode("payload"),
                                        "toString"
                                )
                        )
                );
            } else if (method.getOutputType().equals("time:Utc")) {
                responseCheck.addElseStatement(
                        getReturnStatementNode(
                                getTypeCastExpressionNode(
                                        method.getOutputType(),
                                        getMethodCallExpressionNode(
                                                getSimpleNameReferenceNode("payload"),
                                                "cloneReadOnly"
                                        )
                                )
                        )
                );
            } else {
                responseCheck.addElseStatement(
                        getReturnStatementNode(
                                getTypeCastExpressionNode(
                                        method.getOutputPackageType(filename) + method.getOutputType(),
                                        getSimpleNameReferenceNode("payload")
                                )
                        )
                );
            }
        }
        function.addIfElseStatement(responseCheck.getIfElseStatementNode());
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    private static Function getReceiveContextFunction(Method method, String filename) {
        String outCap = "Nil";
        if (method.getOutputType() != null) {
            switch (method.getOutputType()) {
                case "byte[]":
                    outCap = "Bytes";
                    break;
                case "time:Utc":
                    outCap = "Timestamp";
                    break;
                case "time:Seconds":
                    outCap = "Duration";
                    break;
                case "map<anydata>":
                    outCap = "Struct";
                    break;
                case "'any:Any":
                    outCap = "Any";
                    break;
                default:
                    outCap = capitalize(method.getOutputType());
                    break;
            }
        }
        Function function = new Function("receiveContext" + outCap);
        SeparatedNodeList<Node> receiveArgs = NodeFactory.createSeparatedNodeList(
                getBuiltinSimpleNameReferenceNode("anydata"),
                SyntaxTreeConstants.SYNTAX_TREE_COMMA,
                getMapTypeDescriptorNode(
                        getUnionTypeDescriptorNode(
                                SYNTAX_TREE_VAR_STRING,
                                SYNTAX_TREE_VAR_STRING_ARRAY
                        )
                )
        );
        TypedBindingPatternNode receiveArgsPattern;
        if (method.getOutputType() == null) {
            receiveArgsPattern = getTypedBindingPatternNode(
                    getTupleTypeDescriptorNode(receiveArgs),
                    getListBindingPatternNode(new String[]{"_", HEADERS})
            );
        } else {
            receiveArgsPattern = getTypedBindingPatternNode(
                    getTupleTypeDescriptorNode(receiveArgs),
                    getListBindingPatternNode(new String[]{"payload", HEADERS})
            );
        }
        String contextParam = "Context" + outCap;
        if (isBallerinaProtobufType(method.getOutputType())) {
            contextParam = getProtobufType(method.getOutputType()) + COLON + contextParam;
        } else {
            contextParam = getModulePrefix(contextParam, filename) + contextParam;
        }
        function.addReturns(
                TypeDescriptor.getUnionTypeDescriptorNode(
                        getSimpleNameReferenceNode(contextParam),
                        SYNTAX_TREE_GRPC_ERROR_OPTIONAL
                )
        );
        VariableDeclaration response = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getBuiltinSimpleNameReferenceNode("var"),
                        getCaptureBindingPatternNode("response")
                ),
                getCheckExpressionNode(
                        getRemoteMethodCallActionNode(
                                getFieldAccessExpressionNode("self", "sClient"),
                                "receive"
                        )
                )
        );
        function.addVariableStatement(response.getVariableDeclarationNode());
        IfElse responseCheck = new IfElse(
                getTypeTestExpressionNode(
                        getSimpleNameReferenceNode("response"),
                        getNilTypeDescriptorNode()
                )
        );
        responseCheck.addIfStatement(
                getReturnStatementNode(
                        getSimpleNameReferenceNode("response")
                )
        );
        responseCheck.addElseStatement(
                new VariableDeclaration(
                        receiveArgsPattern,
                        getSimpleNameReferenceNode("response")
                ).getVariableDeclarationNode()
        );
        Map returnMap = new Map();
        if (method.getOutputType() != null) {
            if (method.getOutputType().equals("string")) {
                returnMap.addMethodCallField(
                        CONTENT,
                        getSimpleNameReferenceNode("payload"),
                        "toString"
                );
            } else if (method.getOutputType().equals("time:Utc")) {
                returnMap.addTypeCastExpressionField(
                        CONTENT,
                        method.getOutputType(),
                        getMethodCallExpressionNode(
                                getSimpleNameReferenceNode("payload"),
                                "cloneReadOnly"
                        )
                );
            } else {
                returnMap.addTypeCastExpressionField(
                        CONTENT,
                        method.getOutputPackageType(filename) + method.getOutputType(),
                        getSimpleNameReferenceNode("payload")
                );
            }
        }
        returnMap.addSimpleNameReferenceField(HEADERS, HEADERS);
        responseCheck.addElseStatement(
                getReturnStatementNode(
                        returnMap.getMappingConstructorExpressionNode()
                )
        );
        function.addIfElseStatement(responseCheck.getIfElseStatementNode());
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    private static Function getSendErrorFunction() {
        Function function = new Function("sendError");
        function.addRequiredParameter(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR, "response");
        function.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        function.addReturnStatement(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "sClient"),
                        "sendError",
                        "response")
        );
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    private static Function getCompleteFunction() {
        Function function = new Function("complete");
        function.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        function.addReturnStatement(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "sClient"),
                        "complete"
                )
        );
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }
}
