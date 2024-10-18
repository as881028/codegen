package com.example.demo;

public class CustomSpringCodegen extends org.openapitools.codegen.languages.SpringCodegen {
  @Override
  public void processOpts() {
    super.processOpts();
    additionalProperties.put("serviceVarName", "myService");
  }
}
