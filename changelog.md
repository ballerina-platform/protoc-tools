# Change Log
This file contains all the notable changes done to the Ballerina protoc-tools package through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.1] - 2024-10-07

### Fixed

- [Fix the NPE when there is message field type of google.protobuf.Empty](https://github.com/ballerina-platform/ballerina-library/issues/7230)
- [Fix the issue in showing the actual error message](https://github.com/ballerina-platform/ballerina-library/issues/7238)


## [0.3.0] - 2024-06-27

### Fixed

- [Isolate `grpc:Caller` and `grpc:StreamingClient`](https://github.com/ballerina-platform/ballerina-library/issues/6656)

## [0.2.0] - 2023-09-18

### Added

- [Support subtypes of int in protoc-tool and gRPC](https://github.com/ballerina-platform/ballerina-standard-library/issues/4543)

## [0.1.2] - 2023-07-03

### Fixed

- [Add descriptor map to `grpc:Descriptor` and stub initialization](https://github.com/ballerina-platform/ballerina-standard-library/issues/4555)
- [Tool does not properly handle the types from google.protobuf.wrappers in messages](https://github.com/ballerina-platform/ballerina-standard-library/issues/4576)

## [0.1.1] - 2023-06-01

### Fixed

- [Fix required input error when `help` command is provided](https://github.com/ballerina-platform/ballerina-standard-library/issues/4446)

## [0.1.0] - 2023-02-24

### Added
- [Implement protobuf stub generation through protoc-tools](https://github.com/ballerina-platform/ballerina-standard-library/issues/3019)
