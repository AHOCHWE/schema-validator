import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SchemaRegistryConfig;
import com.networknt.schema.SpecificationVersion;
import com.networknt.schema.path.PathType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class SchemaValidation {
  //static String INPUT_FILE = "Missing_ApplicationId.json";
  static String INPUT_FILE = "Success.json";

  static boolean DEBUG = true;
  static String CLASSPATH = "src/main/resources/";
  static final ObjectMapper MAPPER = new ObjectMapper();

  public static void main(String[] args) throws IOException {
    // Load JSON schema from classpath (src/main/resources)
    JsonNode schemaNode = loadJsonFromClasspath("schemas/ApplicationRequest.json");

    if (DEBUG) System.out.println(schemaNode);

    // Load JSON Input from classpath (src/main/resources)
    //JsonNode inputNode = loadJsonFile(CLASSPATH + INPUT_FILE);
    JsonNode inputNode = loadJsonFromClasspath(INPUT_FILE);

    if (DEBUG) System.out.println(inputNode);

    /* Works with 1.4.3
     * JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaNode));
     *
     * SchemaValidatorsConfig config = SchemaValidatorsConfig
     * .builder()
     * .errorMessageKeyword("x-errorMessage")
     * .pathType(PathType.JSON_PATH)
     * .build();
     *
     * JsonSchema jsonSchema = factory.getSchema(schemaNode, config);
     *
     * Set<ValidationMessage> errors = jsonSchema.validate(inputNode);
     * printValidationMessages(errors);
     */

    SchemaRegistryConfig config = SchemaRegistryConfig
      .builder()
      .errorMessageKeyword("x-errorMessage")
      .pathType(PathType.JSON_PATH)
      .build();

    SchemaRegistry registry = SchemaRegistry.withDefaultDialect(
      SpecificationVersion.DRAFT_7,
      builder -> builder.schemaRegistryConfig(config)
    );

    Schema schema = registry.getSchema(schemaNode);
    List<Error> errors = schema.validate(inputNode);
    printValidationErrors(errors);
  }

  /**
   * Loads a JSON file from the file system and parses it into a JsonNode.
   *
   * @param filePath path to the JSON file
   * @return parsed JsonNode
   * @throws RuntimeException if the file is not found or cannot be parsed
   */
  private static JsonNode loadJsonFile(String filePath) {
    try {
      File file = new File(filePath);
      if (!file.exists()) {
        throw new RuntimeException("File not found: " + file.getAbsolutePath());
      }
      return MAPPER.readTree(file);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load JSON from file: " + filePath, e);
    }
  }

  /**
   * Prints all validation errors from a set of ValidationMessage.
   *
   * @param errors set of validation messages
   */
  /* Works with 1.4.3
   * private static void printValidationMessages(Set<ValidationMessage> errors) {
   * if (errors == null || errors.isEmpty()) {
   * System.out.println("No validation errors."); return; }
   *
   * System.out.println("Validation Errors:"); for (ValidationMessage error :
   * errors) { System.out.println("- " + error.getMessage()); } }
   */

  /**
   * Prints all validation errors.
   *
   * @param errors list of validation errors
   */
  private static void printValidationErrors(List<Error> errors) {
    if (errors == null || errors.isEmpty()) {
      System.out.println("No validation errors.");
      return;
    }

    System.out.println("Validation Errors:");
    for (Error error : errors) {
      System.out.println("- " + error.getMessage());
    }
  }

  /**
   * Loads a JSON file from the classpath and parses it into a JsonNode.
   *
   * @param resourcePath path to the JSON resource (e.g., "schemas/ApplicationRequest.json")
   * @return parsed JsonNode
   * @throws RuntimeException if the resource is not found or cannot be parsed
   */
  private static JsonNode loadJsonFromClasspath(String resourcePath) {
    try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new RuntimeException("Resource not found in classpath: " + resourcePath);
      }
      return MAPPER.readTree(in);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load JSON from: " + resourcePath, e);
    }
  }
}
