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

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.BinaryExpressionNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.protoc.core.builder.stub.EnumMessage;
import io.ballerina.protoc.core.builder.stub.Field;
import io.ballerina.protoc.core.builder.stub.Message;
import io.ballerina.protoc.core.builder.syntaxtree.components.Annotation;
import io.ballerina.protoc.core.builder.syntaxtree.components.Function;
import io.ballerina.protoc.core.builder.syntaxtree.components.IfElse;
import io.ballerina.protoc.core.builder.syntaxtree.components.Record;
import io.ballerina.protoc.core.builder.syntaxtree.components.Type;
import io.ballerina.protoc.core.builder.syntaxtree.components.VariableDeclaration;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.ballerina.protoc.core.GrpcConstants.ANN_DESCRIPTOR;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getBinaryExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getFieldAccessExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getMethodCallExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getOptionalFieldAccessExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getUnaryTypeTestExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Literal.getBooleanLiteralNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Literal.getNumericLiteralNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Statement.getCompoundAssignmentStatementNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Statement.getReturnStatementNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getBuiltinSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getCaptureBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getNilTypeDescriptorNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypedBindingPatternNode;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.capitalizeFirstLetter;
import static io.ballerina.protoc.core.builder.syntaxtree.utils.CommonUtils.toPascalCase;

/**
 * Utility functions related to Message.
 *
 @since 0.1.0
 */
public class MessageUtils {

    private MessageUtils() {

    }

    public static NodeList<ModuleMemberDeclarationNode> getMessageNodes(Message message, String descriptorName,
                                                                        String filename) {
        NodeList<ModuleMemberDeclarationNode> messageMembers = AbstractNodeFactory.createEmptyNodeList();
        NodeList<AnnotationNode> annotationNodes = AbstractNodeFactory.createEmptyNodeList();

        Annotation protobufDescriptor = new Annotation("protobuf", ANN_DESCRIPTOR);
        protobufDescriptor.addField(
                "value",
                descriptorName
        );
        annotationNodes = annotationNodes.add(protobufDescriptor.getAnnotationNode());
        MetadataNode metadataNode = NodeFactory.createMetadataNode(null, annotationNodes);
        messageMembers = messageMembers.add(getMessageType(message, filename).getTypeDefinitionNode(metadataNode));

        if (message.getNestedMessageList() != null) {
            for (Message nestedMessage : message.getNestedMessageList()) {
                for (ModuleMemberDeclarationNode messageNode : getMessageNodes(nestedMessage, descriptorName,
                        filename)) {
                    messageMembers = messageMembers.add(messageNode);
                }
            }
        }
        if (message.getOneofFieldMap() != null) {
            messageMembers = messageMembers.add(getValidationFunction(message).getFunctionDefinitionNode());
            for (Map.Entry<String, List<Field>> oneOfFieldMap : message.getOneofFieldMap().entrySet()) {
                for (Field field : oneOfFieldMap.getValue()) {
                    messageMembers = messageMembers.add(
                            getOneOfFieldSetFunction(
                                    message.getMessageName(),
                                    field,
                                    oneOfFieldMap.getValue()
                            ).getFunctionDefinitionNode()
                    );
                }
            }
        }
        if (message.getEnumList() != null) {
            for (EnumMessage enumMessage : message.getEnumList()) {
                messageMembers = messageMembers.add(EnumUtils.getEnum(enumMessage).getEnumDeclarationNode());
            }
        }
        return messageMembers;
    }

