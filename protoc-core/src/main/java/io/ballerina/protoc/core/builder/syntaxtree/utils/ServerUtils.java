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
import io.ballerina.protoc.core.builder.stub.Method;
import io.ballerina.protoc.core.builder.syntaxtree.components.Class;
import io.ballerina.protoc.core.builder.syntaxtree.components.Function;
import io.ballerina.protoc.core.builder.syntaxtree.components.IfElse;
import io.ballerina.protoc.core.builder.syntaxtree.components.Map;
import io.ballerina.protoc.core.builder.syntaxtree.components.Record;
import io.ballerina.protoc.core.builder.syntaxtree.components.VariableDeclaration;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.componentsModuleMap;
import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.protofileModuleMap;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.PACKAGE_SEPARATOR;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getExplicitNewExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getFieldAccessExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getMethodCallExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getTypeTestExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Statement.getReturnStatementNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getBuiltinSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getCaptureBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getListBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getMapTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getNilTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getObjectFieldNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getStreamTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTupleTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypedBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getUnionTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.CONTENT;
import static io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants.HEADERS;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.addClientCallBody;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getMethodType;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getModulePrefix;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.getProtobufType;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.isBallerinaProtobufType;

/**
 * Utility functions related to Server.
 *
 @since 0.1.0
 */
public class ServerUtils {

    private ServerUtils() {

    }

    public static Function getServerStreamingFunction(Method method, String filename) {
        Function function = new Function(method.getMethodName());
        String inputCap = "Nil";
        if (method.getInputType() != null) {
            inputCap = getMethodType(method.getInputType());
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
        String outCap = getMethodType(method.getOutputType());
        function.addReturns(
                getUnionTypeDescriptorNode(
                        getStreamTypeDescriptorNode(
                                getSimpleNameReferenceNode(method.getOutputPackageType(filename) +
                                        method.getOutputType()),
                                SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL
                        ),
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR
                )
        );
        addServerBody(function, method, inputCap, outCap, "_", filename);
        function.addReturnStatement(
                getExplicitNewExpressionNode(
                        getStreamTypeDescriptorNode(
                                getSimpleNameReferenceNode(method.getOutputPackageType(filename) +
                                        method.getOutputType()),
                                SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL
                        ),
                        "outputStream")
        );
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    public static Function getServerStreamingContextFunction(Method method, String filename) {
        Function function = new Function(method.getMethodName() + "Context");
        String inputCap = "Nil";
        if (method.getInputType() != null) {
            inputCap = getMethodType(method.getInputType());
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
        String outputCap = getMethodType(method.getOutputType());
        String contextStreamParam = "Context" + outputCap + "Stream";
        if (isBallerinaProtobufType(method.getOutputType())) {
            contextStreamParam = getProtobufType(method.getOutputType()) + COLON + contextStreamParam;
        } else if (componentsModuleMap.containsKey(contextStreamParam) && protofileModuleMap.containsKey(filename)) {
            String module = componentsModuleMap.get(contextStreamParam);
            if (!protofileModuleMap.get(filename).equals(module)) {
                contextStreamParam = module.substring(module.lastIndexOf(PACKAGE_SEPARATOR) + 1) + COLON
                        + contextStreamParam;
            }
        }
        function.addReturns(
                getUnionTypeDescriptorNode(
                        getSimpleNameReferenceNode(contextStreamParam),
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR
                )
        );
        addServerBody(function, method, inputCap, outputCap, "respHeaders", filename);
        Map returnMap = new Map();
        returnMap.addField(
                CONTENT,
                getExplicitNewExpressionNode(
                        getStreamTypeDescriptorNode(
                                getSimpleNameReferenceNode(method.getOutputPackageType(filename) +
                                        method.getOutputType()),
                                SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL
                        ),
                        "outputStream")
        );
        returnMap.addSimpleNameReferenceField(HEADERS, "respHeaders");
        function.addReturnStatement(returnMap.getMappingConstructorExpressionNode());
        function.addQualifiers(new String[]{"isolated", "remote"});
        return function;
    }

    public static Class getServerStreamClass(Method method, String filename) {
        String outputCap = getMethodType(method.getOutputType());
        Class serverStream = new Class(outputCap + "Stream", true);
        if (protofileModuleMap.containsKey(filename)) {
            componentsModuleMap.put(outputCap + "Stream", protofileModuleMap.get(filename));
        }
        serverStream.addMember(
                getObjectFieldNode(
                        "private",
                        new String[]{},
                        getStreamTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                                SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL), "anydataStream"));

        serverStream.addMember(getInitFunction().getFunctionDefinitionNode());

        serverStream.addMember(getNextFunction(method, filename).getFunctionDefinitionNode());

        serverStream.addMember(getCloseFunction().getFunctionDefinitionNode());

        return serverStream;
    }

    private static Function getInitFunction() {
        Function function = new Function("init");
        function.addRequiredParameter(
                getStreamTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL), "anydataStream"
        );
        function.addAssignmentStatement(
                getFieldAccessExpressionNode("self", "anydataStream"),
                getSimpleNameReferenceNode("anydataStream")
        );
        function.addQualifiers(new String[]{"public", "isolated"});
        return function;
    }

    private static Function getNextFunction(Method method, String filename) {
        Function function = new Function("next");
        Record nextRecord = new Record();
        nextRecord.addCustomField(method.getOutputPackageType(filename) +
                method.getOutputType(), "value");
        function.addReturns(
                getUnionTypeDescriptorNode(
                        nextRecord.getRecordTypeDescriptorNode(),
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL
                )
        );
        VariableDeclaration streamValue = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getBuiltinSimpleNameReferenceNode("var"),
                        getCaptureBindingPatternNode("streamValue")
                ),
                getMethodCallExpressionNode(
                        getFieldAccessExpressionNode("self", "anydataStream"),
                        "next"
                )
        );
        function.addVariableStatement(streamValue.getVariableDeclarationNode());

