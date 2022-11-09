package io.ballerina.protoc.idl.client;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static io.ballerina.protoc.idl.client.TestUtils.checkModuleAvailability;
import static io.ballerina.protoc.idl.client.TestUtils.getMatchingFiles;

/**
 * Client IDL import integration tests.
 *
 * @since 0.1.0
 */
public class IDLGenPluginTests {

    @Test(description = "Single client declaration in module level")
    public void testSingleClientDeclaration() throws IOException, InterruptedException {
        Assert.assertTrue(checkModuleAvailability("project_01"));
        List<String> ids = new LinkedList<>();
        ids.add("foo");
        File[] matchingFiles = getMatchingFiles("project_01", ids);
        Assert.assertNotNull(matchingFiles);
    }

    @Test(description = "two declarations in module level")
    public void testMultiClientDeclaration() throws IOException, InterruptedException {
        Assert.assertTrue(checkModuleAvailability("project_02"));
        List<String> ids = new LinkedList<>();
        ids.add("foo");
        File[] matchingFiles = getMatchingFiles("project_02", ids);
        Assert.assertNotNull(matchingFiles);
    }
}