    private static Type getMessageType(Message message, String filename) {
        Record messageRecord = new Record();
        for (Field field : message.getFieldList()) {
            if (field.getFieldLabel() == null) {
                switch (field.getFieldType()) {
                    case "string":
                    case "int":
                    case "float":
                    case "boolean":
                        messageRecord.addBasicFieldWithDefaultValue(field.getFieldType(), field.getFieldName(),
                                field.getDefaultValue(filename));
                        break;
                    case "byte[]":
                        messageRecord.addArrayFieldWithDefaultValue("byte", field.getFieldName());
                        break;
                    case "Timestamp":
                        messageRecord.addCustomFieldWithDefaultValue("time:Utc", field.getFieldName(), "[0, 0.0d]");
                        break;
                    case "Duration":
                        messageRecord.addCustomFieldWithDefaultValue("time:Seconds", field.getFieldName(), "0.0d");
                        break;
                    case "Struct":
                        messageRecord.addMapFieldWithDefaultValue(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                                field.getFieldName());
                        break;
                    case "'any:Any":
                        messageRecord.addCustomFieldWithDefaultValue("'any:Any", field.getFieldName(), "{}");
                        break;
                    default:
                        messageRecord.addCustomFieldWithDefaultValue(field.getPackageName(filename) +
                                        field.getFieldType(), field.getFieldName(), field.getDefaultValue(filename));
                }
            } else {
                switch (field.getFieldType()) {
                    case "byte[]":
                        messageRecord.addArrayFieldWithDefaultValue("byte", field.getFieldName());
                        break;
                    case "Timestamp":
                        messageRecord.addArrayFieldWithDefaultValue("time:Utc", field.getFieldName());
                        break;
                    case "Duration":
                        messageRecord.addArrayFieldWithDefaultValue("time:Seconds", field.getFieldName());
                        break;
                    case "Struct":
                        messageRecord.addArrayFieldWithDefaultValue("map<anydata>", field.getFieldName());
                        break;
                    case "'any:Any":
                        messageRecord.addArrayFieldWithDefaultValue("'any:Any", field.getFieldName());
                        break;
                    case "string":
                    case "int":
                    case "float":
                    case "boolean":
                    default:
                        messageRecord.addArrayFieldWithDefaultValue(field.getPackageName(filename) +
                                field.getFieldType(), field.getFieldName());
                }
            }
        }
        if (message.getOneofFieldMap() != null) {
            for (Map.Entry<String, List<Field>> oneOfField : message.getOneofFieldMap().entrySet()) {
                for (Field field : oneOfField.getValue()) {
                    switch (field.getFieldType()) {
                        case "string":
                        case "int":
                        case "float":
                        case "boolean":
                            if (field.getFieldLabel() == null) {
                                messageRecord.addOptionalBasicField(field.getFieldType(), field.getFieldName());
                            } else {
                                messageRecord.addOptionalArrayField(field.getFieldType(), field.getFieldName());
                            }
                            break;
                        case "byte[]":
                            messageRecord.addOptionalArrayField("byte", field.getFieldName());
                            break;
                        case "Timestamp":
                            messageRecord.addOptionalCustomField("time:Utc", field.getFieldName());
                            break;
                        case "Duration":
                            messageRecord.addOptionalCustomField("time:Seconds", field.getFieldName());
                            break;
                        case "Struct":
                            messageRecord.addOptionalMapField(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                                    field.getFieldName());
                            break;
                        default:
                            if (field.getFieldLabel() == null) {
                                messageRecord.addOptionalCustomField(field.getPackageName(filename) +
                                        field.getFieldType(), field.getFieldName());
                            } else {
                                messageRecord.addOptionalArrayField(field.getPackageName(filename) +
                                        field.getFieldType(), field.getFieldName());
                            }
                    }
                }
            }
        }
        if (message.getMapList() != null) {
            for (Message map : message.getMapList()) {
                Record record = new Record();
                for (Field field : map.getFieldList()) {
                    // Todo: Add a test case with all the field types (int32, int64 ...)
                    record.addBasicField(field.getPackageName(filename) + field.getFieldType(),
                            field.getFieldName());
                }
                messageRecord.addArrayFieldWithDefaultValue(record, map.getMessageName());
            }
        }
        return new Type(
                true,
                message.getMessageName(),
                messageRecord.getRecordTypeDescriptorNode());
    }

