package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;
@Data
public class Snapshot {
    final LinkedMultiValueMap<String, InstanceMeta> REGISTRY;
    final Map<String, Long> VERSIONS;
    final Map<String, Long> TIMESTAMPS;
    final long version;

    public Snapshot(LinkedMultiValueMap<String, InstanceMeta> registry, Map<String, Long> versions, Map<String, Long> timestamps, long version) {
        REGISTRY = registry;
        VERSIONS = versions;
        TIMESTAMPS = timestamps;
        this.version = version;
    }


}
