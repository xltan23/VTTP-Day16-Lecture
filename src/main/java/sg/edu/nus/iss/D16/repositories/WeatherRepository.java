package sg.edu.nus.iss.D16.repositories;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherRepository {
    
    @Value("${weather.cache.duration}")
    private Long cacheTime;

    @Autowired
    @Qualifier("redislab")
    private RedisTemplate<String,String> redisTemplate;

    public void save(String city, String payload) {
        ValueOperations<String,String> valueOp = redisTemplate.opsForValue();
        // Key: City, Value: Payload
        valueOp.set(city.toLowerCase(), payload, Duration.ofMinutes(cacheTime));
    }

    public Optional<String> get(String city) {
        ValueOperations<String,String> valueOp = redisTemplate.opsForValue();
        // Obtain payload
        String value = valueOp.get(city.toLowerCase());
        if (null == value) {
            // Empty box
            return Optional.empty();
        }
        // Box with payload
        return Optional.of(value);
    }
}
