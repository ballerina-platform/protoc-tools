/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.protoc.tools;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.protoc.tools.ToolingTestUtils.BALLERINA_TOML_FILE;
import static io.ballerina.protoc.tools.ToolingTestUtils.BAL_FILE_DIRECTORY;
import static io.ballerina.protoc.tools.ToolingTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.protoc.tools.ToolingTestUtils.PROTO_FILE_DIRECTORY;
import static io.ballerina.protoc.tools.ToolingTestUtils.RESOURCE_DIRECTORY;
import static io.ballerina.protoc.tools.ToolingTestUtils.assertGeneratedDataTypeSources;
import static io.ballerina.protoc.tools.ToolingTestUtils.assertGeneratedDataTypeSourcesNegative;
import static io.ballerina.protoc.tools.ToolingTestUtils.assertGeneratedSources;
import static io.ballerina.protoc.tools.ToolingTestUtils.assertGeneratedSourcesWithNestedDirectories;
import static io.ballerina.protoc.tools.ToolingTestUtils.copyBallerinaToml;
import static io.ballerina.protoc.tools.ToolingTestUtils.generateSourceCode;
import static io.ballerina.protoc.tools.ToolingTestUtils.getHelpText;
import static io.ballerina.protoc.tools.ToolingTestUtils.hasSemanticDiagnostics;
import static io.ballerina.protoc.tools.ToolingTestUtils.hasSyntacticDiagnostics;
import static io.ballerina.protoc.tools.ToolingTestUtils.readContent;

/**
 * gRPC tool common tests.
 */
public class ToolingCommonTest {

