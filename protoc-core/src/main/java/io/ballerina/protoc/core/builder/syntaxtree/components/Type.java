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
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

/**
 * Class representing TypeDefinitionNode.
 *
 @since 0.1.0
 */
public class Type {

    private Token visibilityQualifier;
    private final Token typeName;
    private final Node typeDescriptor;

    public Type(boolean isPublic, String name, Node descriptor) {
        if (isPublic) {
            visibilityQualifier = AbstractNodeFactory.createIdentifierToken("public ");
        }
        typeName = AbstractNodeFactory.createIdentifierToken(name + " ");
        typeDescriptor = descriptor;
    }

    public TypeDefinitionNode getTypeDefinitionNode(MetadataNode metadataNode) {
        return NodeFactory.createTypeDefinitionNode(
                metadataNode,
                visibilityQualifier,
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_TYPE,
                typeName,
                typeDescriptor,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON

        );
    }
}
