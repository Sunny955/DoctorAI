package com.diagnosis_service.controllers;

import com.diagnosis_service.dto.Response.GenerateResponse;
import com.diagnosis_service.services.DiagnosisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "/analyze")
    public ResponseEntity<Object> analyzeImage(@RequestPart(value = "image", required = false) MultipartFile image,
                                               @RequestParam(value = "description", required = false) String description,
                                               HttpServletRequest request) throws IOException
    {
        Long userId = (Long) request.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not found");
        }

        if (image == null && (description == null || description.isEmpty())) {
            return ResponseEntity.badRequest().body("At least one of image or description must be provided");
        }

        String base64Image = null;

        if(image != null) {
            base64Image = Base64.getEncoder().encodeToString(image.getBytes());
        }

        String response = diagnosisService.generateText(description, base64Image, userId);

        GenerateResponse responseAI = new GenerateResponse();

        if(response ==null || response.startsWith("Error")) {
            responseAI.setData("No server response");
            responseAI.setStatus("FAILED");
            return new ResponseEntity<>(responseAI, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseAI.setData(response);
        responseAI.setStatus("SUCCESS");

        return new ResponseEntity<>(responseAI, HttpStatus.OK);
    }

}

