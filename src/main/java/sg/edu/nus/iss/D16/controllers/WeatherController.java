package sg.edu.nus.iss.D16.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.D16.models.Weather;
import sg.edu.nus.iss.D16.services.WeatherService;

@Controller
@RequestMapping(path = "/weather")
public class WeatherController {
    
    @Autowired
    private WeatherService wSvc;

    @GetMapping
    public String getCity(@RequestParam("city") String city, Model model) {
        List<Weather> weatherList = wSvc.getWeather(city);
        model.addAttribute("city", city.toUpperCase());
        model.addAttribute("weatherList", weatherList);
        return "weather";
    }
}
