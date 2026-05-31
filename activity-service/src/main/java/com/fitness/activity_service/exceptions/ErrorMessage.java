package com.fitness.activity_service.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {
    private int statusCode;
    @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private LocalDateTime timestamp;
    private String message;
    private String errorType;
    private String path;
    private List<String> details;
}
