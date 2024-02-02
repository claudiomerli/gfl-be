package it.xtreamdev.gflbe.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(cron = "0 0 0 * * MON")
    private void evictAllCache() {
        this.cacheManager.getCacheNames().forEach(s -> {
            this.cacheManager.getCache(s).clear();
        });
    }

}