        IfElse streamValueNilCheck = new IfElse(
                getTypeTestExpressionNode(
                        getSimpleNameReferenceNode("streamValue"),
                        getNilTypeDescriptorNode()
                )
        );
        streamValueNilCheck.addIfStatement(
                getReturnStatementNode(
                        getSimpleNameReferenceNode("streamValue")
                )
        );
        IfElse streamValueErrorCheck = new IfElse(
                getTypeTestExpressionNode(
                        getSimpleNameReferenceNode("streamValue"),
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR
                )
        );
        streamValueErrorCheck.addIfStatement(
                getReturnStatementNode(
                        getSimpleNameReferenceNode("streamValue")
                )
        );

        Record nextRecordRec = new Record();
        nextRecordRec.addCustomField(method.getOutputPackageType(filename) +
                method.getOutputType(), "value");
        Map nextRecordMap = new Map();
        if (method.getOutputType().equals("time:Utc")) {
            nextRecordMap.addTypeCastExpressionField(
                    "value",
                    method.getOutputPackageType(filename) + method.getOutputType(),
                    getMethodCallExpressionNode(
                            getFieldAccessExpressionNode("streamValue", "value"),
                            "cloneReadOnly"
                    )
            );
        } else {
            nextRecordMap.addTypeCastExpressionField(
                    "value",
                    method.getOutputPackageType(filename) + method.getOutputType(),
                    getFieldAccessExpressionNode("streamValue", "value")
            );
        }
        VariableDeclaration nextRecordVar = new VariableDeclaration(
                getTypedBindingPatternNode(
                        nextRecordRec.getRecordTypeDescriptorNode(),
                        getCaptureBindingPatternNode("nextRecord")
                ),
                nextRecordMap.getMappingConstructorExpressionNode()
        );
        streamValueErrorCheck.addElseStatement(
                nextRecordVar.getVariableDeclarationNode()
        );
        streamValueErrorCheck.addElseStatement(
                getReturnStatementNode(
                        getSimpleNameReferenceNode("nextRecord")
                )
        );
        streamValueNilCheck.addElseBody(streamValueErrorCheck);

        function.addIfElseStatement(streamValueNilCheck.getIfElseStatementNode());
        function.addQualifiers(new String[]{"public", "isolated"});
        return function;
    }

    private static Function getCloseFunction() {
        Function function = new Function("close");
        function.addReturns(SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL);
        function.addReturnStatement(
                getMethodCallExpressionNode(
                        getFieldAccessExpressionNode("self", "anydataStream"),
                        "close"
                )
        );
        function.addQualifiers(new String[]{"public", "isolated"});
        return function;
    }

    private static void addServerBody(Function function, Method method, String inputCap, String outCap,
                                      String headers, String filename) {

        addClientCallBody(function, inputCap, method, filename);
        SeparatedNodeList<Node> payloadArgs = NodeFactory.createSeparatedNodeList(
                getStreamTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                        SyntaxTreeConstants.SYNTAX_TREE_GRPC_ERROR_OPTIONAL), SyntaxTreeConstants.SYNTAX_TREE_COMMA,
                getMapTypeDescriptorNode(
                        getUnionTypeDescriptorNode(
                                SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING,
                                SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING_ARRAY
                        )
                )
        );
        VariableDeclaration payloadTuple = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getTupleTypeDescriptorNode(payloadArgs),
                        getListBindingPatternNode(new String[]{"result", headers})),
                getSimpleNameReferenceNode("payload")
        );
        function.addVariableStatement(payloadTuple.getVariableDeclarationNode());

        String streamParam = outCap + "Stream";
        if (isBallerinaProtobufType(method.getOutputType())) {
            String streamParamPrefix = "s" + getProtobufType(method.getOutputType());
            if (inputCap.equals("Any")) {
                streamParamPrefix = "sany";
            }
            streamParam = streamParamPrefix + COLON + streamParam;
        }
        if (componentsModuleMap.containsKey(streamParam) && protofileModuleMap.containsKey(filename)) {
            String module = componentsModuleMap.get(streamParam);
            if (!protofileModuleMap.get(filename).equals(module)) {
                streamParam = module.substring(module.lastIndexOf(PACKAGE_SEPARATOR) + 1) + COLON + streamParam;
            }
        }
        VariableDeclaration stream = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getSimpleNameReferenceNode(streamParam),
                        getCaptureBindingPatternNode("outputStream")
                ),
                getExplicitNewExpressionNode(streamParam, new String[]{"result"})
        );
        function.addVariableStatement(stream.getVariableDeclarationNode());
    }
}
