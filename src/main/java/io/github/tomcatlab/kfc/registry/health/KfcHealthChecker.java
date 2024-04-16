package io.github.tomcatlab.kfc.registry.health;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;
import io.github.tomcatlab.kfc.registry.service.KfcRegistryService;
import io.github.tomcatlab.kfc.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KfcHealthChecker implements HealthChecker{
    public KfcHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    RegistryService registryService;
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 20_000;

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(
                ()->{
                    log.info(" ===> Health checker running...");
                    long now = System.currentTimeMillis();
                    KfcRegistryService.TIMESTAMPS.keySet().stream().forEach(serviceAndInst -> {
                        long timestamp = KfcRegistryService.TIMESTAMPS.get(serviceAndInst);
                        if (now - timestamp > timeout) {
                            log.info(" ===> Health checker: {} is down", serviceAndInst);
                            int index = serviceAndInst.indexOf("@");
                            String service = serviceAndInst.substring(0, index);
                            String url = serviceAndInst.substring(index + 1);
                            InstanceMeta instance = InstanceMeta.from(url);
                            registryService.unregister(service, instance);
                            KfcRegistryService.TIMESTAMPS.remove(serviceAndInst);
                        }
                    });

                },10,10, TimeUnit.SECONDS
        );
    }

    @Override
    public void stop() {

    }
}
