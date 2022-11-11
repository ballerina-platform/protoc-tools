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
package io.ballerina.protoc.protobuf.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.protoc.builder.BallerinaFileBuilder;
import io.ballerina.protoc.builder.balgen.BalGenConstants;
import io.ballerina.protoc.builder.model.SrcFilePojo;
import io.ballerina.protoc.builder.stub.GeneratorContext;
import io.ballerina.protoc.exception.CodeBuilderException;
import io.ballerina.protoc.protobuf.BalGenerationConstants;
import io.ballerina.protoc.protobuf.descriptor.DescriptorMeta;
import io.ballerina.protoc.protobuf.exception.CodeGeneratorException;
import io.ballerina.protoc.protobuf.exception.InvalidProtoFileException;
import io.ballerina.protoc.protobuf.utils.BalFileGenerationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.protoc.protobuf.BalGenerationConstants.EMPTY_STRING;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.META_LOCATION;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.PROTO_SUFFIX;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.TEMP_API_DIRECTORY;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.TEMP_BALLERINA_DIRECTORY;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.TEMP_COMPILER_DIRECTORY;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.TEMP_GOOGLE_DIRECTORY;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.TEMP_PROTOBUF_DIRECTORY;
import static io.ballerina.protoc.protobuf.BalGenerationConstants.TMP_DIRECTORY_PATH;

/**
 * Class to implement "grpc" command for ballerina.
 * Ex: ballerina grpc  --input (proto-file-path)  --output (output-directory-path)
 */
@CommandLine.Command(
        name = "grpc",
        description = "generate Ballerina gRPC client stub for gRPC service for a given gRPC protoc " +
                "definition.")
