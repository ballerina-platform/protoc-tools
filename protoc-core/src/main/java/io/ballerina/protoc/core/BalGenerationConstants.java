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

import java.io.File;

/**PROTOBUF
 * Bal Generation Tool contants class.
 */
public class BalGenerationConstants {

    private BalGenerationConstants() {
    }

    public static final String OS_NAME_SYSTEM_PROPERTY = "os.name";
    public static final String OS_ARCH_SYSTEM_PROPERTY = "os.arch";
    public static final String FILE_SEPARATOR = File.separator;
    public static final String META_LOCATION = "desc_gen";
    public static final String TEMP_GOOGLE_DIRECTORY = "google";
    public static final String TEMP_PROTOBUF_DIRECTORY = "protobuf";
    public static final String TEMP_COMPILER_DIRECTORY = "compiler";
    public static final String TEMP_API_DIRECTORY = "api";
    public static final String TEMP_BALLERINA_DIRECTORY = "ballerina";

    public static final String META_DEPENDENCY_LOCATION = "desc_gen" + FILE_SEPARATOR + "dependencies";
    public static final String NEW_LINE_CHARACTER = System.getProperty("line.separator");
    public static final String GOOGLE_STANDARD_LIB_PROTOBUF = "google" + FILE_SEPARATOR + "protobuf";
    public static final String GOOGLE_STANDARD_LIB_API = "google" + FILE_SEPARATOR + "api";
    public static final String BALLERINA_STANDARD_LIB_PROTOBUF = TEMP_BALLERINA_DIRECTORY + FILE_SEPARATOR +
            TEMP_PROTOBUF_DIRECTORY;
    public static final String EMPTY_STRING = "";

    public static final String COMPONENT_IDENTIFIER = "grpc";
    public static final String PROTOC_PLUGIN_EXE_PREFIX = ".exe";
    public static final String PROTO_SUFFIX = ".proto";
    public static final String DESC_SUFFIX = ".desc";
    public static final String PROTOC_PLUGIN_EXE_URL_SUFFIX = "https://repo1.maven.org/maven2/com/google/" +
            "protobuf/protoc/";
    public static final String TMP_DIRECTORY_PATH = System.getProperty("java.io.tmpdir");
}
