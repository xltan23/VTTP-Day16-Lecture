package sg.edu.nus.iss.D16.services;

import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.D16.models.Weather;
import sg.edu.nus.iss.D16.repositories.WeatherRepository;


@Service
public class WeatherService {
    
    private static final String URL = "https://api.openweathermap.org/data/2.5/weather";

    @Value("${API_KEY}")
    private String key;

    @Autowired
    private WeatherRepository weatherRepo;

    public List<Weather> getWeather(String city) {
        Optional<String> opt = weatherRepo.get(city);
        String payload;
        System.out.printf(">>> City: %s\n", city);

        // If repository is empty, attempt to retrieve data from OpenWeatherMap
        if (opt.isEmpty()) {
            System.out.println("Getting weather from OpenWeatherMap");

            try {
                // Building URL: https://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}
                String url = UriComponentsBuilder.fromUriString(URL)
                    .queryParam("q", URLEncoder.encode(city, "UTF-8"))
                    .queryParam("appid", key)
                    .toUriString();

                // Create GET request
                RequestEntity<Void> req = RequestEntity.get(url).build();

                // Make the call to OpenWeatherMap
                RestTemplate template = new RestTemplate();
                ResponseEntity<String> resp;

                // Throws an exception if status code in between 200-399
                resp = template.exchange(req, String.class);
                // Get payload and perform an action with it
                payload = resp.getBody();
                System.out.println("Payload: " + payload);

                // Save it in repository (Redis receives this information)
                weatherRepo.save(city, payload);
                
            } catch (Exception ex) {
                System.err.printf("Error: %s\n", ex.getMessage());
                return Collections.emptyList();
            }
        } else {
            payload = opt.get();
            System.out.printf(">>> Cache: %s\n", payload);
        }

        // Convert payload to Json object
        // Convert the String to reader
        Reader strReader = new StringReader(payload);
        // Create a JsonReader from reader
        JsonReader jsonReader = Json.createReader(strReader);
        // Read the payload as Json object
        JsonObject weatherResult = jsonReader.readObject();
        // Get only the weather list (different weather for different cities) from all Json weather results
        JsonArray cities = weatherResult.getJsonArray("weather");
        List<Weather> list = new LinkedList<>();
        for (int i = 0; i < cities.size(); i++) {
            // For each city, obtain the JsonObject 
            JsonObject jo = cities.getJsonObject(i);
            // Convert Json object to a Weather object and add to the list
            list.add(Weather.create(jo));
        }
        return list;
    }
}