    @Test
    public void testHelloWorldWithDependency() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithDependency.proto",
                "helloWorldWithDependency_pb.bal", "tool_test_data_type_1");
    }

    @Test
    public void testCommandWithoutArguments() {
        // Capture output using ByteArrayOutputStream with explicit encoding
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        java.io.PrintStream printStream = new java.io.PrintStream(outputStream, true,
                java.nio.charset.StandardCharsets.UTF_8);

        // Create a GrpcCmd instance with the captured print stream
        io.ballerina.protoc.cli.GrpcCmd grpcCmd = new io.ballerina.protoc.cli.GrpcCmd(printStream);

        try {
            grpcCmd.execute();
            String actualOutput = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8).trim();
            String expectedOutput = getHelpText();
            Assert.assertEquals(actualOutput, expectedOutput,
                    "Expected help text to be displayed when no arguments are provided");
        } catch (Exception e) {
            Assert.fail("Command should not throw exception when no arguments are provided, but should show help " +
                    "instead. Exception: " + e.getMessage());
        }
    }

    @Test
    public void testCommandWithHelpFlag() {
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        java.io.PrintStream printStream = new java.io.PrintStream(outputStream, true,
                java.nio.charset.StandardCharsets.UTF_8);
        io.ballerina.protoc.cli.GrpcCmd grpcCmd = new io.ballerina.protoc.cli.GrpcCmd(printStream);

        try {
            java.lang.reflect.Field helpFlagField = grpcCmd.getClass().getDeclaredField("helpFlag");
            helpFlagField.setAccessible(true);
            helpFlagField.set(grpcCmd, true);

            grpcCmd.execute();

            String actualOutput = outputStream.toString(java.nio.charset.StandardCharsets.UTF_8).trim();
            String expectedOutput = getHelpText();
            Assert.assertEquals(actualOutput, expectedOutput,
                    "Expected help text to be displayed when --help flag is provided");
        } catch (ReflectiveOperationException e) {
            Assert.fail("Help flag test failed due to reflection error: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("Help flag test failed with unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testHelloWorldWithEnum() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithEnum.proto",
                "helloWorldWithEnum_pb.bal", "tool_test_data_type_3");
    }

    @Test
    public void testHelloWorldWithMap() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithMap.proto",
                "helloWorldWithMap_pb.bal", "tool_test_data_type_5");
    }

    @Test
    public void testHelloWorldWithNestedEnum() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithNestedEnum.proto",
                "helloWorldWithNestedEnum_pb.bal", "tool_test_data_type_6");
    }

    @Test
    public void testHelloWorldWithNestedMessage() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithNestedMessage.proto",
                "helloWorldWithNestedMessage_pb.bal", "tool_test_data_type_7");
    }

    @Test
    public void testHelloWorldWithPackage() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithPackage.proto",
                "helloWorldWithPackage_pb.bal", "tool_test_data_type_8");
    }

    @Test
    public void testHelloWorldWithReservedNames() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithReservedNames.proto",
                "helloWorldWithReservedNames_pb.bal", "tool_test_data_type_9");
    }

    @Test
    public void testStubGenerationWithReservedNames() {

        assertGeneratedDataTypeSources("data-types", "enumWithReservedNames.proto",
                "enumWithReservedNames_pb.bal", "tool_test_data_type_23");
    }

    @Test
    public void testEmptyMessageTypes() {

        assertGeneratedDataTypeSources("data-types", "empty_message_types.proto",
                "empty_message_types_pb.bal", "tool_test_data_type_24");
    }

    @Test
    public void testMessage() {
        assertGeneratedDataTypeSources("data-types", "message.proto", "message_pb.bal",
                "tool_test_data_type_10");
    }

    @Test
    public void testOneofFieldService() {
        assertGeneratedDataTypeSources("data-types", "oneof_field_service.proto",
                "oneof_field_service_pb.bal", "tool_test_data_type_11");
    }

    @Test
    public void testTestMessage() {
        assertGeneratedDataTypeSources("data-types", "testMessage.proto",
                "testMessage_pb.bal", "tool_test_data_type_12");
    }

    @Test
    public void testHelloWorldWithDuplicateInputOutput() {
        assertGeneratedDataTypeSources("data-types", "helloWorldWithDuplicateInputOutput.proto",
                "helloWorldWithDuplicateInputOutput_pb.bal", "tool_test_data_type_13");
    }

    @Test
    public void testHelloWorldWithDurationType1() {
        assertGeneratedSources("data-types", "duration_type1.proto", "duration_type1_pb.bal",
                "durationhandler_service.bal", "durationhandler_client.bal", "tool_test_data_type_15");
    }

    @Test
    public void testHelloWorldWithDurationType2() {
        assertGeneratedSources("data-types", "duration_type2.proto", "duration_type2_pb.bal",
                "durationhandler_service.bal", "durationhandler_client.bal", "tool_test_data_type_16");
    }

    @Test
    public void testHelloWorldWithStructType1() {
        assertGeneratedSources("data-types", "struct_type1.proto", "struct_type1_pb.bal",
                "structhandler_service.bal", "structhandler_client.bal", "tool_test_data_type_17");
    }

    @Test
    public void testHelloWorldWithStructType2() {
        assertGeneratedSources("data-types", "struct_type2.proto", "struct_type2_pb.bal",
                "structhandler_service.bal", "structhandler_client.bal", "tool_test_data_type_18");
    }

    @Test
    public void testHelloWorldWithAnyType() {
        assertGeneratedSources("data-types", "any.proto", "any_pb.bal", "anytypeserver_service.bal",
                "anytypeserver_client.bal", "tool_test_data_type_21");
    }

    @Test
    public void testHelloWorldChild() {
        assertGeneratedDataTypeSources("data-types", "child.proto", "parent_pb.bal",
                "tool_test_data_type_14");
        assertGeneratedDataTypeSources("data-types", "child.proto", "child_pb.bal",
                "tool_test_data_type_14");
    }

    @Test
    public void testTimeWithDependency() {
        assertGeneratedDataTypeSources("data-types", "time_root.proto", "time_root_pb.bal",
                "tool_test_data_type_19");
        assertGeneratedDataTypeSources("data-types", "time_root.proto", "time_dependent_pb.bal",
                "tool_test_data_type_19");
    }

    @Test
    public void testWithoutOutputDir() {
        assertGeneratedDataTypeSources("data-types", "message.proto",
                "message_pb.bal", "");
    }

    @Test
    public void testHelloWorldErrorSyntax() {
        assertGeneratedDataTypeSourcesNegative("negative", "helloWorldErrorSyntax.proto",
                "helloWorldErrorSyntax_pb.bal", "tool_test_data_type_2");
    }

    @Test
    public void testHelloWorldWithInvalidDependency() {
        assertGeneratedDataTypeSourcesNegative("negative", "helloWorldWithInvalidDependency.proto",
                "helloWorldWithInvalidDependency_pb.bal", "tool_test_data_type_4");
    }

    @Test
    public void testMultipleWrapperTypes() {
        assertGeneratedDataTypeSources("data-types", "multiple_wrapper_types.proto",
                "multiple_wrapper_types_pb.bal", "tool_test_data_type_20");
    }

    @Test
    public void testDuplicateOutputType() {
        assertGeneratedDataTypeSources("data-types", "duplicate_output_type.proto",
                "duplicate_output_type_pb.bal", "tool_test_data_type_22");
    }

    @Test
    public void testServiceWithNestedMessage() {
        assertGeneratedSources("data-types", "service_with_nested_messages.proto",
                "service_with_nested_messages_pb.bal", "servicewithnestedmessage_service.bal",
                "servicewithnestedmessage_client.bal", "tool_test_data_type_25");
    }

    @Test
    public void testBasicNestedDirectories() {
        assertGeneratedSourcesWithNestedDirectories("nested/basic/**.proto",
                "tool_test_nested_directories_01", null);
        Path expectedPath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_nested_directories_01");
        Path actualPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_nested_directories_01");
        Assert.assertEquals(readContent(expectedPath.resolve("service_pb.bal")),
                readContent(actualPath.resolve("service_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages_pb.bal")),
                readContent(actualPath.resolve("messages_pb.bal")));
    }

    @Test
    public void testNestedDirectoryWithImportPath() {
        assertGeneratedSourcesWithNestedDirectories("nested/import_path/**.proto",
                "tool_test_nested_directories_02", "nested/import_path/");
        Path expectedPath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_nested_directories_02");
        Path actualPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_nested_directories_02");
        Assert.assertEquals(readContent(expectedPath.resolve("service_pb.bal")),
                readContent(actualPath.resolve("service_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages1_pb.bal")),
                readContent(actualPath.resolve("messages1_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages2_pb.bal")),
                readContent(actualPath.resolve("messages2_pb.bal")));
    }

    @Test
    public void testNestedDirectoryWithMultipleServices() {
        assertGeneratedSourcesWithNestedDirectories("nested/multiple_service/**.proto",
                "tool_test_nested_directories_03", "nested/multiple_service");
        Path expectedPath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_nested_directories_03");
        Path actualPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_nested_directories_03");
        Assert.assertEquals(readContent(expectedPath.resolve("service1_pb.bal")),
                readContent(actualPath.resolve("service1_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("service2_pb.bal")),
                readContent(actualPath.resolve("service2_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages1_pb.bal")),
                readContent(actualPath.resolve("messages1_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages2_pb.bal")),
                readContent(actualPath.resolve("messages2_pb.bal")));
    }

    @Test
    public void testNestedDirectoryWithPackageOption() {
        try {
            Files.createDirectories(Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_packaging_nested_dirs"));
        } catch (IOException e) {
            Assert.fail("Could not create target directories", e);
        }
        assertGeneratedSourcesWithNestedDirectories("nested/package/**.proto", "tool_test_packaging_nested_dirs",
                "nested/package/");
        Path expectedPath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_packaging_nested_dirs");
        Path actualPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_packaging_nested_dirs");
        Assert.assertEquals(readContent(expectedPath.resolve("service1_pb.bal")),
                readContent(actualPath.resolve("service1_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("service2_pb.bal")),
                readContent(actualPath.resolve("service2_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages1_pb.bal")),
                readContent(actualPath.resolve("modules/messages/messages1_pb.bal")));
        Assert.assertEquals(readContent(expectedPath.resolve("messages2_pb.bal")),
                readContent(actualPath.resolve("modules/messages/messages2_pb.bal")));
    }

    @Test
    public void testProtoDirectory() {
        Path protoFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, "proto-dir");
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_proto_dir");

        generateSourceCode(protoFilePath, outputDirPath, null, null);

        Path expectedStubFilePath1 = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_proto_dir", "helloWorldBoolean_pb.bal");
        Path expectedStubFilePath2 = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_proto_dir", "helloWorldInt_pb.bal");
        Path expectedStubFilePath3 = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_proto_dir", "helloWorldString_pb.bal");
        Path expectedStubFilePath4 = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_proto_dir", "helloWorldWithDependency_pb.bal");

        Path actualStubFilePath1 = Paths.get(outputDirPath.toString(), "helloWorldBoolean_pb.bal");
        Path actualStubFilePath2 = Paths.get(outputDirPath.toString(), "helloWorldInt_pb.bal");
        Path actualStubFilePath3 = Paths.get(outputDirPath.toString(), "helloWorldString_pb.bal");
        Path actualStubFilePath4 = Paths.get(outputDirPath.toString(), "helloWorldWithDependency_pb.bal");

        Assert.assertTrue(Files.exists(actualStubFilePath1));
        Assert.assertFalse(hasSemanticDiagnostics(actualStubFilePath1, true));
        Assert.assertEquals(readContent(expectedStubFilePath1), readContent(actualStubFilePath1));

        Assert.assertTrue(Files.exists(actualStubFilePath2));
        Assert.assertFalse(hasSemanticDiagnostics(actualStubFilePath2, true));
        Assert.assertEquals(readContent(expectedStubFilePath2), readContent(actualStubFilePath2));

        Assert.assertTrue(Files.exists(actualStubFilePath3));
        Assert.assertFalse(hasSemanticDiagnostics(actualStubFilePath3, true));
        Assert.assertEquals(readContent(expectedStubFilePath3), readContent(actualStubFilePath3));

        Assert.assertTrue(Files.exists(actualStubFilePath4));
        Assert.assertFalse(hasSyntacticDiagnostics(actualStubFilePath4));
        Assert.assertEquals(readContent(expectedStubFilePath4), readContent(actualStubFilePath4));
    }

    @Test
    public void testExternalImportPaths() {
        Path protoFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, "external-imports",
                "myproj", "foo", "bar", "child.proto");
        Path importDirPath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, "external-imports",
                "myproj");
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_external_imports");

        Path actualRootStubFilePath = outputDirPath.resolve("child_pb.bal");
        Path actualDependentStubFilePath = outputDirPath.resolve("parent_pb.bal");
        Path expectedRootStubFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_external_imports", "child_pb.bal");
        Path expectedDependentStubFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                "tool_test_external_imports", "parent_pb.bal");

        generateSourceCode(protoFilePath, outputDirPath, "stubs", importDirPath);

        Path destTomlFile = outputDirPath.resolve(BALLERINA_TOML_FILE);
        copyBallerinaToml(destTomlFile);

        Assert.assertTrue(Files.exists(actualRootStubFilePath));
        Assert.assertFalse(hasSemanticDiagnostics(outputDirPath, false));
        Assert.assertEquals(readContent(expectedRootStubFilePath), readContent(actualRootStubFilePath));

        Assert.assertTrue(Files.exists(actualDependentStubFilePath));
        Assert.assertFalse(hasSemanticDiagnostics(outputDirPath, false));
        Assert.assertEquals(readContent(expectedDependentStubFilePath), readContent(actualDependentStubFilePath));
    }

    @Test
    public void testImportsWithEnums() {
        assertGeneratedSources("data-types/enum_imports", "child.proto",
                "child_pb.bal", "helloworld_service.bal", "helloworld_client.bal", "tool_test_data_type_26");
    }

    @Test
    public void testGeneratedFileNewlines() {
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, "tool_test_newline_test");
        try {
            Files.createDirectories(outputDirPath);
        } catch (IOException e) {
            Assert.fail("Could not create target directories", e);
        }

        Path protoFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, "data-types",
                "message.proto");
        Path generatedFile = outputDirPath.resolve("message_pb.bal");

        generateSourceCode(protoFilePath, outputDirPath, null, null);
        Assert.assertTrue(Files.exists(generatedFile), "Generated file does not exist");

        try {
            String content = Files.readString(generatedFile);
            int newlineCount = 0;
            int index = content.length() - 1;

            // check for newlines at the end of the file
            while (index >= 0 && content.charAt(index) == '\n') {
                newlineCount++;
                index--;
            }

            // if the newline count is not 1, fail the test
            Assert.assertEquals(newlineCount, 1,
                    "Generated file should have exactly one newline at the end, found " + newlineCount);
        } catch (IOException e) {
            Assert.fail("Failed to read generated file", e);
        }
    }
}
