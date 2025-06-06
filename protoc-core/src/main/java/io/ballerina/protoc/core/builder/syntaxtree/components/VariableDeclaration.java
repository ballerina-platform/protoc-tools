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

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypedBindingPatternNode;
import io.ballerina.compiler.syntax.tree.VariableDeclarationNode;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

/**
 * Class representing VariableDeclarationNode.
 *
 @since 0.1.0
 */
public class VariableDeclaration {

    private final NodeList<AnnotationNode> annotations;
    private final Token finalKeyWord;
    private final TypedBindingPatternNode patternNode;
    private final ExpressionNode initializer;

    public VariableDeclaration(TypedBindingPatternNode patternNode, ExpressionNode initializer) {
        annotations = NodeFactory.createEmptyNodeList();
        finalKeyWord = null;
        this.patternNode = patternNode;
        this.initializer = initializer;
    }

    public VariableDeclarationNode getVariableDeclarationNode() {
        Token equals;
        if (initializer == null) {
            equals = null;
        } else {
            equals = SyntaxTreeConstants.SYNTAX_TREE_EQUAL;
        }
        return NodeFactory.createVariableDeclarationNode(
                annotations,
                finalKeyWord,
                patternNode,
                equals,
                initializer,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }
}
