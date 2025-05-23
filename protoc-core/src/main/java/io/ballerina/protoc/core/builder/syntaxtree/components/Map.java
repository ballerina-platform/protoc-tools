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

import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import java.util.ArrayList;
import java.util.List;

import static io.ballerina.protoc.core.builder.syntaxtree.components.Expression.getMethodCallExpressionNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.Literal.getStringLiteralNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getTypeCastExpressionNode;

/**
 * Class representing MappingConstructorExpressionNode.
 *
 @since 0.1.0
 */
public class Map {

    private final List<Node> fields;

    public Map() {
        fields = new ArrayList<>();
    }

    public MappingConstructorExpressionNode getMappingConstructorExpressionNode() {
        return NodeFactory.createMappingConstructorExpressionNode(
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACE,
                NodeFactory.createSeparatedNodeList(fields),
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACE
        );
    }

    public void addField(String fieldName, ExpressionNode field) {
        if (fields.size() > 0) {
            fields.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
        }
        fields.add(
                NodeFactory.createSpecificFieldNode(
                        null,
                        NodeFactory.createIdentifierToken(fieldName),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        field
                )
        );
    }

    public void addStringField(String key, String value) {
        if (fields.size() > 0) {
            fields.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
        }
        fields.add(
                NodeFactory.createSpecificFieldNode(
                        null,
                        getStringLiteralNode(key),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        getStringLiteralNode(value)
                )
        );
    }

    public void addMethodCallField(String key, ExpressionNode expression, String methodName, String... args) {
        if (fields.size() > 0) {
            fields.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
        }
        fields.add(
                NodeFactory.createSpecificFieldNode(
                        null,
                        NodeFactory.createIdentifierToken(key),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        getMethodCallExpressionNode(expression, methodName, args))
        );
    }

    public void addSimpleNameReferenceField(String key, String value) {
        if (fields.size() > 0) {
            fields.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
        }
        fields.add(
                NodeFactory.createSpecificFieldNode(
                        null,
                        NodeFactory.createIdentifierToken(key),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        getSimpleNameReferenceNode(value))
        );
    }

    public void addTypeCastExpressionField(String fieldName, String typeCastParam, ExpressionNode expression) {
        if (fields.size() > 0) {
            fields.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
        }
        fields.add(
                NodeFactory.createSpecificFieldNode(
                        null,
                        NodeFactory.createIdentifierToken(fieldName),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        getTypeCastExpressionNode(typeCastParam, expression)
                )
        );
    }
}
