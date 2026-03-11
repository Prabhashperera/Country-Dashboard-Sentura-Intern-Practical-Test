package com.micro.internproject.controller;

import com.micro.internproject.model.Country;
import com.micro.internproject.service.CountryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin(origins = "*") // Allows your React frontend to connect
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<Country> getCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/search")
    public List<Country> searchCountries(@RequestParam String query) {
        return countryService.searchCountries(query);
    }
}
