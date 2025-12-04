# JSON Schema Validator <!-- omit in toc -->

A simple Java utility to validate JSON files against JSON Schemas using **Jackson*- and **NetworkNT JSON Schema Validator**.

- [Overview](#overview)
- [Setup](#setup)
- [Usage](#usage)
- [Features](#features)
- [Example Output](#example-output)
- [Notes](#notes)

## Overview

This project lets you:

- Load a JSON Schema from `src/main/resources/schemas/`.
- Load a JSON file from `src/main/resources/`.
- Validate the JSON against the schema.
- Print validation errors (or confirm the JSON is valid).

## Setup

1. Clone the repository.
2. Open the project in **STS*- or any Java IDE.
3. Ensure your JSON schemas are in `src/main/resources/schemas/`.
4. Place JSON input files in `src/main/resources/`.
5. Add Maven dependencies to `pom.xml` (if not already present):

```xml
<dependencies>
    <dependency>
        <groupId>com.networknt</groupId>
        <artifactId>json-schema-validator</artifactId>
        <version>1.0.76</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>
```

## Usage

1. Open `SchemaValidation.java`.
2. Set the JSON input file:

```java
static String INPUT_FILE = "Success.json"; // or any other JSON file
```

3. Run the class as a Java Application.
4. Check console output:

- **No validation errors**-* → JSON is valid.
- **Validation errors**-* → Lists all errors with paths and messages.

## Features

- Automatic detection of JSON Schema versions.
- Custom error messages with `x-errorMessage`.
- Debug mode (`DEBUG = true`) prints the loaded JSON and schema.

## Example Output

**Valid JSON:**

```
No validation errors.
```

**Invalid JSON:**

```
Validation Errors:
- $.applicationId: must not be null
- $.customer.name: required field missing
```

## Notes

- Works with Java 11+.
- Ideal for testing JSON payloads before API calls.
