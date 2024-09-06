package com.msp.everestFitness.everestFitness.dto;

import lombok.Data;

@Data
public class ApiResponseDto {
    private String message;
    private boolean success;
    private int status;

    public  ApiResponseDto(String message, boolean success, int status){
        this.message=message;
        this.success=success;
        this.status=status;
    }
}
