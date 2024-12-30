package com.diagnosis_service.controllers;

import com.diagnosis_service.services.DiagnosisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/diagnosis")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> analyzeImage(@RequestPart("image") MultipartFile image,
                                               @RequestParam(value = "description", required = false) String description) throws IOException
    {
        // Convert image to Base64
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        String response = diagnosisService.generateText(description, base64Image);

//        GenerateResponse responseAI;

        if(response ==null || response.startsWith("Error")) {
//            responseAI = new GenerateResponse("FAILED", "No data available");
            return new ResponseEntity<>("FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        responseAI = new GenerateResponse("SUCCESS", response);

        return ResponseEntity.status(200).body(response);
    }

}

