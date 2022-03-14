package io.pismo.interview.creditcard.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Error")
public class ExceptionData {
    String message;
}
