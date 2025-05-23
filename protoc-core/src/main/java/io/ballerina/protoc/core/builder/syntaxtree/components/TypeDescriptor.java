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

package io.ballerina.protoc.core.builder.syntaxtree.components;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ArrayDimensionNode;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BindingPatternNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.CaptureBindingPatternNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.ListBindingPatternNode;
import io.ballerina.compiler.syntax.tree.MapTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.NilTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.ObjectFieldNode;
import io.ballerina.compiler.syntax.tree.OptionalTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.ParameterizedTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.ReturnTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.StreamTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TupleTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.TypeCastExpressionNode;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.TypeReferenceNode;
import io.ballerina.compiler.syntax.tree.TypedBindingPatternNode;
import io.ballerina.compiler.syntax.tree.UnionTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.WildcardBindingPatternNode;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing different types of TypeDescriptorNodes.
 *
 @since 0.1.0
 */
public class TypeDescriptor {

    private TypeDescriptor() {

    }

    public static QualifiedNameReferenceNode getQualifiedNameReferenceNode(String modulePrefix, String identifier) {
        return NodeFactory.createQualifiedNameReferenceNode(
                AbstractNodeFactory.createIdentifierToken(modulePrefix),
                SyntaxTreeConstants.SYNTAX_TREE_COLON,
                AbstractNodeFactory.createIdentifierToken(identifier + " ")
        );
    }

    public static OptionalTypeDescriptorNode getOptionalTypeDescriptorNode(String modulePrefix, String identifier) {
        if (modulePrefix.isEmpty()) {
            return NodeFactory.createOptionalTypeDescriptorNode(
                    getSimpleNameReferenceNode(identifier),
                    SyntaxTreeConstants.SYNTAX_TREE_QUESTION_MARK
            );
        }
        return NodeFactory.createOptionalTypeDescriptorNode(
                getQualifiedNameReferenceNode(modulePrefix, identifier),
                SyntaxTreeConstants.SYNTAX_TREE_QUESTION_MARK
        );
    }

    public static UnionTypeDescriptorNode getUnionTypeDescriptorNode(TypeDescriptorNode lhs, TypeDescriptorNode rhs) {
        return NodeFactory.createUnionTypeDescriptorNode(
                lhs,
                SyntaxTreeConstants.SYNTAX_TREE_PIPE,
                rhs
        );
    }

    public static BuiltinSimpleNameReferenceNode getBuiltinSimpleNameReferenceNode(String name) {
        SyntaxKind kind;
        switch (name) {
            case "int":
                kind = SyntaxKind.INT_TYPE_DESC;
                break;
            case "float":
                kind = SyntaxKind.DECIMAL_TYPE_DESC;
                break;
            case "boolean":
                kind = SyntaxKind.BOOLEAN_TYPE_DESC;
                break;
            case "byte":
                kind = SyntaxKind.BYTE_TYPE_DESC;
                break;
            case "var":
                kind = SyntaxKind.VAR_TYPE_DESC;
                break;
            default:
                kind = SyntaxKind.STRING_TYPE_DESC;
        }
        return NodeFactory.createBuiltinSimpleNameReferenceNode(
                kind,
                AbstractNodeFactory.createIdentifierToken(name + " ")
        );
    }

    public static ParameterizedTypeDescriptorNode getErrorTypeDescriptorNode() {
        return NodeFactory.createParameterizedTypeDescriptorNode(
                SyntaxKind.ERROR_TYPE_DESC,
                AbstractNodeFactory.createIdentifierToken("error"),
                null
        );
    }

    public static ArrayTypeDescriptorNode getArrayTypeDescriptorNode(String type) {
        ArrayDimensionNode arrayDimensionNode = NodeFactory.createArrayDimensionNode(
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACKET,
                null,
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACKET
        );
        NodeList<ArrayDimensionNode> dimensionList = NodeFactory.createNodeList(arrayDimensionNode);
        return NodeFactory.createArrayTypeDescriptorNode(
                getBuiltinSimpleNameReferenceNode(type),
                dimensionList
        );
    }

    public static ArrayTypeDescriptorNode getArrayTypeDescriptorNode(Record type) {
        ArrayDimensionNode arrayDimensionNode = NodeFactory.createArrayDimensionNode(
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACKET,
                null,
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACKET
        );
        NodeList<ArrayDimensionNode> dimensionList = NodeFactory.createNodeList(arrayDimensionNode);
        return NodeFactory.createArrayTypeDescriptorNode(
                type.getRecordTypeDescriptorNode(),
                dimensionList
        );
    }

