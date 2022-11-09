package io.ballerina.protoc.idl.client;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static io.ballerina.protoc.idl.client.TestUtils.getMatchingFiles;

/**
 * Client IDL import integration tests.
 *
 * @since 0.1.0
 */
public class IDLGenPluginTests {

    @Test(description = "Single client declaration in module level")
    public void testSingleClientDeclaration() throws IOException, InterruptedException {
        File[] matchingFiles = getMatchingFiles("project_01");
        Assert.assertNotNull(matchingFiles);
        Assert.assertEquals(matchingFiles.length, 1);
    }

    @Test(description = "two declarations in module level")
    public void testMultiClientDeclaration() throws IOException, InterruptedException {
        File[] matchingFiles = getMatchingFiles("project_02");
        Assert.assertNotNull(matchingFiles);
        Assert.assertEquals(matchingFiles.length, 1);
    }
}
