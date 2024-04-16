package io.github.tomcatlab.kfc.registry.config;

import io.github.tomcatlab.kfc.registry.health.HealthChecker;
import io.github.tomcatlab.kfc.registry.health.KfcHealthChecker;
import io.github.tomcatlab.kfc.registry.service.KfcRegistryService;
import io.github.tomcatlab.kfc.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class: KfcRegistryConfig
 * Author: cola
 * Date: 2024/4/14
 * Description: registry config
 */

@Configuration
public class KfcRegistryConfig {
    @Bean
    public RegistryService kfcRegistryService(){
        return new KfcRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(@Autowired RegistryService registryService) {
        return new KfcHealthChecker(registryService);
    }
}