    private static Function getValidationFunction(Message message) {
        Function function = new Function("isValid" + capitalizeFirstLetter(message.getMessageName()));
        function.addReturns(getBuiltinSimpleNameReferenceNode("boolean"));
        function.addRequiredParameter(getSimpleNameReferenceNode(message.getMessageName()), "r");
        ArrayList<String> counts = new ArrayList<>();
        for (Map.Entry<String, List<Field>> oneOfFieldMap : message.getOneofFieldMap().entrySet()) {
            counts.add(oneOfFieldMap.getKey() + "Count");
            VariableDeclaration count = new VariableDeclaration(
                    getTypedBindingPatternNode(
                            SyntaxTreeConstants.SYNTAX_TREE_VAR_INT,
                            getCaptureBindingPatternNode(oneOfFieldMap.getKey() + "Count")
                    ),
                    getNumericLiteralNode(0)
            );
            function.addVariableStatement(count.getVariableDeclarationNode());
            for (Field field : oneOfFieldMap.getValue()) {
                IfElse oneOfFieldCheck = new IfElse(
                    getUnaryTypeTestExpressionNode(
                            getOptionalFieldAccessExpressionNode("r", field.getFieldName()),
                            getNilTypeDescriptorNode()
                    )
                );
                oneOfFieldCheck.addIfStatement(
                        getCompoundAssignmentStatementNode(
                                oneOfFieldMap.getKey() + "Count",
                                SyntaxTreeConstants.SYNTAX_TREE_OPERATOR_PLUS,
                                1
                        )
                );
                function.addIfElseStatement(oneOfFieldCheck.getIfElseStatementNode());
            }
        }
        if (counts.size() > 0) {
            IfElse countCheck = new IfElse(
                    getCountCheckBinaryExpression(counts)
            );
            countCheck.addIfStatement(
                    getReturnStatementNode(
                            getBooleanLiteralNode(false)
                    )
            );
            function.addIfElseStatement(countCheck.getIfElseStatementNode());
        }
        function.addReturnStatement(
                getBooleanLiteralNode(true)
        );
        function.addQualifiers(new String[]{"isolated"});
        return function;
    }

    private static BinaryExpressionNode getCountCheckBinaryExpression(ArrayList<String> counts) {
        BinaryExpressionNode binaryExpressionNode = getBinaryExpressionNode(
                getSimpleNameReferenceNode(counts.get(0)),
                getNumericLiteralNode(1),
                SyntaxTreeConstants.SYNTAX_TREE_OPERATOR_GREATER_THAN
        );
        for (int i = 1; i < counts.size(); i++) {
            BinaryExpressionNode rhs = getBinaryExpressionNode(
                    getSimpleNameReferenceNode(counts.get(i)),
                    getNumericLiteralNode(1),
                    SyntaxTreeConstants.SYNTAX_TREE_OPERATOR_GREATER_THAN
            );
            binaryExpressionNode = getBinaryExpressionNode(
                    binaryExpressionNode,
                    rhs,
                    SyntaxTreeConstants.SYNTAX_TREE_OPERATOR_OR
            );
        }
        return binaryExpressionNode;
    }

    private static Function getOneOfFieldSetFunction(String messageName, Field field, List<Field> fields) {
        Function function = new Function("set" + messageName + "_" + toPascalCase(field.getFieldName()));
        function.addRequiredParameter(getSimpleNameReferenceNode(messageName), "r");
        function.addRequiredParameter(
                getBuiltinSimpleNameReferenceNode(field.getFieldType()),
                field.getFieldName()
        );
        function.addAssignmentStatement(
                getFieldAccessExpressionNode(
                        "r",
                        field.getFieldName()
                ),
                getSimpleNameReferenceNode(field.getFieldName())
        );
        for (Field oneOfField : fields) {
            if (!oneOfField.getFieldName().equals(field.getFieldName())) {
                function.addAssignmentStatement(
                        getSimpleNameReferenceNode("_"),
                        getMethodCallExpressionNode(
                                getSimpleNameReferenceNode("r"),
                                "removeIfHasKey",
                                "\"" + oneOfField.getFieldName().replaceAll("'", "") + "\"")
                );
            }
        }
        function.addQualifiers(new String[]{"isolated"});
        return function;
    }
}
