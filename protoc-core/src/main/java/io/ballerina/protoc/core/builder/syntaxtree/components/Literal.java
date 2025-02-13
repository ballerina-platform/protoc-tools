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
import io.ballerina.compiler.syntax.tree.BasicLiteralNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.SyntaxKind;

/**
 * Class representing different types of BasicLiteralNodes.
 *
 @since 0.1.0
 */
public class Literal {

    private Literal() {

    }

    public static BasicLiteralNode getNumericLiteralNode(int value) {
        return NodeFactory.createBasicLiteralNode(
                SyntaxKind.NUMERIC_LITERAL,
                NodeFactory.createLiteralValueToken(
                        SyntaxKind.DECIMAL_INTEGER_LITERAL_TOKEN,
                        String.valueOf(value), NodeFactory.createEmptyMinutiaeList(),
                        NodeFactory.createEmptyMinutiaeList()
                )
        );
    }

    public static BasicLiteralNode getNumericLiteralNode(float value) {
        return NodeFactory.createBasicLiteralNode(
                SyntaxKind.NUMERIC_LITERAL,
                NodeFactory.createLiteralValueToken(
                        SyntaxKind.DECIMAL_FLOATING_POINT_LITERAL_TOKEN,
                        String.valueOf(value), NodeFactory.createEmptyMinutiaeList(),
                        NodeFactory.createEmptyMinutiaeList()
                )
        );
    }

    public static BasicLiteralNode getDecimalLiteralNode(String value) {
        return NodeFactory.createBasicLiteralNode(
                SyntaxKind.DECIMAL_FLOATING_POINT_LITERAL_TOKEN,
                AbstractNodeFactory.createIdentifierToken(value)
        );
    }

    public static BasicLiteralNode getStringLiteralNode(String value) {
        return NodeFactory.createBasicLiteralNode(
                SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createIdentifierToken("\"" + value + "\"")
        );
    }

    public static BasicLiteralNode getByteArrayLiteralNode(String bytes) {
        return NodeFactory.createBasicLiteralNode(
                SyntaxKind.BYTE_ARRAY_LITERAL,
                AbstractNodeFactory.createIdentifierToken(bytes)
        );
    }

    public static BasicLiteralNode getTupleLiteralNode(String tuple) {
        return NodeFactory.createBasicLiteralNode(
                SyntaxKind.ARRAY_TYPE_DESC,
                AbstractNodeFactory.createIdentifierToken(tuple)
        );
    }

    public static BasicLiteralNode getBooleanLiteralNode(boolean value) {
        if (value) {
            return NodeFactory.createBasicLiteralNode(
                    SyntaxKind.BOOLEAN_LITERAL,
                    NodeFactory.createToken(
                            SyntaxKind.TRUE_KEYWORD,
                            NodeFactory.createEmptyMinutiaeList(),
                            NodeFactory.createEmptyMinutiaeList()
                    )
            );
        } else {
            return NodeFactory.createBasicLiteralNode(
                    SyntaxKind.BOOLEAN_LITERAL,
                    NodeFactory.createToken(
                            SyntaxKind.FALSE_KEYWORD,
                            NodeFactory.createEmptyMinutiaeList(),
                            NodeFactory.createEmptyMinutiaeList()
                    )
            );
        }
    }

    public static BasicLiteralNode createBasicLiteralNode(SyntaxKind syntaxKind, String value) {
        return NodeFactory.createBasicLiteralNode(syntaxKind, AbstractNodeFactory.createIdentifierToken(value));
    }
}
