package io.github.tomcatlab.kfc.registry.service;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Slf4j
public class KfcRegistryService implements RegistryService {
    MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        instance.setStatus(true);
        if (!CollectionUtils.isEmpty(metas) && metas.contains(instance)) {
            log.info("this instance has register:{}", instance);
            return instance;
        }
        REGISTRY.add(service, instance);
        log.info("register this instance  :{}", instance.toUrl());
        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if (CollectionUtils.isEmpty(metas)) return null;
        metas.removeIf(m -> m.equals(instance));
        log.info("unregister this instance  :{}", instance.toUrl());
        instance.setStatus(false);
        return instance;
    }

    @Override
    public List<InstanceMeta> fetchAllInstance(String service) {
        return REGISTRY.get(service);
    }
}
