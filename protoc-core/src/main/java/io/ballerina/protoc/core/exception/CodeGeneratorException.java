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
package io.ballerina.protoc.core.exception;

/**
 * Thrown to indicate that the requested field type is not supported.
 *
 */
public class CodeGeneratorException extends Exception {

    /**
     * Constructs an UnsupportedFieldTypeException with the specified detail message.
     *
     * @param message the detail message
     */
    public CodeGeneratorException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public CodeGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
