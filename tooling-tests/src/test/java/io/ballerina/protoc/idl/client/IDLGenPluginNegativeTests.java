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
public class IDLGenPluginNegativeTests {
    @Test(description = "Proto directory configuration")
    public void testProtoDirConfiguration() throws IOException, InterruptedException {
        File[] matchingFiles = getMatchingFiles("project_03");
        Assert.assertNull(matchingFiles);
    }

    @Test(description = "Remote proto file configuration")
    public void testRemoteProtoDefinition() throws IOException, InterruptedException {
        File[] matchingFiles = getMatchingFiles("project_04");
        Assert.assertNull(matchingFiles);
    }

    @Test(description = "invalid proto file name")
    public void testInvalidProtoFileName() throws IOException, InterruptedException {
        File[] matchingFiles = getMatchingFiles("project_08");
        Assert.assertNull(matchingFiles);
    }
}
