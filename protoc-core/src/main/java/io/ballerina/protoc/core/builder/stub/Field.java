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
package io.ballerina.protoc.core.builder.stub;

import com.google.protobuf.DescriptorProtos;
import io.ballerina.protoc.core.GrpcConstants;
import org.wso2.ballerinalang.compiler.util.Names;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.componentsModuleMap;
import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.enumDefaultValueMap;
import static io.ballerina.protoc.core.builder.BallerinaFileBuilder.protofileModuleMap;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.COLON;
import static io.ballerina.protoc.core.builder.balgen.BalGenConstants.PACKAGE_SEPARATOR;
import static io.ballerina.protoc.core.builder.stub.utils.StubUtils.RESERVED_LITERAL_NAMES;

/**
 * Field definition bean class.
 *
 * @since 0.1.0
 */
public class Field {
    private final String fieldType;
    private final String fieldLabel;
    private final String fieldName;
    private final String defaultValue;
    private final String moduleName;

    private Field(String fieldName, String fieldType, String fieldLabel, String defaultValue, String moduleName) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldLabel = fieldLabel;
        this.defaultValue = defaultValue;
        this.moduleName = moduleName;
    }

    public static Builder newBuilder(DescriptorProtos.FieldDescriptorProto fieldDescriptor) {
        return new Builder(fieldDescriptor, fieldDescriptor.getTypeName());
    }

    public static Builder newBuilder(DescriptorProtos.FieldDescriptorProto fieldDescriptor, String fieldType) {
        return new Builder(fieldDescriptor, fieldType);
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDefaultValue(String filename) {
        if (isFieldInsideSubModule(filename)) {
            return moduleName + COLON + defaultValue;
        }
        return defaultValue;
    }

    public String getPackageName(String filename) {
        if (isFieldInsideSubModule(filename)) {
            return moduleName + COLON;
        }
        return "";
    }

    private boolean isFieldInsideSubModule(String filename) {
        return !moduleName.isEmpty() && protofileModuleMap.containsKey(filename) &&
                !protofileModuleMap.get(filename).substring(protofileModuleMap.get(filename)
                        .lastIndexOf(PACKAGE_SEPARATOR) + 1).equals(moduleName);
    }

    /**
     * Field Definition.Builder.
     */
    public static class Builder {
        private final DescriptorProtos.FieldDescriptorProto fieldDescriptor;
        private final String type;

        public Field build() {
            String fieldType = type;
            String fieldDefaultValue = null;
            if (CUSTOM_FIELD_TYPE_MAP.get(fieldDescriptor.getTypeName()) != null) {
                fieldType = CUSTOM_FIELD_TYPE_MAP.get(fieldDescriptor.getTypeName());
                fieldDefaultValue = CUSTOM_DEFAULT_VALUE_MAP.get(fieldDescriptor.getTypeName());
            } else if (FIELD_TYPE_MAP.get(fieldDescriptor.getType()) != null) {
                fieldType = FIELD_TYPE_MAP.get(fieldDescriptor.getType());
                fieldDefaultValue = FIELD_DEFAULT_VALUE_MAP.get(fieldDescriptor.getType());
            }

            if (fieldType.startsWith(GrpcConstants.DOT)) {
                String[] fieldTypeArray = fieldType.split(GrpcConstants.REGEX_DOT_SEPERATOR);
                fieldType = fieldTypeArray[fieldTypeArray.length - 1];
            }

            String fieldLabel = FIELD_LABEL_MAP.get(fieldDescriptor.getLabel());
            if (fieldDescriptor.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED) {
                fieldDefaultValue = fieldLabel;
            }
            String fieldName = fieldDescriptor.getName();
            if (Arrays.stream(RESERVED_LITERAL_NAMES).anyMatch(fieldName::equalsIgnoreCase) || Names.ERROR.getValue()
                    .equalsIgnoreCase(fieldName)) {
                fieldName = "'" + fieldName;
            }
            if (fieldDescriptor.getType().equals(DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM)) {
                fieldDefaultValue = enumDefaultValueMap.get(fieldType);
            }
            String moduleName = "";
            if (fieldType.length() > 0 && componentsModuleMap.containsKey(fieldType)) {
                moduleName = componentsModuleMap.get(fieldType).substring(componentsModuleMap.get(fieldType)
                        .lastIndexOf(PACKAGE_SEPARATOR) + 1);
            }
            return new Field(fieldName, fieldType, fieldLabel, fieldDefaultValue, moduleName);
        }

        private Builder(DescriptorProtos.FieldDescriptorProto fieldDescriptor, String fieldType) {
            this.fieldDescriptor = fieldDescriptor;
            this.type = fieldType != null ? fieldType : "empty:Empty";
        }
    }

    private static final Map<DescriptorProtos.FieldDescriptorProto.Type, String> FIELD_TYPE_MAP;
    private static final Map<String, String> CUSTOM_FIELD_TYPE_MAP;
    private static final Map<DescriptorProtos.FieldDescriptorProto.Label, String> FIELD_LABEL_MAP;
    private static final Map<DescriptorProtos.FieldDescriptorProto.Type, String> FIELD_DEFAULT_VALUE_MAP;
    private static final Map<String, String> CUSTOM_DEFAULT_VALUE_MAP;

    static {
        FIELD_TYPE_MAP = new HashMap<>();
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, "float");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, "float");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, "int:Unsigned32");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, "int:Signed32");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, "int");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, "boolean");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, "string");
        FIELD_TYPE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES, "byte[]");

        CUSTOM_FIELD_TYPE_MAP = new HashMap<>();
        CUSTOM_FIELD_TYPE_MAP.put(".google.protobuf.Any", "'any:Any");
        CUSTOM_FIELD_TYPE_MAP.put(".google.protobuf.Timestamp", "time:Utc");
        CUSTOM_FIELD_TYPE_MAP.put(".google.protobuf.Duration", "time:Seconds");

        FIELD_DEFAULT_VALUE_MAP = new HashMap<>();
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, "0.0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, "0.0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, "0");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, "false");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, "");
        FIELD_DEFAULT_VALUE_MAP.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES, "[]");

        CUSTOM_DEFAULT_VALUE_MAP = new HashMap<>();
        CUSTOM_DEFAULT_VALUE_MAP.put(".google.protobuf.Any", "()");
        CUSTOM_DEFAULT_VALUE_MAP.put(".google.protobuf.Timestamp", "[0, 0.0d]");
        CUSTOM_DEFAULT_VALUE_MAP.put(".google.protobuf.Duration", "0.0d");

        FIELD_LABEL_MAP = new HashMap<>();
        FIELD_LABEL_MAP.put(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL, null);
        FIELD_LABEL_MAP.put(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED, null);
        FIELD_LABEL_MAP.put(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED, "[]");
    }
}