    public static TypeReferenceNode getTypeReferenceNode(Node typeName) {
        return NodeFactory.createTypeReferenceNode(
                SyntaxTreeConstants.SYNTAX_TREE_ASTERISK,
                typeName,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static ObjectFieldNode getObjectFieldNode(String visibility, String[] qualifiers, Node typeName,
                                                     String fieldName) {
        NodeList<Token> qualifierList = NodeFactory.createEmptyNodeList();
        for (String qualifier : qualifiers) {
            qualifierList = qualifierList.add(AbstractNodeFactory.createIdentifierToken(qualifier + " "));
        }
        return NodeFactory.createObjectFieldNode(
                null,
                AbstractNodeFactory.createIdentifierToken("\n" + visibility + " "),
                qualifierList,
                typeName,
                AbstractNodeFactory.createIdentifierToken(fieldName),
                null,
                null,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static StreamTypeDescriptorNode getStreamTypeDescriptorNode(Node lhs, Node rhs) {
        Token comma;
        if (rhs == null) {
            comma = null;
        } else {
            comma = SyntaxTreeConstants.SYNTAX_TREE_COMMA;
        }
        return NodeFactory.createStreamTypeDescriptorNode(
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_STREAM,
                NodeFactory.createStreamTypeParamsNode(
                        SyntaxTreeConstants.SYNTAX_TREE_IT,
                        lhs,
                        comma,
                        rhs,
                        SyntaxTreeConstants.SYNTAX_TREE_GT
                )
        );
    }

    public static CaptureBindingPatternNode getCaptureBindingPatternNode(String name) {
        return NodeFactory.createCaptureBindingPatternNode(
                AbstractNodeFactory.createIdentifierToken(name)
        );
    }

    public static WildcardBindingPatternNode getWildcardBindingPatternNode() {
        return NodeFactory.createWildcardBindingPatternNode(
                SyntaxTreeConstants.SYNTAX_TREE_UNDERSCORE
        );
    }

    public static TypedBindingPatternNode getTypedBindingPatternNode(TypeDescriptorNode typeDescriptorNode,
                                                                     BindingPatternNode bindingPatternNode) {
        return NodeFactory.createTypedBindingPatternNode(
                typeDescriptorNode,
                bindingPatternNode
        );
    }

    public static TupleTypeDescriptorNode getTupleTypeDescriptorNode(SeparatedNodeList<Node> memberTypeDesc) {
        return NodeFactory.createTupleTypeDescriptorNode(
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACKET,
                memberTypeDesc,
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACKET
        );
    }

    public static ListBindingPatternNode getListBindingPatternNode(String[] bindingPatterns) {
        List<Node> bindingPatterNodes = new ArrayList<>();
        for (String bindingPattern : bindingPatterns) {
            if (bindingPatterNodes.size() > 0) {
                bindingPatterNodes.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
            }
            if (bindingPattern.equals("_")) {
                bindingPatterNodes.add(getWildcardBindingPatternNode());
            } else {
                bindingPatterNodes.add(getCaptureBindingPatternNode(bindingPattern));
            }
        }
        return NodeFactory.createListBindingPatternNode(
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACKET,
                NodeFactory.createSeparatedNodeList(bindingPatterNodes),
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACKET
        );
    }

    public static TypeCastExpressionNode getTypeCastExpressionNode(String typeCastParam, ExpressionNode expression) {
        return NodeFactory.createTypeCastExpressionNode(
                SyntaxTreeConstants.SYNTAX_TREE_IT,
                NodeFactory.createTypeCastParamNode(
                        NodeFactory.createEmptyNodeList(),
                        getBuiltinSimpleNameReferenceNode(typeCastParam)
                ),
                SyntaxTreeConstants.SYNTAX_TREE_GT,
                expression
        );
    }

    public static ReturnTypeDescriptorNode getReturnTypeDescriptorNode(Node type) {
        NodeList<AnnotationNode> annotations = NodeFactory.createEmptyNodeList();
        return NodeFactory.createReturnTypeDescriptorNode(
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_RETURNS,
                annotations,
                type
        );
    }

    public static NilTypeDescriptorNode getNilTypeDescriptorNode() {
        return NodeFactory.createNilTypeDescriptorNode(
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_PAREN,
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_PAREN
        );
    }

    public static SimpleNameReferenceNode getSimpleNameReferenceNode(String name) {
        return NodeFactory.createSimpleNameReferenceNode(AbstractNodeFactory.createIdentifierToken(name + " "));
    }

    public static MapTypeDescriptorNode getMapTypeDescriptorNode(TypeDescriptorNode descriptorNode) {
        return NodeFactory.createMapTypeDescriptorNode(
                AbstractNodeFactory.createIdentifierToken("map"),
                NodeFactory.createTypeParameterNode(
                        SyntaxTreeConstants.SYNTAX_TREE_IT,
                        descriptorNode,
                        SyntaxTreeConstants.SYNTAX_TREE_GT
                )
        );
    }
}
