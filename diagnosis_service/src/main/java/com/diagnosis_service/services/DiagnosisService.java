package com.diagnosis_service.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisService {
    @Value("${api_endpoint}")
    private String API_ENDPOINT;

    @Value("${api_key}")
    private String API_KEY;

    private final HistoryService historyService;

    public DiagnosisService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public String generateText(String description, String imageBase64, Long userId) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_ENDPOINT + "?key=" + API_KEY);
            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());

            if(description == null) {
                description = "This is the image of my skin what kind of disease it is?";
            }

            String requestBody = null;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                // Payload with image
                requestBody = """
                    {
                        "contents": [{
                            "parts": [
                                {"text": "%s"},
                                {
                                    "inline_data": {
                                        "mime_type": "image/jpeg",
                                        "data": "%s"
                                    }
                                }
                            ]
                        }]
                    }
                    """.formatted(description, imageBase64);
            } else {
                // Payload without image
                requestBody = """
                    {
                        "contents": [{
                            "parts": [{"text": "%s"}]
                        }]
                    }
                    """.formatted(description);
            }

            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            HttpClientResponseHandler<String> responseHandler = (ClassicHttpResponse response) -> {
                int statusCode = response.getCode();
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == 200) {
                    SuccessResponse successResponse = objectMapper.readValue(responseBody, SuccessResponse.class);

                    if (successResponse.getCandidates() != null && !successResponse.getCandidates().isEmpty()) {
                        SuccessResponse.Candidate firstCandidate = successResponse.getCandidates().get(0);

                        if (firstCandidate.getContent() != null &&
                                firstCandidate.getContent().getParts() != null &&
                                !firstCandidate.getContent().getParts().isEmpty()) {
                            return firstCandidate.getContent().getParts().get(0).getText();
                        }
                    }
                    return "No generated text found in the response.";
                } else {
                    ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                    return "Error: " + errorResponse.getMessage();
                }
            };

            String result = httpClient.execute(httpPost, responseHandler);

            historyService.storeQueryHistory(userId, requestBody, result);

            return result;

        } catch (Exception e) {
            return "Error generating text: " + e.getMessage();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuccessResponse {
        private List<Candidate> candidates;

        public List<Candidate> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<Candidate> candidates) {
            this.candidates = candidates;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Candidate {
            private Content content;

            public Content getContent() {
                return content;
            }

            public void setContent(Content content) {
                this.content = content;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Content {
            private List<Part> parts;

            public List<Part> getParts() {
                return parts;
            }

            public void setParts(List<Part> parts) {
                this.parts = parts;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Part {
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }

    // Define error response model
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorResponse {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}