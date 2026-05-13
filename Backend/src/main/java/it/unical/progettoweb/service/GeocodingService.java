package it.unical.progettoweb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public double[] geocodifica(String street, String civicNumber,
                                String city, String cap, String province) {
        try {
            String address = street + " " + civicNumber + ", " + cap + " " + city + " " + province + ", Italy";

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("maps.googleapis.com")
                    .path("/maps/api/geocode/json")
                    .queryParam("address", address)
                    .queryParam("key", apiKey)
                    .build()
                    .encode()
                    .toUri();

            String json = restTemplate.getForObject(uri, String.class);
            JsonNode root = objectMapper.readTree(json);

            if ("OK".equals(root.path("status").asText())) {
                JsonNode location = root
                        .path("results").get(0)
                        .path("geometry")
                        .path("location");
                return new double[]{
                        location.path("lat").asDouble(),
                        location.path("lng").asDouble()
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0.0, 0.0};
    }
}