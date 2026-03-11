package com.micro.internproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.internproject.model.Country;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private List<Country> cachedCountries = new ArrayList<>();

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // Runs every 10 minutes
    @Scheduled(fixedRate = 600000)
    public void fetchCountriesFromApi() {

        String apiUrl = "https://restcountries.com/v3.1/all?fields=name,capital,region,population,flags";
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            String jsonResponse = response.getBody();

            if (jsonResponse == null) {
                System.out.println("API returned empty response");
                return;
            }

            JsonNode rootNode = mapper.readTree(jsonResponse);

            List<Country> newCountries = new ArrayList<>();

            if (rootNode.isArray()) {

                for (JsonNode node : rootNode) {

                    Country country = new Country();

                    country.setName(
                            node.path("name").path("common").asText("Unknown")
                    );

                    // FIX: capital sometimes missing
                    String capital = "N/A";
                    JsonNode capitalNode = node.path("capital");

                    if (capitalNode.isArray() && capitalNode.size() > 0) {
                        capital = capitalNode.get(0).asText();
                    }

                    country.setCapital(capital);

                    country.setRegion(
                            node.path("region").asText("Unknown")
                    );

                    country.setPopulation(
                            node.path("population").asLong(0)
                    );

                    country.setFlag(
                            node.path("flags").path("svg").asText("")
                    );

                    newCountries.add(country);
                }
            }

            cachedCountries = newCountries;

            System.out.println("✅ Fetched " + cachedCountries.size() + " countries.");

        } catch (Exception e) {
            System.out.println("❌ Error fetching API: " + e.getMessage());
        }
    }

    public List<Country> getAllCountries() {

        if (cachedCountries.isEmpty()) {
            fetchCountriesFromApi();
        }

        return cachedCountries;
    }

    public List<Country> searchCountries(String query) {

        if (query == null || query.isEmpty()) {
            return getAllCountries();
        }

        return getAllCountries().stream()
                .filter(c -> c.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}