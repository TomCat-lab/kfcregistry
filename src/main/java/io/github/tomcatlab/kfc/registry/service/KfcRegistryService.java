package io.github.tomcatlab.kfc.registry.service;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class KfcRegistryService implements RegistryService {
   final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();
   final static Map<String,Long> VERSIONS = new ConcurrentHashMap<>();
   public final static Map<String,Long> TIMESTAMPS = new ConcurrentHashMap<>();
   final static AtomicLong VERSION = new AtomicLong(0);

    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        instance.setStatus(true);
        if (!CollectionUtils.isEmpty(metas) && metas.contains(instance)) {
            log.info("this instance has register:{}", instance);
            return instance;
        }
        REGISTRY.add(service, instance);
        renew(instance, service);
        VERSIONS.put(service,VERSION.incrementAndGet());
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
        VERSIONS.put(service,VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public List<InstanceMeta> fetchAllInstance(String service) {
        return REGISTRY.get(service);
    }

    @Override
    public long  renew(InstanceMeta instance, String ... services){
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTAMPS.put(service+"@"+instance.toUrl(),now);
        }
        return now;
    }

    @Override
    public Long version(String service) {
        return VERSIONS.get(service);
    }

    @Override
    public Map<String,Long> versions(String ...services){
        return Arrays.stream(services)
                .collect(Collectors.toMap(s->s,VERSIONS::get,(s,s1)->s1));
    }
}
