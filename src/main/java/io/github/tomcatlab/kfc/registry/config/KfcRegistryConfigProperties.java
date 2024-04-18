package io.github.tomcatlab.kfc.registry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "kfcregistry")
public class KfcRegistryConfigProperties {
    private List<String> serverlist;
}
