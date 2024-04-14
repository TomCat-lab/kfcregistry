package io.github.tomcatlab.kfc.registry.config;

import io.github.tomcatlab.kfc.registry.service.KfcRegistryService;
import io.github.tomcatlab.kfc.registry.service.RegistryService;
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
}