public class GrpcCmd implements BLauncherCmd {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcCmd.class);

    private static final PrintStream outStream = System.out;
    private static final String PROTO_EXTENSION = "proto";

    private CommandLine parentCmdParser;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = {"--input"}, description = "Input .proto file or a directory containing " +
            "multiple .proto files", required = true)
    private String protoPath;

    @CommandLine.Option(names = {"--mode"},
            description = "Ballerina source [service or client]"
    )
    private String mode = "stub";

    @CommandLine.Option(names = {"--output"},
            description = "Generated Ballerina source files location"
    )
    private String balOutPath;

    private String protocExePath;

    @CommandLine.Option(names = {"--protoc-version"}, hidden = true)
    private String protocVersion = "3.21.7";

    @CommandLine.Option(names = {"--proto-path"}, description = "Path to a directory in which to look for .proto " +
            "files when resolving import directives")
    private String importPath = "";

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/wrapper.proto"
     */
    private static void exportResource(String resourceName, ClassLoader classLoader) throws CodeGeneratorException {

        try (InputStream initialStream = classLoader.getResourceAsStream(resourceName);
             OutputStream resStreamOut = new FileOutputStream(new File(TMP_DIRECTORY_PATH, resourceName))) {
            if (initialStream == null) {
                throw new CodeGeneratorException("Cannot get resource file \"" + resourceName + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = initialStream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            throw new CodeGeneratorException("Cannot find '" + resourceName + "' file inside resources.", e);
        }
    }
    
    @Override
    public void execute() {
        //Help flag check
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(getName());
            outStream.println(commandUsageInfo);
            return;
        }

        List<List<SrcFilePojo>> genSrcFiles = new ArrayList<>();
        if (protoPath.endsWith("**.proto") || new File(protoPath).isDirectory()) {
            // Multiple proto files
            List<String> protoFiles;
            try {
                protoFiles = getProtoFiles(protoPath);
            } catch (IOException e) {
                String errorMessage = "Failed to find proto files in the directory. " +
                        "Please input a valid proto files directory.";
                outStream.println(errorMessage);
                return;
            }
            if (protoFiles.size() == 0) {
                String errorMessage = "Input directory does not contain any proto files. " +
                        "Please input a valid proto files directory.";
                outStream.println(errorMessage);
            } else {
                for (String protoFile : protoFiles) {
                    try {
                        genSrcFiles.add(generateBalFile(protoFile, importPath, balOutPath, protocExePath, protocVersion,
                                mode, GeneratorContext.CLI));
                    } catch (Exception e) {
                        outStream.println(e.getMessage());
                    }
                }
            }
        } else {
            // Single proto file
            // check input protobuf file path
            Optional<String> pathExtension = getFileExtension(protoPath);
            if (pathExtension.isEmpty() || !PROTO_EXTENSION.equalsIgnoreCase(pathExtension.get())) {
                String errorMessage = "Invalid proto file path. Please input valid proto file location.";
                outStream.println(errorMessage);
                return;
            }
            try {
                genSrcFiles.add(generateBalFile(protoPath, importPath, balOutPath, protocExePath, protocVersion, mode,
                        GeneratorContext.CLI));
            } catch (Exception e) {
                outStream.println(e.getMessage());
            }
        }
        try {
            writeOutputFile(genSrcFiles);
        } catch (CodeBuilderException e) {
            outStream.println(e.getMessage());
        }
    }

    public List<String> getProtoFiles(String path) throws IOException {
        if (path.endsWith("**.proto")) {
            try (Stream<Path> walk = Files.walk(Paths.get(path.substring(0, path.length() - 8)))) {
                return walkInsideProtoDirectory(walk);
            }
        }
        try (Stream<Path> walk = Files.walk(Paths.get(path), 1)) {
            return walkInsideProtoDirectory(walk);
        }
    }

    private List<String> walkInsideProtoDirectory(Stream<Path> walk) {
        return walk.filter(p -> !Files.isDirectory(p))
                .map(Path::toString)
                .filter(f -> f.endsWith(".proto"))
                .collect(Collectors.toList());
    }

    private static void writeOutputFile(List<List<SrcFilePojo>> genSrcFiles) throws CodeBuilderException {
        for (List<SrcFilePojo> genFilesPerProto : genSrcFiles) {
            for (SrcFilePojo genSrcFile : genFilesPerProto) {
                try (PrintWriter writer = new PrintWriter(genSrcFile.getFileName(), StandardCharsets.UTF_8.name())) {
                    writer.println(genSrcFile.getContent());
                    outStream.println("Successfully generated the Ballerina file." +
                            BalGenerationConstants.NEW_LINE_CHARACTER);
                } catch (IOException e) {
                    throw new CodeBuilderException("IO Error while writing output to Ballerina file. " +
                            e.getMessage(), e);
                }
            }
        }
    }

    public List<SrcFilePojo> generateBalFile(String protoPath, String importPath, String balOutPath,
                                             String protocExePath, String protocVersion, String mode,
                                             GeneratorContext generatorContext) throws Exception {
        if (!Files.isReadable(Paths.get(protoPath))) {
            String errorMessage = "Provided service proto file is not readable. Please input valid proto file " +
                    "location.";
            outStream.println(errorMessage);
            throw new InvalidProtoFileException(errorMessage);
        }

        if (!importPath.isBlank()) {
            File importFilePath = new File(importPath);
            File protoFilePath = new File(protoPath);
            if (!isInSubDirectory(importFilePath, protoFilePath)) {
                String errorMessage = "Input .proto file does not reside within the path specified using " +
                        "--proto-path. You must specify a --proto-path which encompasses the .proto file.";
                outStream.println(errorMessage);
                throw new Exception(errorMessage);
            }
        }
        // Temporary disabled due to new service changes.
        if (BalGenConstants.GRPC_PROXY.equals(mode)) {
            String errorMessage = "gRPC gateway proxy service generation is currently not supported.";
            outStream.println(errorMessage);
            throw new Exception(errorMessage);
        }

        // download protoc executor.
        String absoluteProtocExePath;
        try {
            absoluteProtocExePath = downloadProtocexe(protocExePath, protocVersion);
        } catch (IOException | CodeGeneratorException e) {
            String errorMessage = "Error while preparing the protoc executable. " + e.getMessage();
            LOG.error("Error while preparing the protoc executable.", e);
            outStream.println(errorMessage);
            throw new CodeGeneratorException(e.getMessage());
        }

        // extract proto library files.
        createProtoPackageDirectories();
        StringBuilder msg = new StringBuilder();
        LOG.debug("Initializing the ballerina code generation.");
        DescriptorMeta root;
        Set<DescriptorMeta> dependant;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            try {
                List<String> protoFiles = readProperties(classLoader);
                for (String file : protoFiles) {
                    exportResource(file, classLoader);
                }
            } catch (Exception e) {
                LOG.error("Error extracting the resource file. ", e);
                outStream.println("Error while reading the library files. " + e.getMessage());
                throw new Exception(e.getMessage());
            }
            if (msg.toString().isEmpty()) {
                outStream.println("Successfully extracted the library files.");
            } else {
                outStream.println(msg);
                throw new Exception(msg.toString());
            }

            // read root/dependent file descriptors.
            Path descFilePath = createServiceDescriptorFile(protoPath);
            try {
                if (importPath.isBlank()) {
                    root = DescriptorsGenerator.generateRootDescriptor(
                            absoluteProtocExePath,
                            getAbsolutePath(protoPath),
                            getAbsolutePath(BalFileGenerationUtils.resolveProtoFolderPath(protoPath)),
                            descFilePath.toAbsolutePath().toString()
                    );
                } else {
                    root = DescriptorsGenerator.generateRootDescriptor(
                            absoluteProtocExePath,
                            getAbsolutePath(protoPath),
                            getAbsolutePath(importPath),
                            descFilePath.toAbsolutePath().toString()
                    );
                }
            } catch (CodeGeneratorException e) {
                String errorMessage = "An error occurred when generating the proto descriptor. " + e.getMessage();
                LOG.error("An error occurred when generating the proto descriptor.", e);
                outStream.println(errorMessage);
                throw new CodeGeneratorException(e.getMessage());
            }
            if (root.getDescriptor().length == 0) {
                String errorMsg = "An error occurred when generating the proto descriptor.";
                LOG.error(errorMsg);
                outStream.println(errorMsg);
                throw new Exception(errorMsg);
            }
            LOG.debug("Successfully generated the root descriptor.");
            try {
                if (importPath.isBlank()) {
                    dependant = DescriptorsGenerator.generateDependentDescriptor(
                            absoluteProtocExePath,
                            getAbsolutePath(BalFileGenerationUtils.resolveProtoFolderPath(protoPath)),
                            descFilePath.toAbsolutePath().toString()
                    );
                } else {
                    dependant = DescriptorsGenerator.generateDependentDescriptor(
                            absoluteProtocExePath,
                            getAbsolutePath(importPath),
                            descFilePath.toAbsolutePath().toString()
                    );
                }
            } catch (CodeGeneratorException e) {
                String errorMessage =
                        "An error occurred when generating the dependent proto descriptor. " + e.getMessage();
                LOG.error(errorMessage, e);
                outStream.println(errorMessage);
                throw new CodeGeneratorException(e.getMessage());
            }
            LOG.debug("Successfully generated the dependent descriptor.");
        } finally {
            //delete temporary meta files
            File tempDir = new File(TMP_DIRECTORY_PATH);
            BalFileGenerationUtils.delete(new File(tempDir, META_LOCATION));
            BalFileGenerationUtils.delete(new File(tempDir, TEMP_GOOGLE_DIRECTORY));
            BalFileGenerationUtils.delete(new File(tempDir, TEMP_BALLERINA_DIRECTORY));
            LOG.debug("Successfully deleted temporary files.");
        }
        // generate ballerina stub based on descriptor values.
        BallerinaFileBuilder ballerinaFileBuilder;
        // If user provides output directory, generate service stub inside output directory.
        try {
            if (balOutPath == null) {
                ballerinaFileBuilder = new BallerinaFileBuilder(root, dependant);
            } else {
                ballerinaFileBuilder = new BallerinaFileBuilder(root, dependant, balOutPath);
            }
            return ballerinaFileBuilder.build(mode, generatorContext);
        } catch (CodeBuilderException | CodeGeneratorException | IOException e) {
            LOG.error("Error generating the Ballerina file.", e);
            msg.append("Error generating the Ballerina file.").append(e.getMessage())
                    .append(BalGenerationConstants.NEW_LINE_CHARACTER);
            outStream.println(msg);
            throw new Exception(e.getMessage());
        }
    }

    public static boolean isInSubDirectory(File dir, File file) {
        if (file == null) {
            return false;
        }
        if (file.equals(dir)) {
            return true;
        }
        return isInSubDirectory(dir, file.getParentFile());
    }

    /**
     * Create temp metadata directory which needed for intermediate processing.
     *
     * @return Temporary Created meta file.
     */
    private static Path createServiceDescriptorFile(String protoPath) {

        Path descriptorDirPath = Paths.get(TMP_DIRECTORY_PATH, META_LOCATION);
        try {
            Files.createDirectories(descriptorDirPath);
            return Files.createFile(descriptorDirPath.resolve(getProtoFileName(protoPath) + "-descriptor.desc"));
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't create a temp directory: "
                    + descriptorDirPath.toAbsolutePath() + " error: " + e.getMessage(), e);
        }
    }

    /**
     * Create directories to copy dependent proto files.
     */
    private static void createProtoPackageDirectories() {

        Path protobufCompilerDirPath = Paths.get(TMP_DIRECTORY_PATH, TEMP_GOOGLE_DIRECTORY, TEMP_PROTOBUF_DIRECTORY,
                TEMP_COMPILER_DIRECTORY);
        Path protobufApiDirPath = Paths.get(TMP_DIRECTORY_PATH, TEMP_GOOGLE_DIRECTORY, TEMP_API_DIRECTORY);
        Path ballerinaProtoDirPath = Paths.get(TMP_DIRECTORY_PATH, TEMP_BALLERINA_DIRECTORY, TEMP_PROTOBUF_DIRECTORY);
        try {
            Files.createDirectories(protobufCompilerDirPath);
            Files.createDirectories(protobufApiDirPath);
            Files.createDirectories(ballerinaProtoDirPath);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't create directories for dependent proto files. "
                    + " error: " + e.getMessage(), e);
        }
    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private static String getAbsolutePath(String protoPath) {

        return Paths.get(protoPath).toAbsolutePath().toString();
    }
    
    /**
     * Download the protoc executor.
     */
    private static String downloadProtocexe(String protocExePath, String protocVersion) throws IOException,
            CodeGeneratorException {

        if (protocExePath == null) {
            String protocFilename = "protoc-" + protocVersion
                    + "-" + OSDetector.getDetectedClassifier() + BalGenerationConstants.PROTOC_PLUGIN_EXE_PREFIX;
            File protocExeFile = new File(TMP_DIRECTORY_PATH, protocFilename);
            protocExePath = protocExeFile.getAbsolutePath(); // if file already exists will do nothing
            if (!protocExeFile.exists()) {
                outStream.println("Downloading the protoc executor file - " + protocFilename);
                String protocDownloadurl = BalGenerationConstants.PROTOC_PLUGIN_EXE_URL_SUFFIX +
                        protocVersion + "/" + protocFilename;
                File tempDownloadFile = new File(TMP_DIRECTORY_PATH, protocFilename + ".download");
                try {
                    BalFileGenerationUtils.downloadFile(new URL(protocDownloadurl), tempDownloadFile);
                    Files.move(tempDownloadFile.toPath(), protocExeFile.toPath());
                    //set application user permissions to 455
                    BalFileGenerationUtils.grantPermission(protocExeFile);
                } catch (CodeGeneratorException e) {
                    Files.deleteIfExists(Paths.get(protocExePath));
                    throw e;
                }
                outStream.println("Download successfully completed. Executor file path - " + protocExeFile.getPath());
            } else {
                BalFileGenerationUtils.grantPermission(protocExeFile);
                outStream.println("Continuing with the existing protoc executor file at " + protocExeFile.getPath());
            }
        } else {
            outStream.println("Continuing with the provided protoc executor file at " + protocExePath);
        }
        return protocExePath;
    }
    
    
    @Override
    public String getName() {
        return BalGenerationConstants.COMPONENT_IDENTIFIER;
    }
    
    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Generates ballerina gRPC client stub for gRPC service").append(System.lineSeparator());
        out.append("for a given grpc protoc definition").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + BalGenerationConstants.COMPONENT_IDENTIFIER + " --input chat.proto\n");
    }
    
    private static String getProtoFileName(String protoPath) {
        File file = new File(protoPath.replace("**", EMPTY_STRING));
        return file.getName().replace(PROTO_SUFFIX, EMPTY_STRING);
    }
    
    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
        this.parentCmdParser = parentCmdParser;
    }

    private static List<String> readProperties(ClassLoader classLoader) throws CodeGeneratorException {

        String fileName;
        List<String> protoFilesList = new ArrayList<>();
        try (InputStream initialStream = classLoader.getResourceAsStream("standardProtos.properties");
             BufferedReader reader = new BufferedReader(new InputStreamReader(initialStream, StandardCharsets.UTF_8))) {
            while ((fileName = reader.readLine()) != null) {
                protoFilesList.add(fileName);
            }
        } catch (IOException e) {
            throw new CodeGeneratorException("Error while reading property file. " + e.getMessage(), e);
        }
        return protoFilesList;
    }
    
    public void setProtoPath(String protoPath) {
        this.protoPath = protoPath;
    }
    
    public void setBalOutPath(String balOutPath) {
        this.balOutPath = balOutPath;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }
}
