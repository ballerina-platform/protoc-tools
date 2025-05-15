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
package io.ballerina.protoc.cli;

import io.ballerina.protoc.core.BalGenerationConstants;

import java.util.Locale;

import static io.ballerina.protoc.cli.SupportOSTypes.AIX;
import static io.ballerina.protoc.cli.SupportOSTypes.FREEBSD;
import static io.ballerina.protoc.cli.SupportOSTypes.HPUX;
import static io.ballerina.protoc.cli.SupportOSTypes.LINUX;
import static io.ballerina.protoc.cli.SupportOSTypes.MACOSX;
import static io.ballerina.protoc.cli.SupportOSTypes.NETBSD;
import static io.ballerina.protoc.cli.SupportOSTypes.OPENBSD;
import static io.ballerina.protoc.cli.SupportOSTypes.OS400;
import static io.ballerina.protoc.cli.SupportOSTypes.OSX;
import static io.ballerina.protoc.cli.SupportOSTypes.SOLARIS;
import static io.ballerina.protoc.cli.SupportOSTypes.SUNOS;
import static io.ballerina.protoc.cli.SupportOSTypes.WINDOWS;

/**
 * Class for detecting the system operating system version and type.
 * Ref : https://github.com/trustin/os-maven-plugin/blob/master/src/main/java/kr/motd/maven/os/Detector.java
 */
abstract class OSDetector {
    
    private static final String UNKNOWN = "unknown";
    
    static String getDetectedClassifier() {
        
        final String osName = System.getProperty(BalGenerationConstants.OS_NAME_SYSTEM_PROPERTY);
        final String osArch = System.getProperty(BalGenerationConstants.OS_ARCH_SYSTEM_PROPERTY);
        
        final String detectedName = normalizeOs(osName);
        final String detectedArch = normalizeArch(osArch);
        
        final String failOnUnknownOS = System.getProperty("failOnUnknownOS");
        if (!"false".equalsIgnoreCase(failOnUnknownOS)) {
            if (UNKNOWN.equals(detectedName)) {
                throw new RuntimeException("unknown os.name: " + osName);
            }
            if (UNKNOWN.equals(detectedArch)) {
                throw new RuntimeException("unknown os.arch: " + osArch);
            }
        }
        // Assume the default classifier, without any os "like" extension.
        return detectedName + '-' + detectedArch;
    }
    
    private static String normalizeOs(String value) {
        
        value = normalize(value);
        if (value.startsWith(AIX)) {
            return AIX;
        }
        if (value.startsWith(HPUX)) {
            return HPUX;
        }
        if (value.startsWith(OS400)) {
            // Avoid the names such as os4000
            if (value.length() <= 5 || !Character.isDigit(value.charAt(5))) {
                return OS400;
            }
        }
        if (value.startsWith(LINUX)) {
            return LINUX;
        }
        if (value.startsWith(MACOSX) || value.startsWith(OSX)) {
            return "osx";
        }
        if (value.startsWith(FREEBSD)) {
            return FREEBSD;
        }
        if (value.startsWith(OPENBSD)) {
            return OPENBSD;
        }
        if (value.startsWith(NETBSD)) {
            return NETBSD;
        }
        if (value.startsWith(SOLARIS) || value.startsWith(SUNOS)) {
            return SUNOS;
        }
        if (value.startsWith(WINDOWS)) {
            return WINDOWS;
        }
        return UNKNOWN;
    }
    
    private static String normalizeArch(String value) {
        
        value = normalize(value);
        if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
            return "x86_64";
        }
        if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
            return "x86_32";
        }
        if (value.matches("^(ia64|itanium64)$")) {
            return "itanium_64";
        }
        if (value.matches("^(sparc|sparc32)$")) {
            return "sparc_32";
        }
        if (value.matches("^(sparcv9|sparc64)$")) {
            return "sparc_64";
        }
        if (value.matches("^(arm|arm32)$")) {
            return "arm_32";
        }
        if ("aarch64".equals(value)) {
            return "aarch_64";
        }
        if (value.matches("^(ppc|ppc32)$")) {
            return "ppc_32";
        }
        if ("ppc64".equals(value)) {
            return "ppc_64";
        }
        if ("ppc64le".equals(value)) {
            return "ppcle_64";
        }
        if ("s390".equals(value)) {
            return "s390_32";
        }
        if ("s390x".equals(value)) {
            return "s390_64";
        }
        return UNKNOWN;
    }
    
    private static String normalize(String value) {
        
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
    }
}
