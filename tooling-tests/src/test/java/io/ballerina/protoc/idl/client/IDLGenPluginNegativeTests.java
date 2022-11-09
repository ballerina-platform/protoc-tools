package io.ballerina.protoc.idl.client;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.ballerina.protoc.idl.client.TestUtils.checkModuleAvailability;

/**
 * Client IDL import integration tests.
 *
 * @since 0.1.0
 */
public class IDLGenPluginNegativeTests {
    @Test(description = "Proto directory configuration")
    public void testProtoDirConfiguration() throws IOException, InterruptedException {
        Assert.assertFalse(checkModuleAvailability("project_03"));
    }

    @Test(description = "Remote proto file configuration", enabled = false)
    public void testRemoteProtoDefinition() throws IOException, InterruptedException {
        Assert.assertFalse(checkModuleAvailability("project_04"));
    }

    @Test(description = "invalid proto file name", enabled = false)
    public void testInvalidProtoFileName() throws IOException, InterruptedException {
        Assert.assertFalse(checkModuleAvailability("project_05"));
    }
}
