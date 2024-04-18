package io.github.tomcatlab.kfc.registry;

import io.github.tomcatlab.kfc.registry.config.KfcRegistryConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({KfcRegistryConfigProperties.class})
public class KfcregistryApplication {


    public static void main(String[] args) {
        SpringApplication.run(KfcregistryApplication.class, args);
    }

}
