package io.github.tomcatlab.kfc.registry.service;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;

import java.util.List;
import java.util.Map;

/**
 * Class: RegistryService
 * Author: cola
 * Date: 2024/4/14
 * Description:
 */

public interface RegistryService {
    InstanceMeta register(String service, InstanceMeta instance);
    InstanceMeta unregister(String service,InstanceMeta instance);
    List<InstanceMeta> fetchAllInstance(String service);

    long renew(InstanceMeta instance, String... service);
    Long version(String service);

    Map<String, Long> versions(String[] services);
}
