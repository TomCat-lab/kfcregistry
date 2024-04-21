package io.github.tomcatlab.kfc.registry.service;

import io.github.tomcatlab.kfc.registry.cluster.Snapshot;
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
   public final static AtomicLong VERSION = new AtomicLong(0);

    public static long restore(Snapshot snapshot) {
        REGISTRY.clear();
        REGISTRY.putAll(snapshot.getRegistry());
        VERSIONS.clear();
        VERSIONS.putAll(snapshot.getVersions());
        TIMESTAMPS.clear();
        TIMESTAMPS.putAll(snapshot.getTimestamps());
        VERSION.set(snapshot.getVersion());
        return snapshot.getVersion();
    }

    @Override
    public synchronized InstanceMeta register(String service, InstanceMeta instance) {
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
    public synchronized InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if (CollectionUtils.isEmpty(metas)) return null;
        metas.removeIf(m -> m.equals(instance));
        log.info("unregister this instance  :{}", instance.toUrl());
        instance.setStatus(false);
        VERSIONS.put(service,VERSION.incrementAndGet());
        return instance;
    }

    public static synchronized Snapshot snapshot() {
        LinkedMultiValueMap<String, InstanceMeta> registry = new LinkedMultiValueMap<>();
        registry.addAll(REGISTRY);
        Map<String, Long> versions = new ConcurrentHashMap<>(VERSIONS);
        Map<String, Long> timestamps = new ConcurrentHashMap<>(TIMESTAMPS);
        return new Snapshot(registry, versions, timestamps, VERSION.get());
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
