package com.a24group.codegen;

import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.CodegenConstants;
import io.swagger.codegen.CodegenModelFactory;
import io.swagger.codegen.CodegenModelType;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.CodegenResponse;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.DefaultCodegen;
import io.swagger.codegen.SupportingFile;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class TriagephpGenerator extends DefaultCodegen implements CodegenConfig {

  protected String sourceFolder = "src";
  protected String apiVersion = "1.0.0";
  final protected String AUTHOR = "Code Gen <code.gen@a24testmail.com>";
  protected String moduleName = "a24GroupModule";
  protected String driverName = "a24GroupDriver";
  protected String modelDirName = "Model//Resource";
  protected String apiDirName = "Model//EndPoint";
  protected String modelSubPackage = "";
  protected String resourcePackage = "";
  protected String folderPackage = "";
  protected String packageDir = "";
  protected String resourceSubPackage = "";
  protected String apiSubPackage = "";
  protected String apiCategory = "";
  protected String modelTestCategory = "";
  protected String modelTestPackage = "";
  protected String modelTestSubPackage = "";
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

  /**
   * Default constructor.
   * This method will map between Swagger type and language-specified type, as well as mapping
   * between Swagger type and the corresponding import statement for the language. This will
   * also add some language specified CLI options, if any.
   *
   *
   * returns string presentation of the example path (it's a constructor)
   */
  public TriagephpGenerator() {
    super();

    // Set the resonse class that should be returned from factory
    CodegenModelFactory.setTypeMapping(CodegenModelType.RESPONSE, TriagePhpCodegenResponse.class);
    CodegenModelFactory.setTypeMapping(CodegenModelType.OPERATION, TriagePhpCodegenOperation.class);
    CodegenModelFactory.setTypeMapping(CodegenModelType.PROPERTY, TriagePhpCodegenProperty.class);

    // Create a list of exceptions for constant naming
    resourceConfigConstantFieldExceptions.put("__v", "version");
    resourceConfigConstantFieldExceptions.put("_id", "id");

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

    modelTestTemplateFiles.put("modelVOTest.mustache", "VOTest.php");

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
            "self::DATA_TYPE_FLOAT",
            "null" // TODO Can this still be removed?
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
    additionalProperties.put("author", AUTHOR);
    additionalProperties.put("initialCaps", new InitialCapsLambda());
  }

  /**
   * Process options and add them to additional properties.
   *
   * Additional properties is available on all templates
   */
  @Override
  public void processOpts() {
      super.processOpts();

      if (additionalProperties.containsKey("folderPackage")) {
          folderPackage = (String) additionalProperties.get("folderPackage");
      }
      if (additionalProperties.containsKey("driverName")) {
          driverName = (String) additionalProperties.get("driverName");
      }
      moduleName = getModuleNameFromPackage(folderPackage);
      packageDir = packageToDirName(folderPackage);
      modelDirName = packageDir + "//" + driverName + "//Resource";
      apiDirName = packageDir + "//" + driverName + "//EndPoint";

      /**
       * Supporting Files.  You can write single files for the generator with the
       * entire object tree available.  If the input file has a suffix of `.mustache
       * it will be processed by the template engine.  Otherwise, it will be copied
       */
      supportingFiles.add(new SupportingFile("driver.mustache", packageDir + "//" + driverName, driverName + "Driver.php"));
      supportingFiles.add(new SupportingFile("client.mustache", packageDir + "//" + driverName, driverName + "Client.php"));
      supportingFiles.add(
          new SupportingFile("driverFactory.mustache", packageDir + "//" + driverName + "//Service//Factory", driverName + "DriverFactory.php")
      );

      resourcePackage = folderPackage;
      resourceSubPackage = resourcePackage + "\\" + driverName + "\\Resource";
      modelPackage = resourcePackage;
      modelSubPackage = resourcePackage + "\\" + driverName;
      apiCategory = moduleName;
      apiPackage = resourcePackage;
      apiSubPackage = resourcePackage + "\\" + driverName + "\\EndPoint";
      modelTestCategory = moduleName + "Test";
      modelTestPackage = toModelTestPackage(resourcePackage);
      modelTestSubPackage = modelTestPackage + "\\" + driverName + "\\Resource";

      additionalProperties.put("moduleName", moduleName);
      additionalProperties.put("folderPackage", folderPackage);
      additionalProperties.put(CodegenConstants.MODEL_PACKAGE, modelPackage);
      additionalProperties.put("modelSubPackage", modelSubPackage);
      additionalProperties.put(CodegenConstants.API_PACKAGE, apiPackage);
      additionalProperties.put("apiCategory", apiCategory);
      additionalProperties.put("apiSubPackage", apiSubPackage);
      additionalProperties.put("resourcePackage", resourcePackage);
      additionalProperties.put("resourceSubPackage", resourceSubPackage);
      additionalProperties.put("modelTestPackage", modelTestPackage);
      additionalProperties.put("modelTestSubPackage", modelTestSubPackage);
      additionalProperties.put("modelTestCategory", modelTestCategory);
      additionalProperties.put("currentDate", getCurrentDate());

  }

  /**
   * Return the capitalized file name of the model test
   *
   * @param name the model name
   * @return the file name of the model
   */
  @Override
  public String toModelTestFilename(String name) {
      return initialCaps(name);
  }

  /**
   * Get the current date in the format of 'dd MMM yyyy'
   *
   * This is used for the @since tag
   *
   * @return The date in format '01 Sep 2016'
   */
  public String getCurrentDate() {
      DateFormat df = new SimpleDateFormat("dd MMM yyyy");
      Date dateobj = new Date();
      return df.format(dateobj);
  }

  /**
   * Convert the package to a test package by adding 'Test' to the module name
   *
   * @param packagePath - The package that needs to be changed to test package
   *
   * @return The changed package
   */
  protected String toModelTestPackage(String packagePath) {
      String testPackage = packagePath.substring(0, packagePath.indexOf("\\"));
      testPackage += "Test\\" + packagePath.substring(packagePath.indexOf("\\") + 1);
      return testPackage;
  }

  /**
   * Get the module name from a given package
   *
   * Module name will be everything before the first '\'
   *
   * @param packagePath - The package that needs to be changed to test package
   *
   * @return The module name
   */
  protected String getModuleNameFromPackage(String packagePath) {
      return packagePath.substring(0, packagePath.indexOf("\\"));
  }

  /**
   * Convert the package into a folder structure
   *
   * Replace '\' with '/'
   *
   * @param packagePath - The package that needs to be changed into a dir structure
   *
   * @return The dir structure
   */
  protected String packageToDirName(String packagePath) {
      String clone = packagePath;
      return clone.replace("\\", "//");
  }

  /**
   * Return the file name of the Api Test
   *
   * @param name the file name of the Api
   * @return the file name of the Api
   */
  @Override
  public String apiFilename(String templateName, String tag) {
      String suffix = apiTemplateFiles().get(templateName);
      return apiFileFolder() + '/' + toApiFilename(tag) + suffix;
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
   * Location to write model files.  You can use the modelPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String modelTestFileFolder() {
      return (outputFolder + "//Test//" + packageDir + "//" + driverName + "//Resource");
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

  /**
   * Update property for array(list) container
   *
   * Override the parent function in order to set the 'isPrimitiveType' on the inner property as well
   *
   * @param property Codegen property
   * @param innerProperty Codegen inner property of map or list
   */
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
      tr.isNoContent = false;
      if (!responseCode.equals("default")) {
          int statusCode = Integer.parseInt(responseCode);
          if (statusCode >= 200 && statusCode < 300) {
              tr.isSuccess = true;
          }

          if (statusCode == 204) {
              tr.isNoContent = true;
          }
      }
      return tr;
  }

  /**
   * Convert Swagger Operation object to Codegen Operation object
   *
   * @param path the path of the operation
   * @param httpMethod HTTP method
   * @param operation Swagger operation object
   * @param definitions a map of Swagger models
   * @param swagger a Swagger object representing the spec
   * @return Codegen Operation object
   */
  @Override
  public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Model> definitions, Swagger swagger) {
      CodegenOperation co = super.fromOperation(path, httpMethod, operation, definitions, swagger);
      co.imports.clear();
      addResourceImport(co);

      TriagePhpCodegenOperation tr = (TriagePhpCodegenOperation) co;
      tr.isGetList = false;
      int lastSlashIndex = co.path.lastIndexOf("/");
      int lastCurlyBraceIndex = co.path.lastIndexOf("}");

      if (co.httpMethod.equals("GET") && lastCurlyBraceIndex < lastSlashIndex) {
          tr.isGetList = true;
      } else if (co.httpMethod.equals("GET")) {
          tr.isGet = true;
      }
      return tr;
  }

  /**
   * Configure import paths for model(The VO and RC) files
   *
   * @param operation - The current operation
   */
  public void addResourceImport(CodegenOperation operation) {
      for (CodegenResponse response: operation.responses) {
          if (
              response.baseType != null &&
              !defaultIncludes.contains(response.baseType) &&
              !languageSpecificPrimitives.contains(response.baseType)
          ) {
              System.out.println(operation.operationId + ":" + response.dataType);
              operation.imports.add(resourceSubPackage + "\\" + response.baseType);
          }
      }
  }

  /**
   * Return the fully-qualified "Model" name for import
   *
   * @param name the name of the "Model"
   * @return the fully-qualified "Model" name for import
   */
  @Override
  public String toModelImport(String name) {
      return name;
  }

  /**
   * Class that is used by a lambda to convert any value to initial caps.
   *
   * Change 'hello' into 'Hello'
   */
  private static class InitialCapsLambda extends TriageCustomLambda {
      @Override
      public String initialCaps(String value) {
          return StringUtils.capitalize(value);
      }
  }

  /**
   * Custom lambda that can be used to initial caps any value.
   *
   * Simply use '{{#initialCaps}}someValue{{/initialCaps}}'
   */
  private static abstract class TriageCustomLambda implements Mustache.Lambda {
      @Override
      public void execute(Template.Fragment frag, Writer out) throws IOException {
          final StringWriter tempWriter = new StringWriter();
          frag.execute(tempWriter);
          out.write(initialCaps(tempWriter.toString()));
      }

      public abstract String initialCaps(String value);
  }

    /**
     * Convert Swagger Property object to Codegen Property object
     *
     * @param name name of the property
     * @param p Swagger property object
     * @return Codegen Property object
     */
    @Override
    public CodegenProperty fromProperty(String name, Property p) {
        CodegenProperty cp = super.fromProperty(name, p);
        TriagePhpCodegenProperty tp = (TriagePhpCodegenProperty) cp;
        if (resourceConfigConstantFieldExceptions.containsKey(cp.name)) {
            cp.name = resourceConfigConstantFieldExceptions.get(cp.name);
        }
        tp.uppercaseName = cp.name.toUpperCase();
        return tp;
    }

}