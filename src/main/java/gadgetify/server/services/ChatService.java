package gadgetify.server.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    public Map<String, Object> sendMessageToGemini(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();
        String GEMINI_API_KEY = "AIzaSyBSJLhLY20KpVW-UfJmIMSWprhm2R1-YRo";
        String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=" + GEMINI_API_KEY;
        return restTemplate.postForObject(API_URL, entity, Map.class);
    }

}
