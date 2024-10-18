/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.0.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.example.generated.api;

import com.example.generated.model.UsEnglishBook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-10-18T14:08:26.386934239+08:00[Asia/Taipei]")
@Validated
@Tag(name = "Us", description = "the Us API")
public interface UsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /us/books : Get list of English books
     *
     * @return A list of English books (status code 200)
     */
    @Operation(
        operationId = "usBooksGet",
        summary = "Get list of English books",
        tags = { "us" },
        responses = {
            @ApiResponse(responseCode = "200", description = "A list of English books", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UsEnglishBook.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/us/books",
        produces = { "application/json" }
    )
    default ResponseEntity<List<UsEnglishBook>> usBooksGet(
        
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"author\" : \"author\", \"language\" : \"English\", \"id\" : 0, \"title\" : \"title\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
