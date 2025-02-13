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
import io.ballerina.compiler.syntax.tree.AssignmentStatementNode;
import io.ballerina.compiler.syntax.tree.CompoundAssignmentStatementNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.ExpressionStatementNode;
import io.ballerina.compiler.syntax.tree.FunctionCallExpressionNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.ReturnStatementNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.protoc.core.builder.syntaxtree.constants.SyntaxTreeConstants;

import java.util.ArrayList;

import static io.ballerina.protoc.core.builder.syntaxtree.components.Literal.getNumericLiteralNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getQualifiedNameReferenceNode;
import static io.ballerina.protoc.core.builder.syntaxtree.components.TypeDescriptor.getSimpleNameReferenceNode;

/**
 * Class representing different types of StatementNodes.
 *
 @since 0.1.0
 */
public class Statement {

    private Statement() {

    }

    public static AssignmentStatementNode getAssignmentStatementNode(String varRef, ExpressionNode expression) {
        return NodeFactory.createAssignmentStatementNode(
                getSimpleNameReferenceNode(varRef),
                SyntaxTreeConstants.SYNTAX_TREE_EQUAL,
                expression,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static CompoundAssignmentStatementNode getCompoundAssignmentStatementNode(String lhs, Token binaryOperator,
                                                                                     int value) {
        return NodeFactory.createCompoundAssignmentStatementNode(
                getSimpleNameReferenceNode(lhs),
                binaryOperator,
                SyntaxTreeConstants.SYNTAX_TREE_EQUAL,
                getNumericLiteralNode(value),
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static ReturnStatementNode getReturnStatementNode(ExpressionNode expression) {
        return NodeFactory.createReturnStatementNode(
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_RETURN,
                expression,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static ExpressionStatementNode getCallStatementNode(ExpressionNode expression) {
        return NodeFactory.createExpressionStatementNode(
                SyntaxKind.CALL_STATEMENT,
                expression,
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static FunctionCallExpressionNode getFunctionCallExpressionNode(String modulePrefix, String identifier,
                                                                           String... args) {
        ArrayList<Node> argNodeList = new ArrayList<>();
        for (String arg : args) {
            argNodeList.add(NodeFactory.createPositionalArgumentNode(getSimpleNameReferenceNode(arg)));
        }
        return NodeFactory.createFunctionCallExpressionNode(
                getQualifiedNameReferenceNode(modulePrefix, identifier), SyntaxTreeConstants.SYNTAX_TREE_OPEN_PAREN,
                AbstractNodeFactory.createSeparatedNodeList(argNodeList), SyntaxTreeConstants.SYNTAX_TREE_CLOSE_PAREN);
    }
}
