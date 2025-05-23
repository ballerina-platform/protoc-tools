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
import io.ballerina.compiler.syntax.tree.ImplicitNewExpressionNode;
import io.ballerina.compiler.syntax.tree.ListenerDeclarationNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

/**
 * Class representing ListenerDeclarationNode.
 *
 @since 0.1.0
 */
public class Listener {

    private final MetadataNode metadata;
    private Token visibilityQualifier;
    private final TypeDescriptorNode typeDescriptor;
    private final Token name;
    private final ImplicitNewExpressionNode initializer;

    public Listener(String name, TypeDescriptorNode typeDescriptor, ImplicitNewExpressionNode initializer,
                    boolean isPublic) {
        metadata = null;
        if (isPublic) {
            visibilityQualifier = AbstractNodeFactory.createIdentifierToken("public ");
        }
        this.name = AbstractNodeFactory.createIdentifierToken(name + " ");
        this.typeDescriptor = typeDescriptor;
        this.initializer = initializer;
    }

    public ListenerDeclarationNode getListenerDeclarationNode() {
        return NodeFactory.createListenerDeclarationNode(
                metadata,
                visibilityQualifier,
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_LISTENER,
                typeDescriptor,
                name,
                SyntaxTreeConstants.SYNTAX_TREE_EQUAL,
                initializer,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }
}
