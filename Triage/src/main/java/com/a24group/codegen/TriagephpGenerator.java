package com.a24group.codegen;

import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.CodegenConstants;
import io.swagger.codegen.CodegenModelFactory;
import io.swagger.codegen.CodegenModelType;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.CodegenResponse;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.DefaultCodegen;
import io.swagger.codegen.SupportingFile;
import io.swagger.models.Response;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.a24group.codegen.TriagePhpCodegenResponse;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class TriagephpGenerator extends DefaultCodegen implements CodegenConfig {

  protected String sourceFolder = "src";
  protected String apiVersion = "1.0.0";
  final protected String AUTHOR = "Code Gen <code.gen@a24testmail.com>";
  protected String exceptionCategory = "";
  protected String exceptionPackage = "";
  protected String exceptionSubPackage = "";
  protected String modelCategory = "";
  protected String modelDirName = "Model//Resource";
  protected String apiDirName = "Model//EndPoints";
  protected String resourcePackage = "";
  protected String apiSubPackage = "";
  protected String apiCategory = "";
  protected HashMap<String, String> resourceConfigConstantFieldExceptions = new HashMap<String, String>();

  /**
   * Configures the type of generator.
   *
   * @return  the CodegenType for this generator
   * @see     io.swagger.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the generator
   * to select the library with the -l flag.
   *
   * @return the friendly name for the generator
   */
  public String getName() {
    return "TriagePhp";
  }

  /**
   * Returns human-friendly help for the generator.  Provide the consumer with help
   * tips, parameters here
   *
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a TriagePhp client library.";
  }

  public TriagephpGenerator() {
    super();

    // Set the resonse class that should be returned from factory
    CodegenModelFactory.setTypeMapping(CodegenModelType.RESPONSE, TriagePhpCodegenResponse.class);

    // Create a list of exceptions for constant naming
    resourceConfigConstantFieldExceptions.put("__v", "version");
    resourceConfigConstantFieldExceptions.put("_id", "id");

    // clear import mapping (from default generator) as php does not use it
    // at the moment
    importMapping.clear();
    supportsInheritance = true;
    // set the output folder here TODO FIX THE OUTPUT FOLDER!!
    outputFolder = "TriageGen\\src\\a24group\\codegen";

    /**
     * Models. You can write model files using the modelTemplateFiles map.
     * if you want to create one template for file, you can do so here.
     * for multiple files for model, just put another entry in the `modelTemplateFiles` with
     * a different extension
     */
    // Generate the ResourceConfig files
    modelTemplateFiles.put(
        "modelResourceConfig.mustache",
        "ResourceConfig.php"
    );
    // Generate the VO files
    modelTemplateFiles.put(
        "modelVO.mustache",
        "VO.php"
    );

    /**
     * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
     * as with models, add multiple entries with different extensions for multiple files per
     * class
     */
    apiTemplateFiles.put(
        "api.mustache",
        "EndPoint.php"
    );

    /**
     * Template Location.  This is the location which templates will be read from. The generator
     * will use the resource stream to attempt to read the templates.
     */
    templateDir = "TriagePhp";

    resourcePackage = "a24group\\Resource";
    modelCategory = "a24group";
    modelPackage = "a24group\\Model";
    apiCategory = "a24group";
    apiPackage = "a24group\\Model";
    apiSubPackage = "a24group\\Model\\EndPoints";
    exceptionCategory = "a24group";
    exceptionPackage = "a24group\\Model";
    exceptionSubPackage = "a24group\\Model\\Exception";

    // ref: http://php.net/manual/en/language.types.intro.php
    languageSpecificPrimitives = new HashSet<String>(
        Arrays.asList(
            "bool",
            "boolean",
            "int",
            "integer",
            "double",
            "float",
            "string",
            "object",
            "DateTime",
            "mixed",
            "number",
            "void",
            "byte",
            "self::DATA_TYPE_INTEGER",
            "self::DATA_TYPE_STRING",
            "self::DATA_TYPE_BOOLEAN",
            "self::DATA_TYPE_FLOAT"
        )
    );

    instantiationTypes.put("array", "array");
    instantiationTypes.put("map", "map");

    setReservedWordsLowerCase(
        Arrays.asList(
            // local variables used in api methods (endpoints)
            "resourcePath", "httpBody", "queryParams", "headerParams",
            "formParams", "_header_accept", "_tempBody",

            // PHP reserved words
            "__halt_compiler", "abstract", "and", "array", "as", "break", "callable", "case", "catch", "class", "clone", "const", "continue", "declare", "default", "die", "do", "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach", "endif", "endswitch", "endwhile", "eval", "exit", "extends", "final", "for", "foreach", "function", "global", "goto", "if", "implements", "include", "include_once", "instanceof", "insteadof", "interface", "isset", "list", "namespace", "new", "or", "print", "private", "protected", "public", "require", "require_once", "return", "static", "switch", "throw", "trait", "try", "unset", "use", "var", "while", "xor")
    );

    // ref: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#data-types
    // Not all types are supported, only mapped the types that match our RC values
    typeMapping = new HashMap<String, String>();
    typeMapping.put("integer", "self::DATA_TYPE_INTEGER");
    typeMapping.put("long", "self::DATA_TYPE_INTEGER");
    typeMapping.put("number", "self::DATA_TYPE_FLOAT");
    typeMapping.put("float", "self::DATA_TYPE_FLOAT");
    typeMapping.put("double", "self::DATA_TYPE_FLOAT");
    typeMapping.put("string", "self::DATA_TYPE_STRING");
    typeMapping.put("byte", "self::DATA_TYPE_INTEGER");
    typeMapping.put("boolean", "self::DATA_TYPE_BOOLEAN");
    typeMapping.put("Date", "self::DATA_TYPE_DATE");
    typeMapping.put("DateTime", "self::DATA_TYPE_DATETIME");
    typeMapping.put("file", "\\SplFileObject");
    typeMapping.put("map", "map");
    typeMapping.put("array", "self::DATA_TYPE_ARRAY");
    typeMapping.put("list", "array");
    typeMapping.put("object", "NOT_SUPPORTED");
    typeMapping.put("binary", "string");
    typeMapping.put("UUID", "self::DATA_TYPE_STRING");

    /**
     * Additional Properties. These values can be passed to the templates and
     * are available in models, apis, and supporting files
     *
     * These properties are set for all fields. Its not a per file system.
     */
    additionalProperties.put("apiVersion", apiVersion);
    additionalProperties.put("modelCategory", modelCategory);
    additionalProperties.put(CodegenConstants.MODEL_PACKAGE, modelPackage);
    additionalProperties.put(CodegenConstants.API_PACKAGE, apiPackage);
    additionalProperties.put("apiCategory", apiCategory);
    additionalProperties.put("apiSubPackage", apiSubPackage);
    additionalProperties.put("resourcePackage", resourcePackage);
    additionalProperties.put("exceptionCategory", exceptionCategory);
    additionalProperties.put("exceptionPackage", exceptionPackage);
    additionalProperties.put("exceptionSubPackage", exceptionSubPackage);
    additionalProperties.put("author", AUTHOR);
    additionalProperties.put("initialCaps", new InitialCapsLambda());

    /**
     * Supporting Files.  You can write single files for the generator with the
     * entire object tree available.  If the input file has a suffix of `.mustache
     * it will be processed by the template engine.  Otherwise, it will be copied
     */
    supportingFiles.add(new SupportingFile("driver.mustache", "Model", "Driver.php"));
    supportingFiles.add(new SupportingFile("client.mustache", "Model", "Client.php"));
    supportingFiles.add(new SupportingFile("runtime_exception.mustache", "Model\\Exception", "RuntimeException.php"));
  }

  /**
   * Location to write model files.  You can use the modelPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String modelFileFolder() {
      return (outputFolder + "/" + modelDirName);
  }

  /**
   * Location to write api files.  You can use the apiPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String apiFileFolder() {
      return (outputFolder + "/" + apiDirName);
  }

  /**
   * Optional - type declaration.  This is a String which is used by the templates to instantiate your
   * types.  There is typically special handling for different property types
   *
   * @return a string value used as the `dataType` field for model templates, `returnType` for api templates
   */
  @Override
  public String getTypeDeclaration(Property p) {
    if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getSwaggerType(p);
    } else if (p instanceof MapProperty) {
      MapProperty mp = (MapProperty) p;
      Property inner = mp.getAdditionalProperties();
      return getSwaggerType(p) + "[String, " + getTypeDeclaration(inner) + "]";
    }
    return super.getTypeDeclaration(p);
  }

  /**
   * Optional - swagger type conversion.  This is used to map swagger types in a `Property` into
   * either language specific types via `typeMapping` or into complex models if there is not a mapping.
   *
   * @return a string value of the type or complex model for this property
   * @see io.swagger.models.properties.Property
   */
  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if (typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if (languageSpecificPrimitives.contains(type))
        return type;
    } else {
        type = initialCaps(swaggerType);
    }
    return type;
  }

  @Override
  protected void updatePropertyForArray(CodegenProperty property, CodegenProperty innerProperty) {
      if (innerProperty == null) {
          LOGGER.warn("skipping invalid array property " + Json.pretty(property));
      } else {
          if (!languageSpecificPrimitives.contains(innerProperty.baseType)) {
              if (typeMapping.containsValue(innerProperty.baseType)) {
                  Set<String> typeNames = typeMapping.keySet();
                  for (String key: typeNames) {
                    if (typeMapping.get(key).equals(innerProperty.baseType)) {
                        property.isPrimitiveType = false;
                        // TODO Added inner type isPrimitiveType
                        innerProperty.isPrimitiveType = true;
                    }
                }

                if (!property.isPrimitiveType) {
                    property.complexType = innerProperty.baseType;
                }
              } else {
                  property.complexType = innerProperty.baseType;
              }
          } else {
              property.isPrimitiveType = true;
          }
          property.items = innerProperty;
          // inner item is Enum
          if (isPropertyInnerMostEnum(property)) {
              // isEnum is set to true when the type is an enum
              // or the inner type of an array/map is an enum
              property.isEnum = true;
              // update datatypeWithEnum and default value for array
              // e.g. List<string> => List<StatusEnum>
              updateDataTypeWithEnumForArray(property);
              // set allowable values to enum values (including array/map of enum)
              property.allowableValues = getInnerEnumAllowableValues(property);
          }
      }
  }

  /**
   * Convert Swagger Response object to Codegen Response object
   *
   * @param responseCode HTTP response code
   * @param response Swagger Response object
   * @return Codegen Response object
   */
  @Override
  public CodegenResponse fromResponse(String responseCode, Response response) {
      CodegenResponse r = super.fromResponse(responseCode, response);
      TriagePhpCodegenResponse tr = (TriagePhpCodegenResponse) r;

      // Used to determine whether the response is a success or not. This way
      // you can decide to error or return the success response
      tr.isSuccess = false;
      if (!responseCode.equals("default")) {
          int statusCode = Integer.parseInt(responseCode);
          if (statusCode >= 200 && statusCode < 300) {
              tr.isSuccess = true;
          }
      }
      return tr;
  }

  private static class InitialCapsLambda extends TriageCustomLambda {
      @Override
      public String initialCaps(String value) {
          return StringUtils.capitalize(value);
      }
  }

  private static abstract class TriageCustomLambda implements Mustache.Lambda {
      @Override
      public void execute(Template.Fragment frag, Writer out) throws IOException {
          final StringWriter tempWriter = new StringWriter();
          frag.execute(tempWriter);
          out.write(initialCaps(tempWriter.toString()));
      }

      public abstract String initialCaps(String value);
  }
}