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
import io.ballerina.protoc.core.builder.syntaxtree.components.Function;
import io.ballerina.protoc.core.builder.syntaxtree.components.Map;
import io.ballerina.protoc.core.builder.syntaxtree.components.VariableDeclaration;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getMethodCallExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getBuiltinSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getListBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getMapTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTupleTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypeCastExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypedBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getUnionTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.CONTENT;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.HEADERS;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING_ARRAY;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.addClientCallBody;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.capitalize;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getModulePrefix;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getProtobufType;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.isBallerinaProtobufType;

/**
 * Utility functions related to Unary.
 *
 @since 0.1.0
 */
public class UnaryUtils {

    private UnaryUtils() {

    }

    public static Function getUnaryFunction(Method method, String filename) {
        Function function = new Function(method.getMethodName());
        function.addQualifiers(new String[]{"isolated", "remote"});
        String inputCap = "Nil";
        if (method.getInputType() != null) {
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
            String contextParam = "Context" + inputCap;
            if (isBallerinaProtobufType(method.getInputType())) {
                contextParam = getProtobufType(method.getInputType()) + COLON + contextParam;
            } else {
                contextParam = getModulePrefix(contextParam, filename) + contextParam;
            }
            function.addRequiredParameter(
                    getUnionTypeDescriptorNode(
                            getSimpleNameReferenceNode(
                                    method.getInputPackagePrefix(filename) + method.getInputType()
                            ),
                            getSimpleNameReferenceNode(contextParam)
                    ),
                    "req"
            );
        }
        if (method.getOutputType() != null) {
            function.addReturns(
                    getUnionTypeDescriptorNode(
                            getSimpleNameReferenceNode(
                                    method.getOutputPackageType(filename) + method.getOutputType()
                            ),
                            SYNTAX_TREE_GRPC_ERROR
                    )
            );
        } else {
            function.addReturns(
                    SYNTAX_TREE_GRPC_ERROR_OPTIONAL
            );
        }
        addClientCallBody(function, inputCap, method, filename);
        if (method.getOutputType() != null) {
            SeparatedNodeList<Node> payloadArgs = NodeFactory.createSeparatedNodeList(
                    getBuiltinSimpleNameReferenceNode("anydata"),
                    SyntaxTreeConstants.SYNTAX_TREE_COMMA,
                    getMapTypeDescriptorNode(
                            getUnionTypeDescriptorNode(
                                    SYNTAX_TREE_VAR_STRING,
                                    SYNTAX_TREE_VAR_STRING_ARRAY
                            )
                    )
            );
            VariableDeclaration payload = new VariableDeclaration(
                    getTypedBindingPatternNode(
                            getTupleTypeDescriptorNode(payloadArgs),
                            getListBindingPatternNode(new String[]{"result", "_"})),
                    getSimpleNameReferenceNode("payload")
            );
            function.addVariableStatement(payload.getVariableDeclarationNode());
            addUnaryFunctionReturnStatement(function, method, filename);
        }
        return function;
    }

    public static Function getUnaryContextFunction(Method method, String filename) {
        Function function = new Function(method.getMethodName() + "Context");
        function.addQualifiers(new String[]{"isolated", "remote"});
        String inputCap = "Nil";
        String outCap;
        if (method.getInputType() != null) {
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
            String contextParam = "Context" + inputCap;
            if (isBallerinaProtobufType(method.getInputType())) {
                contextParam = getProtobufType(method.getInputType()) + COLON + contextParam;
            } else {
                contextParam = getModulePrefix(contextParam, filename) + contextParam;
            }
            function.addRequiredParameter(
                    getUnionTypeDescriptorNode(
                            getSimpleNameReferenceNode(method.getInputPackagePrefix(filename) + method.getInputType()),
                            getSimpleNameReferenceNode(contextParam)
                    ),
                    "req"
            );
        }
        String contextParam;
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
            contextParam = "Context" + outCap;
            if (isBallerinaProtobufType(method.getOutputType())) {
                contextParam = getProtobufType(method.getOutputType()) + COLON + contextParam;
            } else {
                contextParam = getModulePrefix(contextParam, filename) + contextParam;
            }
        } else {
            contextParam = "empty:ContextNil";
        }
        function.addReturns(
                getUnionTypeDescriptorNode(
                        getSimpleNameReferenceNode(contextParam),
                        SYNTAX_TREE_GRPC_ERROR
                )
        );
        addClientCallBody(function, inputCap, method, filename);
        SeparatedNodeList<Node> payloadArgs = NodeFactory.createSeparatedNodeList(
                getBuiltinSimpleNameReferenceNode("anydata"),
                SyntaxTreeConstants.SYNTAX_TREE_COMMA,
                getMapTypeDescriptorNode(
                        getUnionTypeDescriptorNode(
                                SYNTAX_TREE_VAR_STRING, SYNTAX_TREE_VAR_STRING_ARRAY
                        )
                )
        );
        TypedBindingPatternNode bindingPatternNode;
        if (method.getOutputType() == null) {
            bindingPatternNode = getTypedBindingPatternNode(
                    getTupleTypeDescriptorNode(payloadArgs),
                    getListBindingPatternNode(new String[]{"_", "respHeaders"})
            );
        } else {
            bindingPatternNode = getTypedBindingPatternNode(
                    getTupleTypeDescriptorNode(payloadArgs),
                    getListBindingPatternNode(new String[]{"result", "respHeaders"})
            );
        }
        VariableDeclaration payload = new VariableDeclaration(
                bindingPatternNode,
                getSimpleNameReferenceNode("payload")
        );
        function.addVariableStatement(payload.getVariableDeclarationNode());
        addUnaryContextFunctionReturnStatement(function, method, filename);
        return function;
    }

    private static void addUnaryFunctionReturnStatement(Function function, Method method, String filename) {
        if (method.getOutputType().equals("string")) {
            function.addReturnStatement(
                    getMethodCallExpressionNode(
                            getSimpleNameReferenceNode("result"),
                            "toString"
                    )
            );
        } else if (method.getOutputType().equals("time:Utc")) {
            function.addReturnStatement(
                    getTypeCastExpressionNode(
                            method.getOutputPackageType(filename) + method.getOutputType(),
                            getMethodCallExpressionNode(
                                    getSimpleNameReferenceNode("result"),
                                    "cloneReadOnly"
                            )
                    )
            );
        } else {
            function.addReturnStatement(
                    getTypeCastExpressionNode(
                            method.getOutputPackageType(filename) + method.getOutputType(),
                            getSimpleNameReferenceNode("result")
                    )
            );
        }
    }

    private static void addUnaryContextFunctionReturnStatement(Function function, Method method, String filename) {
        Map returnMap = new Map();
        if (method.getOutputType() != null) {
            if (method.getOutputType().equals("string")) {
                returnMap.addMethodCallField(
                        CONTENT,
                        getSimpleNameReferenceNode("result"),
                        "toString"
                );
            } else if (method.getOutputType().equals("time:Utc")) {
                returnMap.addTypeCastExpressionField(
                        CONTENT,
                        method.getOutputType(),
                        getMethodCallExpressionNode(
                                getSimpleNameReferenceNode("result"),
                                "cloneReadOnly"
                        )
                );
            } else {
                returnMap.addTypeCastExpressionField(
                        CONTENT,
                        method.getOutputPackageType(filename) + method.getOutputType(),
                        getSimpleNameReferenceNode("result"));
            }
        }
        returnMap.addSimpleNameReferenceField(HEADERS, "respHeaders");
        function.addReturnStatement(returnMap.getMappingConstructorExpressionNode());
    }
}
