/*
 * Copyright (c) 2022 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

module io.ballerina.protoc {
    exports io.ballerina.protoc.builder.model;
    exports io.ballerina.protoc.protobuf;
    exports io.ballerina.protoc.protobuf.cmd;
    exports io.ballerina.protoc.protobuf.exception;
    exports io.ballerina.protoc.builder.stub;
    requires proto.google.common.protos;
    requires io.ballerina.runtime;
    requires io.ballerina.lang;
    requires io.ballerina.tools.api;
    requires io.ballerina.parser;
    requires io.ballerina.formatter.core;
    requires io.ballerina.cli;
    requires io.ballerina.toml;
    requires com.google.protobuf;
    requires info.picocli;
    requires org.slf4j;
    requires org.apache.commons.lang3;
}
