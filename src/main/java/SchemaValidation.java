import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

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

    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaNode));

    SchemaValidatorsConfig config = SchemaValidatorsConfig
      .builder()
      .errorMessageKeyword("x-errorMessage")
      .pathType(PathType.JSON_PATH)
      .build();

    JsonSchema jsonSchema = factory.getSchema(schemaNode, config);

    Set<ValidationMessage> errors = jsonSchema.validate(inputNode);

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
  private static void printValidationErrors(Set<ValidationMessage> errors) {
    if (errors == null || errors.isEmpty()) {
      System.out.println("No validation errors.");
      return;
    }

    System.out.println("Validation Errors:");
    for (ValidationMessage error : errors) {
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
