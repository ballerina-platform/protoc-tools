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

package io.ballerina.protoc.builder.syntaxtree.utils;

import io.ballerina.compiler.syntax.tree.CheckExpressionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.protoc.builder.stub.Field;
import io.ballerina.protoc.builder.stub.Message;
import io.ballerina.protoc.builder.stub.Method;
import io.ballerina.protoc.builder.stub.ServiceStub;
import io.ballerina.protoc.builder.stub.StubFile;
import io.ballerina.protoc.builder.syntaxtree.components.Function;
import io.ballerina.protoc.builder.syntaxtree.components.IfElse;
import io.ballerina.protoc.builder.syntaxtree.components.Imports;
import io.ballerina.protoc.builder.syntaxtree.components.Map;
import io.ballerina.protoc.builder.syntaxtree.components.VariableDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.ballerina.protoc.GrpcConstants.ORG_NAME;
import static io.ballerina.protoc.MethodDescriptor.MethodType.UNARY;
import static io.ballerina.protoc.builder.BallerinaFileBuilder.componentsModuleMap;
import static io.ballerina.protoc.builder.BallerinaFileBuilder.protofileModuleMap;
import static io.ballerina.protoc.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.builder.balgen.BalGenConstants.PACKAGE_SEPARATOR;
import static io.ballerina.protoc.builder.syntaxtree.components.Expression.getCheckExpressionNode;
import static io.ballerina.protoc.builder.syntaxtree.components.Expression.getFieldAccessExpressionNode;
import static io.ballerina.protoc.builder.syntaxtree.components.Expression.getRemoteMethodCallActionNode;
import static io.ballerina.protoc.builder.syntaxtree.components.Expression.getTypeTestExpressionNode;
import static io.ballerina.protoc.builder.syntaxtree.components.Statement.getAssignmentStatementNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getBuiltinSimpleNameReferenceNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getCaptureBindingPatternNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getMapTypeDescriptorNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getTypedBindingPatternNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getUnionTypeDescriptorNode;
import static io.ballerina.protoc.builder.syntaxtree.components.TypeDescriptor.getWildcardBindingPatternNode;
import static io.ballerina.protoc.builder.syntaxtree.constants.SyntaxTreeConstants.CONTENT;
import static io.ballerina.protoc.builder.syntaxtree.constants.SyntaxTreeConstants.HEADERS;
import static io.ballerina.protoc.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING;
import static io.ballerina.protoc.builder.syntaxtree.constants.SyntaxTreeConstants.SYNTAX_TREE_VAR_STRING_ARRAY;

/**
 * Utility functions common to Syntax tree generation.
 *
 @since 0.1.0
 */
public class CommonUtils {

    private static final Set<String> STANDARD_IMPORTS = Set.of(
            "google/protobuf/wrappers.proto", "google/protobuf/timestamp.proto", "google/protobuf/duration.proto",
            "google/protobuf/any.proto", "google/protobuf/empty.proto", "google/protobuf/type.proto",
            "google/protobuf/struct.proto", "google/protobuf/descriptor.proto", "google/protobuf/api.proto",
            "google/protobuf/field_mask.proto", "google/protobuf/source_context.proto", "google/api/annotations.proto",
            "google/api/http.proto", "ballerina/protobuf/descriptor.proto");

    private CommonUtils() {

    }

