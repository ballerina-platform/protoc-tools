/*
 *  Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.protoc.tools;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.protoc.cli.GrpcCmd;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.testng.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.FILE_SEPARATOR;

/**
 * gRPC tool test Utils.
 */
public class ToolingTestUtils {

    public static final String PROTO_FILE_DIRECTORY = "proto-files/";
    public static final String BAL_FILE_DIRECTORY = "generated-sources/";
    public static final String GENERATED_SOURCES_DIRECTORY = "build/generated-sources/";
    public static final String BALLERINA_TOML_FILE = "Ballerina.toml";

    public static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources", "test-src")
            .toAbsolutePath();
    private static final Path BALLERINA_TOML_PATH = Paths.get(RESOURCE_DIRECTORY.toString(), BALLERINA_TOML_FILE);

    public static void assertGeneratedSources(String subDir, String protoFile, String stubFile, String serviceFile,
                                              String clientFile, String outputDir, String... subModulesFiles) {
        Path protoFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, subDir, protoFile);
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir);

        Path protocOutputDirPath;
        if (outputDir.contains("tool_test_packaging")) {
            protocOutputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY);
        } else {
            protocOutputDirPath = outputDirPath;
        }

        Path expectedStubFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY, outputDir, stubFile);
        Path expectedClientStubFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                outputDir, stubFile.replace(".bal", "_client.bal"));
        Path expectedServiceFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                outputDir, serviceFile);
        Path expectedClientFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                outputDir, clientFile);

        Path actualStubFilePath = outputDirPath.resolve(stubFile);
        Path actualServiceFilePath = outputDirPath.resolve(serviceFile);
        Path actualClientFilePath = outputDirPath.resolve(clientFile);

        generateSourceCode(protoFilePath, protocOutputDirPath, null, null);
        Assert.assertTrue(Files.exists(actualStubFilePath));

        Path destTomlFile = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir, BALLERINA_TOML_FILE);
        copyBallerinaToml(destTomlFile);

        Assert.assertFalse(hasSemanticDiagnostics(outputDirPath, false));
        Assert.assertEquals(readContent(actualStubFilePath), readContent(expectedStubFilePath));
        try {
            Files.deleteIfExists(actualStubFilePath);
        } catch (IOException e) {
            Assert.fail("Failed to delete stub file", e);
        }
        Assert.assertFalse(Files.exists(actualStubFilePath));

        generateSourceCode(protoFilePath, protocOutputDirPath, "client", null);
        Assert.assertTrue(Files.exists(actualStubFilePath));
        Assert.assertTrue(Files.exists(actualClientFilePath));
        Assert.assertFalse(hasSemanticDiagnostics(outputDirPath, false));
        Assert.assertEquals(readContent(actualStubFilePath), readContent(expectedClientStubFilePath));
        Assert.assertEquals(readContent(actualClientFilePath), readContent(expectedClientFilePath));
        try {
            Files.deleteIfExists(actualStubFilePath);
            Files.deleteIfExists(actualClientFilePath);
        } catch (IOException e) {
            Assert.fail("Failed to delete stub file", e);
        }
        Assert.assertFalse(Files.exists(actualStubFilePath));

        generateSourceCode(protoFilePath, protocOutputDirPath, "service", null);
        Assert.assertTrue(Files.exists(actualStubFilePath));
        Assert.assertTrue(Files.exists(actualServiceFilePath));
        Assert.assertFalse(hasSyntacticDiagnostics(actualStubFilePath));
        Assert.assertFalse(hasSyntacticDiagnostics(actualServiceFilePath));
        Assert.assertEquals(readContent(actualStubFilePath), readContent(expectedStubFilePath));
        Assert.assertEquals(readContent(actualServiceFilePath), readContent(expectedServiceFilePath));

        try {
            Files.deleteIfExists(actualServiceFilePath);
        } catch (IOException e) {
            Assert.fail("Failed to delete stub file", e);
        }
        if (subModulesFiles.length > 0) {
            for (String subModuleFile: subModulesFiles.clone()) {
                expectedStubFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY,
                        outputDir, subModuleFile);
                actualStubFilePath = outputDirPath.resolve(subModuleFile);
                Assert.assertEquals(readContent(actualStubFilePath), readContent(expectedStubFilePath));
            }
        }
    }

    public static void assertGeneratedDataTypeSources(String subDir, String protoFile, String stubFile,
                                                      String outputDir) {
        Path protoFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, subDir, protoFile);
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir);

        Path expectedStubFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), BAL_FILE_DIRECTORY, outputDir, stubFile);
        Path actualStubFilePath;
        if (outputDir.equals("")) {
            Path tempPath = Paths.get("temp");
            actualStubFilePath = tempPath.resolve(stubFile);
            generateSourceCode(protoFilePath, Paths.get(""), null, null);
            Assert.assertTrue(Files.exists(actualStubFilePath));

            Path destTomlFile = tempPath.resolve(BALLERINA_TOML_FILE);
            copyBallerinaToml(destTomlFile);

            Assert.assertFalse(hasSemanticDiagnostics(tempPath, false));
            try {
                Files.deleteIfExists(actualStubFilePath);
                Files.deleteIfExists(destTomlFile);
                Files.deleteIfExists(tempPath);
            } catch (IOException e) {
                Assert.fail("Failed to delete stub file", e);
            }
        } else {
            actualStubFilePath = outputDirPath.resolve(stubFile);
            generateSourceCode(protoFilePath, outputDirPath, null, null);
            Assert.assertTrue(Files.exists(actualStubFilePath));

            Path destTomlFile = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir, BALLERINA_TOML_FILE);
            copyBallerinaToml(destTomlFile);

            Assert.assertFalse(hasSemanticDiagnostics(outputDirPath, false));
            Assert.assertEquals(readContent(expectedStubFilePath), readContent(actualStubFilePath));
        }
    }

    public static void assertGeneratedSourcesWithNestedDirectories(String subDir, String outputDir, String importDir) {
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir);
        Path protocOutputDirPath;
        if (outputDir.contains("tool_test_packaging")) {
            protocOutputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY);
        } else {
            protocOutputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir);
        }
        try {
            Class<?> grpcCmdClass = Class.forName("io.ballerina.protoc.cli.GrpcCmd");
            GrpcCmd grpcCmd = (GrpcCmd) grpcCmdClass.getDeclaredConstructor().newInstance();
            grpcCmd.setProtoPath(RESOURCE_DIRECTORY + FILE_SEPARATOR + PROTO_FILE_DIRECTORY + subDir);
            grpcCmd.setBalOutPath(protocOutputDirPath.toAbsolutePath().toString());
            if (importDir != null) {
                grpcCmd.setImportPath(Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, importDir)
                        .toAbsolutePath().toString());
            }
            grpcCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Path destTomlFile = outputDirPath.resolve(BALLERINA_TOML_FILE);
        copyBallerinaToml(destTomlFile);
        Assert.assertFalse(hasSemanticDiagnostics(outputDirPath, false));
    }

    public static void assertGeneratedDataTypeSourcesNegative(String subDir, String protoFile,
                                                              String stubFile, String outputDir) {
        Path protoFilePath = Paths.get(RESOURCE_DIRECTORY.toString(), PROTO_FILE_DIRECTORY, subDir, protoFile);
        Path outputDirPath = Paths.get(GENERATED_SOURCES_DIRECTORY, outputDir);
        Path actualStubFilePath = outputDirPath.resolve(stubFile);

        generateSourceCode(protoFilePath, outputDirPath, null, null);
        Assert.assertFalse(Files.exists(actualStubFilePath));
    }

    public static void generateSourceCode(Path sProtoFilePath, Path sOutputDirPath, String mode, Path sImportDirPath) {
        Class<?> grpcCmdClass;
        try {
            grpcCmdClass = Class.forName("io.ballerina.protoc.cli.GrpcCmd");
            GrpcCmd grpcCmd = (GrpcCmd) grpcCmdClass.getDeclaredConstructor().newInstance();
            grpcCmd.setProtoPath(sProtoFilePath.toAbsolutePath().toString());
            if (!sOutputDirPath.toString().isBlank()) {
                grpcCmd.setBalOutPath(sOutputDirPath.toAbsolutePath().toString());
            }
            if (mode != null) {
                grpcCmd.setMode(mode);
            }
            if (sImportDirPath != null) {
                grpcCmd.setImportPath(sImportDirPath.toAbsolutePath().toString());
            }
            grpcCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasSemanticDiagnostics(Path projectPath, boolean isSingleFile) {
        Package currentPackage;
        if (isSingleFile) {
            SingleFileProject singleFileProject = SingleFileProject.load(projectPath);
            currentPackage = singleFileProject.currentPackage();
        } else {
            BuildProject buildProject = BuildProject.load(projectPath);
            currentPackage = buildProject.currentPackage();
        }
        PackageCompilation compilation = currentPackage.getCompilation();
        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        return diagnosticResult.hasErrors();
    }

    public static boolean hasSyntacticDiagnostics(Path filePath) {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            return false;
        }
        TextDocument textDocument = TextDocuments.from(content);
        return SyntaxTree.from(textDocument).hasDiagnostics();
    }

    public static String readContent(Path filePath) {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            return "";
        }
        return content.replaceAll("\\s+", "");
    }

    public static void copyBallerinaToml(Path destTomlPath) {
        try {
            Files.copy(BALLERINA_TOML_PATH, destTomlPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
