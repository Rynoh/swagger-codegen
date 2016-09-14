package com.a24group.codegen;

import io.swagger.codegen.*;
import io.swagger.models.properties.*;
import io.swagger.util.Json;

import java.util.*;
import java.util.regex.Matcher;
import java.io.File;

public class TriagephpGenerator extends DefaultCodegen implements CodegenConfig {

  // source folder where to write the files
  public String myName = "Ryno Hartzer";
    
  protected String sourceFolder = "src";
  protected String apiVersion = "1.0.0";
  final protected String SUFFIX_VO = "VO";
  final protected String SUFFIX_RC = "ResourceConfig";
  final protected String AUTHOR = "Code Gen <code.gen@a24testmail.com>";
  protected String invokerPackage = "Swagger\\Client";
  protected String resourceName = "";
  protected String modelDirName = "Model//Resource";
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
    modelTemplateFiles.put(
      "modelResourceConfig.mustache", // the template to use
      "ResourceConfig.php"// the extension for each file to write
    );
    modelTemplateFiles.put(
        "modelVO.mustache", // the template to use
        "VO.php"// the extension for each file to write
    );
    /**
     * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
     * as with models, add multiple entries with different extensions for multiple files per
     * class
     */
//    apiTemplateFiles.put(
//      "api.mustache",   // the template to use
//      ".sample");       // the extension for each file to write

    /**
     * Template Location.  This is the location which templates will be read from.  The generator
     * will use the resource stream to attempt to read the templates.
     */
    templateDir = "TriagePhp";

    /**
     * Api Package.  Optional, if needed, this can be used in templates
     */
    apiPackage = "io.swagger.client.api";

    /**
     * Model Package.  Optional, if needed, this can be used in templates
     */
    modelPackage = "a24group\\Resource";

    // ref: http://php.net/manual/en/language.types.intro.php
//    languageSpecificPrimitives = new HashSet<String>(
//        Arrays.asList(
//            "bool",
//            "boolean",
//            "int",
//            "integer",
//            "double",
//            "float",
//            "string",
//            "object",
//            "DateTime",
//            "mixed",
//            "number",
//            "void",
//            "byte"
//        )
//    );

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
     * Additional Properties.  These values can be passed to the templates and
     * are available in models, apis, and supporting files
     */
    additionalProperties.put("apiVersion", apiVersion);
//    additionalProperties.put(CodegenConstants.MODEL_NAME_SUFFIX, SUFFIX_RC);
//    modelPackage = "TriStudio\\Stuff\\Resource";/
    additionalProperties.put(CodegenConstants.MODEL_PACKAGE, modelPackage);
    additionalProperties.put("author", AUTHOR);
    
    /**
     * TODO What does this do?
     * Supporting Files.  You can write single files for the generator with the
     * entire object tree available.  If the input file has a suffix of `.mustache
     * it will be processed by the template engine.  Otherwise, it will be copied
     */
    supportingFiles.add(new SupportingFile("myFile.mustache",   // the input template or file
      "",                                                       // the destination folder, relative `outputFolder`
      "myFile.sample")                                          // the output file
    );

    /**
     * Language Specific Primitives.  These types will not trigger imports by
     * the client generator
//     */
//    languageSpecificPrimitives = new HashSet<String>(
//      Arrays.asList(
//        "Type1",      // replace these with your types
//        "Type2")
//    );
  }

  /**
   * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
   * those terms here.  This logic is only called if a variable matches the reseved words
   * 
   * @return the escaped term
   */
  @Override
  public String escapeReservedWord(String name) {
    return "_" + name;  // add an underscore to the name
  }

  /**
   * Location to write model files.  You can use the modelPackage() as defined when the class is
   * instantiated
   */
  public String modelFileFolder() {
      return (outputFolder + "/" + modelDirName);
//    return outputFolder + "/" + sourceFolder + "/" + modelPackage().replace('.', File.separatorChar);
  }
  
  /**
   * Location to write api files.  You can use the apiPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String apiFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + apiPackage().replace('.', File.separatorChar);
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
        System.out.println("This is instance of ArrayProperty!!!!");
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      
      
      String innerMappingType = null;
      // TODO This is the embedded type getTypeDeclaration(inner)

      if (false) {
          // TODO Fix this for embedded objects!!!
      }
//      System.out.println("Putting inner type as : " + getTypeDeclaration(inner));
//      additionalProperties.put("innerMappingType", getTypeDeclaration(inner));
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
      // TODO Removed the toModel of toModel(type)
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if (typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      System.out.println("This be type: " + type);
      if (languageSpecificPrimitives.contains(type))
        return type;
    } else {
        type = initialCaps(swaggerType);
    }
    return type;
  }
  
  protected String varNameToUppercase(String varName) {
      return varName.toUpperCase();
  }
  
  /**
   * @todo broken and bad name
   * @param varName
   * @return
   */
  protected String constantiseVarName(String varName) {
      if (resourceConfigConstantFieldExceptions.containsKey(varName)) {
          varName = resourceConfigConstantFieldExceptions.get(resourceConfigConstantFieldExceptions);
      }
      
      return varNameToUppercase(varName);
  }
  
  /**
   * Output the proper model name (capitalized)
   *
   * @param name the name of the model
   * @return capitalized model name
   */
//  @Override
//  public String toModelName(final String name) {
////      return initialCaps(modelNamePrefix + stripModelFromName(name));
//      return initialCaps(stripModelFromName(name));
//  }
  
  /**
   * Return the capitalized file name of the model
   *
   * @param name the model name
   * @return the file name of the model
   */
//  @Override
//  public String toModelFilename(String name) {
////      return initialCaps(modelNamePrefix + stripModelFromName(name) + modelNameSuffix);
////      return initialCaps(stripModelFromName(name));
////      return initialCaps(stripModelFromName(name));
//  }
  
//  protected String stripModelFromName(String name) {
//      return name.replaceFirst("Model", "");
//  }
  
  @Override
  public void processOpts() {
      super.processOpts();
  }
  
  /**
   * Convert Swagger Property object to Codegen Property object
   * TODO THIS IS A HACK!
   * @param name name of the property
   * @param p Swagger property object
   * @return Codegen Property object
   */
  @Override
  public CodegenProperty fromProperty(String name, Property p) {
      CodegenProperty cp = super.fromProperty(name, p);
      
      if (resourceConfigConstantFieldExceptions.containsKey(cp.name)) {
          cp.name = resourceConfigConstantFieldExceptions.get(cp.name);
      }
      
      cp.name = cp.name.toUpperCase();
      return cp;
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
                        LOGGER.warn("!!!!!@#FOUND THE VALUE OF BASE TYPE :" + innerProperty.name + " type:" + innerProperty.baseType);
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
  
}