    public static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String capitalizeFirstLetter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static String toCamelCase(String value) {
        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public static String toPascalCase(String str) {
        StringBuilder pascalCaseOutput = new StringBuilder();
        for (String s : str.split("_")) {
            s = s.replaceAll("[^a-zA-Z0-9]", "");
            pascalCaseOutput.append(capitalize(s));
        }
        return pascalCaseOutput.toString();
    }

    public static boolean isBallerinaBasicType(String type) {
        switch (type) {
            case "string" :
            case "int" :
            case "float" :
            case "boolean" :
            case "bytes" :
                return true;
            default:
                return false;
        }
    }

    public static boolean isBallerinaProtobufType(String type) {
        if (type == null) {
            return true;
        }
        switch (type) {
            case "string" :
            case "int" :
            case "float" :
            case "boolean" :
            case "byte[]" :
            case "time:Utc" :
            case "time:Seconds" :
            case "map<anydata>" :
            case "'any:Any" :
                return true;
            default:
                return false;
        }
    }

    public static String getProtobufType(String type) {
        if (type == null) {
            return "empty";
        }
        switch (type) {
            case "string" :
            case "int" :
            case "float" :
            case "boolean" :
            case "byte[]" :
                return "wrappers";
            case "time:Utc" :
                return "timestamp";
            case "time:Seconds" :
                return "duration";
            case "map<anydata>" :
                return "struct";
            case "'any:Any" :
                return "'any";
            default:
                return "";
        }
    }

    public static String getMethodType(String methodType) {
        if (methodType == null) {
            return "";
        }
        switch (methodType) {
            case "byte[]":
                return "Bytes";
            case "time:Utc":
                return "Timestamp";
            case "time:Seconds":
                return "Duration";
            case "map<anydata>":
                return "Struct";
            case "'any:Any":
                return "Any";
            default:
                return capitalize(methodType);
        }
    }

    public static void addClientCallBody(Function function, String inputCap, Method method, String filename) {
        String methodName = method.getMethodType().equals(UNARY) ? "executeSimpleRPC" : "executeServerStreaming";
        if (method.getInputType() == null) {
            Map empty = new Map();
            VariableDeclaration message = new VariableDeclaration(
                    getTypedBindingPatternNode(
                            getSimpleNameReferenceNode("empty:Empty"),
                            getCaptureBindingPatternNode("message")
                    ),
                    empty.getMappingConstructorExpressionNode()
            );
            function.addVariableStatement(message.getVariableDeclarationNode());
        }
        VariableDeclaration headers = new VariableDeclaration(
                getTypedBindingPatternNode(
                        getMapTypeDescriptorNode(
                                getUnionTypeDescriptorNode(
                                        SYNTAX_TREE_VAR_STRING,
                                        SYNTAX_TREE_VAR_STRING_ARRAY
                                )
                        ),
                        getCaptureBindingPatternNode(HEADERS)),
                new Map().getMappingConstructorExpressionNode()
        );
        function.addVariableStatement(headers.getVariableDeclarationNode());

        if (method.getInputType() != null) {
            TypeDescriptorNode messageType;
            if (method.getInputType().equals("string")) {
                messageType = getBuiltinSimpleNameReferenceNode("string");
            } else {
                messageType = getSimpleNameReferenceNode(method.getInputPackagePrefix(filename) +
                        method.getInputType());
            }
            VariableDeclaration message = new VariableDeclaration(
                    getTypedBindingPatternNode(
                            messageType,
                            getCaptureBindingPatternNode("message")),
                    null
            );
            function.addVariableStatement(message.getVariableDeclarationNode());
            String contextParam = "Context" + inputCap;
            if (isBallerinaProtobufType(method.getInputType())) {
                contextParam = getProtobufType(method.getInputType()) + COLON + contextParam;
            } else {
                contextParam = getModulePrefix(contextParam, filename) + contextParam;
            }
            IfElse reqIsContext = new IfElse(
                    getTypeTestExpressionNode(
                            getSimpleNameReferenceNode("req"),
                            getSimpleNameReferenceNode(contextParam)
                    ));
            reqIsContext.addIfStatement(
                    getAssignmentStatementNode(
                            "message",
                            getFieldAccessExpressionNode("req", CONTENT)
                    )
            );
            reqIsContext.addIfStatement(
                    getAssignmentStatementNode(
                            HEADERS,
                            getFieldAccessExpressionNode("req", HEADERS)
                    )
            );
            reqIsContext.addElseStatement(
                    getAssignmentStatementNode(
                            "message",
                            getSimpleNameReferenceNode("req")
                    )
            );
            function.addIfElseStatement(reqIsContext.getIfElseStatementNode());
        }
        CheckExpressionNode checkExpressionNode = getCheckExpressionNode(
                getRemoteMethodCallActionNode(
                        getFieldAccessExpressionNode("self", "grpcClient"),
                        methodName,
                        "\"" + method.getMethodId() + "\"", "message", HEADERS)
        );
        if (method.getOutputType() == null && !function.getFunctionDefinitionNode()
                .functionName().toString().endsWith("Context")) {
            function.addAssignmentStatement(
                getWildcardBindingPatternNode(),
                checkExpressionNode
            );
        } else {
            VariableDeclaration payload = new VariableDeclaration(
                getTypedBindingPatternNode(
                    getBuiltinSimpleNameReferenceNode("var"),
                    getCaptureBindingPatternNode("payload")
                ),
                checkExpressionNode
            );
            function.addVariableStatement(payload.getVariableDeclarationNode());
        }
    }

    public static boolean checkForImportsInServices(List<Method> methodList, String type) {
        for (Method method : methodList) {
            if (isType(method.getInputType(), type) || isType(method.getOutputType(), type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isType(String methodType, String type) {
        return methodType != null && methodType.equals(type);
    }

    public static void addImports(StubFile stubFile, Set<String> ballerinaImports, Set<String> protobufImports) {
        List<Method> methodList = new ArrayList<>();
        for (ServiceStub serviceStub : stubFile.getStubList()) {
            methodList.addAll(serviceStub.getUnaryFunctions());
            methodList.addAll(serviceStub.getClientStreamingFunctions());
            methodList.addAll(serviceStub.getServerStreamingFunctions());
            methodList.addAll(serviceStub.getBidiStreamingFunctions());
        }
        for (String protobufImport : stubFile.getImportList()) {
            switch (protobufImport) {
                case "google/protobuf/wrappers.proto" :
                    if (checkForImportsInServices(methodList, "string") ||
                            checkForImportsInServices(methodList, "int") ||
                            checkForImportsInServices(methodList, "float") ||
                            checkForImportsInServices(methodList, "byte[]") ||
                            checkForImportsInServices(methodList, "boolean")) {
                        protobufImports.add("wrappers");
                    }
                    break;
                case "google/protobuf/timestamp.proto" :
                case "google/protobuf/duration.proto" :
                    ballerinaImports.add("time");
                    break;
                case "google/protobuf/any.proto" :
                    protobufImports.add("'any");
                    break;
                case "google/protobuf/empty.proto" :
                    protobufImports.add("empty");
                    break;
                default:
                    break;
            }
        }
        for (java.util.Map.Entry<String, Message> message : stubFile.getMessageMap().entrySet()) {
            for (Field field : message.getValue().getFieldList()) {
                if (field.getFieldType().equals("int:Signed32") || field.getFieldType().equals("int:Unsigned32")) {
                    ballerinaImports.add("lang.'int");
                }
            }
        }
    }

    public static List<String> removeStandardImports(List<String> importList) {
        List<String> filteredImports = new ArrayList<>();
        for (String importName: importList) {
            if (!STANDARD_IMPORTS.contains(importName)) {
                filteredImports.add(importName);
            }
        }
        return filteredImports;
    }

    public static String getModulePrefix(String contextParam, String filename) {
        if (componentsModuleMap.containsKey(contextParam) && protofileModuleMap.containsKey(filename)) {
            if (!protofileModuleMap.get(filename).equals(componentsModuleMap.get(contextParam))) {
                return componentsModuleMap.get(contextParam).substring(componentsModuleMap.get(contextParam)
                        .lastIndexOf(PACKAGE_SEPARATOR) + 1) + COLON;
            }
        }
        return "";
    }

    public static NodeList<ImportDeclarationNode> addSubModuleImports(List<Method> methodList, String filename,
                                                                      NodeList<ImportDeclarationNode> imports) {
        HashSet<String> importedModules = new HashSet();
        for (Method method: methodList) {
            if (componentsModuleMap.containsKey(method.getInputType())) {
                importedModules.add(componentsModuleMap.get(method.getInputType()));
            }
            if (componentsModuleMap.containsKey(method.getOutputType())) {
                importedModules.add(componentsModuleMap.get(method.getOutputType()));
            }
        }
        for (String type: importedModules.toArray(new String[importedModules.size()])) {
            if (protofileModuleMap.containsKey(filename) && !protofileModuleMap.get(filename).equals(type)) {
                imports = imports.add(Imports.getImportDeclarationNode(type));
            }
        }
        return imports;
    }

    public static NodeList<ImportDeclarationNode> addAnyImportIfExists(List<Method> methods,
                                                                        NodeList<ImportDeclarationNode> imports) {
        if (checkForImportsInServices(methods, "'any:Any")) {
            return imports.add(Imports.getImportDeclarationNode(ORG_NAME, "protobuf.types.'any"));
        }
        return imports;
    }

    public static NodeList<ImportDeclarationNode> addTimeImportsIfExists(List<Method> methods,
                                                                          NodeList<ImportDeclarationNode> imports) {
        if (checkForImportsInServices(methods, "time:Utc") || checkForImportsInServices(methods, "time:Seconds")) {
            return imports.add(Imports.getImportDeclarationNode(ORG_NAME, "time"));
        }
        return imports;
    }
}
