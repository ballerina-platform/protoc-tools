package io.ballerina.protoc.idl.client;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Integration tests for IDL support.
 *
 * @since 0.1.0
 */
public class TestUtils {
    public static final PrintStream OUT = System.out;
    public static final Path TARGET_DIR = Paths.get(System.getProperty("target.dir"));
    public static final Path RESOURCE = Paths.get(System.getProperty("user.dir")).resolve("build/resources/" +
            "test/protoc-idl-client-projects");
    public static final Path TEST_DISTRIBUTION_PATH = TARGET_DIR.resolve(System.getProperty("ballerina.version"));
    public static final String DISTRIBUTION_FILE_NAME = System.getProperty("ballerina.version");
    private static String balFile = "bal";

    public static boolean checkModuleAvailability(String project) throws IOException, InterruptedException {
        Process process = executeRun(RESOURCE.resolve(project));
        File dir = new File(new File(String.valueOf(RESOURCE.resolve(project).resolve("generated"))).toString());
        return dir.exists();
    }

    public static File[] getMatchingFiles(String project, List<String> ids) {
        File dir = new File(RESOURCE.resolve(project + "/generated/").toString());
        File[] matchingFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                for (String id : ids) {
                    if (pathname.getName().contains(id)) {
                        return true;
                    }
                }
                return false;
            }
        });
        return matchingFiles;
    }

    /**
     * Execute Ballerina run command.
     */
    public static Process executeRun(Path sourceDirectory)
            throws IOException, InterruptedException {
        List<String> args = new LinkedList<>();
        args.add(0, "run");
        Process process = getProcessBuilderResults(sourceDirectory, args);
        process.waitFor();
        return process;
    }

    /**
     *  Get Process from given arguments.
     * @param sourceDirectory  The directory where the sources files are location.
     * @param args             The arguments to be passed to the build command.
     * @return process
     * @throws IOException          Error executing build command.
     */
    public static Process getProcessBuilderResults(Path sourceDirectory, List<String> args)
            throws IOException {

        if (System.getProperty("os.name").startsWith("Windows")) {
            balFile = "bal.bat";
        }
        args.add(0, TEST_DISTRIBUTION_PATH.resolve("bin").resolve(balFile).toString());
        OUT.println("Executing: " + StringUtils.join(args, ' '));
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(sourceDirectory.toFile());
        Process process = pb.start();
        return process;
    }

    /**
     * Log the output of an input stream.
     *
     * @param inputStream The stream.
     * @throws IOException Error reading the stream.
     */
    private static void logOutput(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.lines().forEach(OUT::println);
        }
    }
}

