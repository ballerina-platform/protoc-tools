import ballerina/io;
import ballerina/os;

public function main() {
    // Command to run the gRPC command
    string grpcCommand = "bal grpc --input resources/user_service.proto";

    // Execute the command
    var result = os:executeCommand(grpcCommand, null);

    if (result.returnCode != 0) {
        // Retrieve the standard error (stderr) output
        string stderr = check result.errorOutput;

        // Print the error message along with stderr
        io:println("Error: gRPC command failed with exit status " + result.returnCode);
        io:println("Error Details: " + stderr);
    } else {
        io:println("gRPC command completed successfully.");
    }
}
