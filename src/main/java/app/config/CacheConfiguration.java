package app.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String FOODS_CACHE = "foods";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(FOODS_CACHE);
    }
}
