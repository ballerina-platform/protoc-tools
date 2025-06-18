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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.ballerina.protoc.core.GrpcConstants.REGEX_DOT_SEPERATOR;
import static io.ballerina.protoc.core.builder.balgen.BalGenerationUtils.getMappingBalType;

/**
 * Message Definition bean class.
 *
 * @since 0.1.0
 */
public class Message {
    private final List<Field> fieldList;
    private String messageName;
    private Map<String, List<Field>> oneofFieldMap;
    private List<EnumMessage> enumList;
    private List<Message> nestedMessageList;
    private List<Message> mapList;

    private Message(String messageName, List<Field> fieldList) {
        this.messageName = messageName;
        this.fieldList = fieldList;
    }

    private void setOneofFieldMap(Map<String, List<Field>> oneofFieldMap) {
        this.oneofFieldMap = oneofFieldMap;
    }

    public Map<String, List<Field>> getOneofFieldMap() {
        return oneofFieldMap;
    }

    public List<EnumMessage> getEnumList() {
        return enumList;
    }

    private void setEnumList(List<EnumMessage> enumList) {
        this.enumList = enumList;
    }

    public void setMapList(List<Message> mapList) {
        this.mapList = mapList;
    }

    public List<Message> getMapList() {
        return mapList;
    }

    public static Builder newBuilder(DescriptorProtos.DescriptorProto messageDescriptor, String packageName) {
        return new Builder(messageDescriptor, messageDescriptor.getName(), packageName);
    }

    public static Builder newBuilder(DescriptorProtos.DescriptorProto messageDescriptor, String messageName,
                                             String packageName) {
        return new Builder(messageDescriptor, messageName, packageName);
    }

    public List<Message> getNestedMessageList() {
        return nestedMessageList;
    }

    private void setNestedMessageList(List<Message> nestedMessageList) {
        this.nestedMessageList = nestedMessageList;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    /**
     * Message Definition.Builder.
     */
    public static class Builder {
        private final DescriptorProtos.DescriptorProto messageDescriptor;
        private final String messageName;
        private final String packageName;

        public Message build() {
            List<Message> nestedMessageList = new ArrayList<>();
            List<Message> mapList = new ArrayList<>();
            List<String> mapNames = new ArrayList<>();
            for (DescriptorProtos.DescriptorProto nestedDescriptorProto : messageDescriptor.getNestedTypeList()) {
                Message nestedMessage;
                if (nestedDescriptorProto.getOptions().hasMapEntry()) {
                    nestedMessage = Message.newBuilder(nestedDescriptorProto, packageName).build();
                } else {
                    nestedMessage = Message.newBuilder(
                            nestedDescriptorProto,
                            messageName + "_" + nestedDescriptorProto.getName(),
                            packageName
                    ).build();
                }
                if (nestedDescriptorProto.hasOptions() && nestedDescriptorProto.getOptions().hasMapEntry()) {
                    mapNames.add(messageName + "_" + nestedDescriptorProto.getName());

                    Optional<DescriptorProtos.FieldDescriptorProto> mapFieldDescriptor =
                            messageDescriptor.getFieldList().stream()
                                    .filter(field -> field.getTypeName().endsWith(nestedMessage.getMessageName()))
                                    .findFirst();
                    mapFieldDescriptor.ifPresent(fieldDescriptorProto ->
                            nestedMessage.setMessageName(fieldDescriptorProto.getName()));
                    mapList.add(nestedMessage);
                } else {
                    nestedMessageList.add(nestedMessage);
                }
            }

            List<EnumMessage> enumList = new ArrayList<>();
            for (DescriptorProtos.EnumDescriptorProto enumDescriptorProto : messageDescriptor.getEnumTypeList()) {
                EnumMessage enumMessage = EnumMessage.newBuilder(
                        enumDescriptorProto,
                        messageName + "_" + enumDescriptorProto.getName()
                ).build();
                enumList.add(enumMessage);
            }

            List<Field> fieldList = new ArrayList<>();
            Map<String, List<Field>> oneofFieldMap = new HashMap<>();
            for (DescriptorProtos.FieldDescriptorProto fieldDescriptorProto : messageDescriptor.getFieldList()) {
                Field field;
                if (isWellKnownProtoType((fieldDescriptorProto.getTypeName()))) {
                    field = Field.newBuilder(
                            fieldDescriptorProto,
                            getMappingBalType(fieldDescriptorProto.getTypeName())
                    ).build();
                } else {
                    field = Field.newBuilder(
                            fieldDescriptorProto,
                            getFieldType(fieldDescriptorProto.getTypeName(), packageName)
                    ).build();
                }
                if (fieldDescriptorProto.hasOneofIndex()) {
                    String oneofField = messageDescriptor.getOneofDecl(fieldDescriptorProto.getOneofIndex()).getName();
                    List<Field> oneofMessageList = oneofFieldMap.computeIfAbsent(oneofField, k -> new ArrayList<>());
                    oneofMessageList.add(field);
                } else if (!mapNames.contains(field.getFieldType())) {
                    fieldList.add(field);
                }
            }

            Message message = new Message(messageName, fieldList);

            if (!oneofFieldMap.isEmpty()) {
                message.setOneofFieldMap(oneofFieldMap);
            }
            if (!enumList.isEmpty()) {
                message.setEnumList(enumList);
            }
            if (!nestedMessageList.isEmpty()) {
                message.setNestedMessageList(nestedMessageList);
            }
            if (!mapList.isEmpty()) {
                message.setMapList(mapList);
            }
            return message;
        }

        private Builder(DescriptorProtos.DescriptorProto messageDescriptor, String messageName, String packageName) {
            this.messageDescriptor = messageDescriptor;
            this.messageName = messageName;
            this.packageName = packageName;
        }
    }

    private static String getFieldType(String typeName, String packageName) {
        StringBuilder fieldType = new StringBuilder();
        if (!packageName.isBlank()) {
            typeName = typeName.replaceFirst("^\\." + packageName, "");
        }
        String[] types =  typeName.split(REGEX_DOT_SEPERATOR);
        for (int i = 1; i < types.length; i++) {
            if (fieldType.toString().isBlank()) {
                fieldType.append(types[i]);
            } else {
                fieldType.append("_").append(types[i]);
            }
        }
        return fieldType.toString();
    }

    private static boolean isWellKnownProtoType(String type) {
        switch (type) {
            case ".google.protobuf.DoubleValue":
            case ".google.protobuf.FloatValue":
            case ".google.protobuf.Int32Value":
            case ".google.protobuf.Int64Value":
            case ".google.protobuf.UInt64Value":
            case ".google.protobuf.UInt32Value":
            case ".google.protobuf.BoolValue":
            case ".google.protobuf.BytesValue":
            case ".google.protobuf.Any":
            case ".google.protobuf.Empty":
            case ".google.protobuf.Timestamp":
            case ".google.protobuf.Duration":
            case ".google.protobuf.Struct":
            case ".google.protobuf.StringValue":
            case "":
                return true;
            default:
                return false;
        }
    }
}
