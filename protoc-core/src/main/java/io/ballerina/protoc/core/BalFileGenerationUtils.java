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
package io.ballerina.protoc.core;

import io.ballerina.protoc.core.exception.CodeGeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Util function used when generating bal file from .proto definition.
 */
public class BalFileGenerationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(BalFileGenerationUtils.class);

    private BalFileGenerationUtils() {
    }

    /**
     * Execute command and generate file descriptor.
     *
     * @param command protoc executor command.
     * @return the output of the protoc command as a string array.
     * @throws CodeGeneratorException if an error occurred when executing protoc command.
     */
    public static ArrayList<String> generateDescriptor(String command) throws CodeGeneratorException {
        ArrayList<String> output = new ArrayList<>();
        Process process;
        try {
            process = runProcess(command);
        } catch (IOException e) {
            throw new CodeGeneratorException("Error in executing protoc command '" + command + "'. " + e.getMessage(),
                    e);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) {
                        break;
                    }
                    output.add(line);
                } catch (IOException e) {
                    throw new CodeGeneratorException("Failed to read protoc command output. " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new CodeGeneratorException("Failed to generate protoc command output. " + e.getMessage(), e);
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new CodeGeneratorException("Process not successfully completed. Process is interrupted while" +
                    " running the protoc executor. " + e.getMessage(), e);
        }
        if (process.exitValue() != 0) {
            if (output.isEmpty()) {
                throw new CodeGeneratorException("Proto compilation failed with exit code: " + process.exitValue());
            } else {
                String rawError = String.join(System.lineSeparator(), output);
                String formattedError = formatProtoError(rawError);
                throw new CodeGeneratorException(formattedError);
            }
        }
        return output;
    }

    public static Process runProcess(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows()) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }
        builder.directory(new File(System.getProperty("user.home")));
        return builder.start();
    }

    public static void handleProcessExecutionErrors(Process process) throws CodeGeneratorException {
        // Capture error stream before checking exit value to avoid "Stream closed" errors
        StringBuilder errMsg = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),
                StandardCharsets.UTF_8))) {
            String err;
            while ((err = bufferedReader.readLine()) != null) {
                if (!errMsg.isEmpty()) {
                    errMsg.append(System.lineSeparator());
                }
                errMsg.append(err);
            }
        } catch (IOException e) {
            throw new CodeGeneratorException("Failed to read error output from protoc command. " + e.getMessage(), e);
        }

        // Now check exit value after reading error stream
        if (process.exitValue() != 0) {
            if (errMsg.isEmpty()) {
                throw new CodeGeneratorException("Proto compilation failed with exit code: " + process.exitValue());
            } else {
                // Format error message to be more user-friendly
                String formattedError = formatProtoError(errMsg.toString());
                throw new CodeGeneratorException(formattedError);
            }
        }
    }

    /**
     * Format protoc error messages to be more user-friendly.
     *
     * @param rawError The raw error output from protoc
     * @return A formatted, user-friendly error message
     */
    private static String formatProtoError(String rawError) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("Error: Failed to generate Ballerina files from proto definition.")
                .append(System.lineSeparator());
        formatted.append(System.lineSeparator());
        formatted.append("Proto compilation failed with the following error:")
                .append(System.lineSeparator());
        formatted.append(rawError);
        formatted.append(System.lineSeparator());
        formatted.append(System.lineSeparator());
        formatted.append("Please check your .proto file for syntax errors and undefined types.");
        return formatted.toString();
    }

    /**
     * Resolve proto folder path from Proto file path.
     *
     * @param protoPath Proto file path
     * @return Parent folder path of proto file.
     */
    public static String resolveProtoFolderPath(String protoPath) {
        int idx = protoPath.lastIndexOf(BalGenerationConstants.FILE_SEPARATOR);
        String protoFolderPath = BalGenerationConstants.EMPTY_STRING;
        if (idx > 0) {
            protoFolderPath = protoPath.substring(0, idx);
        }
        return protoFolderPath.trim();
    }

    /**
     * Enclose path by double quotations to escape from empty spaces in the path.
     *
     * @param path Any file path or a directory path
     * @return Same given path that enclosed by double quotations.
     */
    public static String escapeSpaces(String path) {

        return "\"" + path + "\"";
    }
    
    /**
     * Used to clear the temporary files.
     *
     * @param file file to be deleted
     */
    public static void delete(File file) {
        if ((file != null) && file.exists() && file.isDirectory()) {
            String[] files = file.list();
            if (files != null) {
                if (files.length != 0) {
                    for (String temp : files) {
                        File fileDelete = new File(file, temp);
                        if (fileDelete.isDirectory()) {
                            delete(fileDelete);
                        }
                        if (fileDelete.delete()) {
                            LOG.debug("Successfully deleted file " + file);
                        }
                    }
                }
            }
            if (file.delete()) {
                LOG.debug("Successfully deleted file " + file);
            }
            if ((file.getParentFile() != null) && (file.getParentFile().delete())) {
                LOG.debug("Successfully deleted parent file " + file);
            }
        } else if (file != null) {
            if (file.delete()) {
                LOG.debug("Successfully deleted parent file " + file);
            }
        }
    }
    
    /**
     * Download file in the url to the destination file.
     *
     * @param url  file URL.
     * @param file destination file.
     * @throws CodeGeneratorException if an error occurred while downloading the file.
     */
    public static void downloadFile(URL url, File file) throws CodeGeneratorException {
        try (InputStream in = url.openStream(); FileOutputStream fos = new FileOutputStream(file)) {
            if (in != null) {
                int length;
                byte[] buffer = new byte[1024]; // buffer for portion of data from
                while ((length = in.read(buffer)) > -1) {
                    fos.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            String msg = "Couldn't download the executable file: " + file.getName() + ". " + e.getMessage();
            throw new CodeGeneratorException(msg, e);
        }
    }
    
    /**
     * Grant permission to the protoc executor file.
     *
     * @param file protoc executor file.
     * @throws CodeGeneratorException if an error occurred while providing execute permission to protoc executor file.
     */
    public static void grantPermission(File file) throws CodeGeneratorException {
        boolean isExecutable = file.setExecutable(true);
        boolean isReadable = file.setReadable(true);
        boolean isWritable = file.setWritable(true);
        if (isExecutable && isReadable && isWritable) {
            LOG.debug("Successfully granted permission for protoc exe file");
        } else {
            String msg = "Couldn't grant permission to executable file: " + file.getName();
            throw new CodeGeneratorException(msg);
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows");
    }
}
