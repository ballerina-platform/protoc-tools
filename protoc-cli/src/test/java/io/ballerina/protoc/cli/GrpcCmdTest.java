/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package io.ballerina.protoc.cli;

import org.testng.Assert;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * This class is used to test the functionality of the gRPC command exit codes.
 */
public class GrpcCmdTest {

    @Test(description = "Test grpc command execution without arguments - should return exit code 2")
    public void testExecuteWithoutArguments() {
        String[] args = {};
        ExitCodeCaptor exitCaptor = new ExitCodeCaptor();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);
        GrpcCmd grpcCmd = new GrpcCmd(printStream, exitCaptor);
        new CommandLine(grpcCmd).parseArgs(args);
        grpcCmd.execute();
        Assert.assertEquals(exitCaptor.getExitCode(), 2,
                "grpc command without arguments should exit with code 2");
    }

    @Test(description = "Test grpc command execution with help flag - should return exit code 0")
    public void testExecuteWithHelpFlag() {
        String[] args = {"-h"};
        ExitCodeCaptor exitCaptor = new ExitCodeCaptor();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);
        GrpcCmd grpcCmd = new GrpcCmd(printStream, exitCaptor);
        new CommandLine(grpcCmd).parseArgs(args);
        grpcCmd.execute();
        Assert.assertEquals(exitCaptor.getExitCode(), 0,
                "grpc command with -h flag should exit with code 0");
    }

    @Test(description = "Test grpc command execution with invalid flag - should throw exception during parsing")
    public void testExecuteWithInvalidFlag() {
        String[] args = {"--invalidFlag"};
        ExitCodeCaptor exitCaptor = new ExitCodeCaptor();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);
        GrpcCmd grpcCmd = new GrpcCmd(printStream, exitCaptor);
        try {
            new CommandLine(grpcCmd).parseArgs(args);
            Assert.fail("Expected picocli to throw exception for invalid flag");
        } catch (CommandLine.UnmatchedArgumentException e) {
            // Expected: picocli rejects invalid flags
            Assert.assertTrue(e.getMessage().contains("Unknown option"));
        }
    }
}
