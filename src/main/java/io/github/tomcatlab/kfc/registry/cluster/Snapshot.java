package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot {
     LinkedMultiValueMap<String, InstanceMeta> registry;
     Map<String, Long> versions;
     Map<String, Long> timestamps;
     long version;




}
