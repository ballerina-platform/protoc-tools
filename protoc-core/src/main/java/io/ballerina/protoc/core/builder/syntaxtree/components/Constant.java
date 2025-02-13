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
import io.ballerina.compiler.syntax.tree.ConstantDeclarationNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getBuiltinSimpleNameReferenceNode;

/**
 * Class representing ConstantDeclarationNode.
 *
 @since 0.1.0
 */
public class Constant {

    private final Token constKeyWord = AbstractNodeFactory.createIdentifierToken("const ");
    private final TypeDescriptorNode typeDescriptor;
    private final Token variableName;
    private final ExpressionNode initializer;
    private final Token visibility;

    public Constant(String visibility, String type, String name, ExpressionNode value) {
        this.visibility = AbstractNodeFactory.createIdentifierToken(visibility);
        typeDescriptor = getBuiltinSimpleNameReferenceNode(type);
        variableName = AbstractNodeFactory.createIdentifierToken(name);
        initializer = value;
    }

    public Constant(String visibility, TypeDescriptorNode type, String name, ExpressionNode value) {
        this.visibility = AbstractNodeFactory.createIdentifierToken(visibility);
        typeDescriptor = type;
        variableName = AbstractNodeFactory.createIdentifierToken(name);
        initializer = value;
    }

    public ConstantDeclarationNode getConstantDeclarationNode() {
        return NodeFactory.createConstantDeclarationNode(
                null,
                visibility,
                constKeyWord,
                typeDescriptor,
                variableName,
                SyntaxTreeConstants.SYNTAX_TREE_EQUAL,
                initializer,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }
}
