package com.Sudip.Money_Manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailDownloadService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmailWithAttachment(
            String to,
            String subject,
            String htmlContent,
            byte[] attachment,
            String fileName) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("api-key", apiKey);

        String base64File = Base64.getEncoder().encodeToString(attachment);

        Map<String, Object> request = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of("email", to)
                ),
                "subject", subject,
                "htmlContent", htmlContent,
                "attachment", List.of(
                        Map.of(
                                "name", fileName,
                                "content", base64File
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(request, headers);


        ResponseEntity<String> response;

        try {
            response = restTemplate.postForEntity(url, entity, String.class);

            System.out.println("Success: " + response.getBody());

        } catch (org.springframework.web.client.HttpClientErrorException e) {

            System.out.println("Status = " + e.getStatusCode());
            System.out.println("Response = " + e.getResponseBodyAsString());

            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Failed to send email: " + response.getBody()
            );
        }
    }
}