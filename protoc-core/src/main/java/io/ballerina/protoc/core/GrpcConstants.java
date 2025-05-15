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

package io.ballerina.protoc.core;

import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BString;

import static io.ballerina.protoc.core.ModuleUtils.getModule;
import static io.ballerina.runtime.api.constants.RuntimeConstants.ORG_NAME_SEPARATOR;

/**
 * Proto Message Constants Class.
 *
 * @since 1.0.0
 */
public class GrpcConstants {

    public static final String REGEX_DOT_SEPERATOR = "\\.";
    public static final String DOT = ".";

    public static final String PROTOCOL_PACKAGE_GRPC = "grpc";
    public static final String PROTOCOL_PACKAGE_VERSION_GRPC = getModule().getMajorVersion();
    public static final String ORG_NAME = "ballerina";
    public static final String PROTOCOL_STRUCT_PACKAGE_GRPC = ORG_NAME + ORG_NAME_SEPARATOR +
            "grpc:" + PROTOCOL_PACKAGE_VERSION_GRPC;
    public static final String PROTOCOL_STRUCT_PACKAGE_PROTOBUF = ORG_NAME + ORG_NAME_SEPARATOR +
            "protobuf:" + PROTOCOL_PACKAGE_VERSION_GRPC;

    public static final String ANN_SERVICE_DESCRIPTOR = "ServiceDescriptor";
    public static final BString ANN_SERVICE_DESCRIPTOR_FQN = StringUtils.fromString(PROTOCOL_STRUCT_PACKAGE_GRPC +
            ":" + ANN_SERVICE_DESCRIPTOR);
    public static final String ANN_DESCRIPTOR = "Descriptor";
    public static final BString ANN_DESCRIPTOR_FQN = StringUtils.fromString(PROTOCOL_STRUCT_PACKAGE_GRPC + ":" +
            ANN_DESCRIPTOR);
    public static final BString ANN_PROTOBUF_DESCRIPTOR = StringUtils.fromString(PROTOCOL_STRUCT_PACKAGE_PROTOBUF +
            ":" + ANN_DESCRIPTOR);
    public static final String DESCRIPTOR_MAP = "_DESCRIPTOR_MAP";
}
