package io.github.tomcatlab.kfc.registry;

import io.github.tomcatlab.kfc.registry.model.InstanceMeta;
import io.github.tomcatlab.kfc.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class KfcRegistryController {
    @Autowired
    RegistryService registryService;

    @RequestMapping("/reg")
    public void register(@RequestParam("service") String service, @RequestBody InstanceMeta instanceMeta){
        log.info("register service:{}",service,"instance:{}",instanceMeta);
        registryService.register(service,instanceMeta);
    }

    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam("service") String service, @RequestBody InstanceMeta instanceMeta){
        log.info("unregister service:{}",service,"instance:{}",instanceMeta);
       return registryService.unregister(service,instanceMeta);
    }

    @RequestMapping("/fetchAll")
    public List<InstanceMeta> fetchAll(@RequestParam("service") String service){
        log.info("fetchAll service:{}",service);
        return registryService.fetchAllInstance(service);
    }

    @RequestMapping("/renew")
    public long renew(@RequestParam String service, @RequestBody InstanceMeta instance)
    {
        log.info(" ===> renew {} @ {}", service, instance);
        return registryService.renew(instance, service);
    }

    @RequestMapping("/renews")
    public long renews(@RequestParam String services, @RequestBody InstanceMeta instance)
    {
        log.info(" ===> renew {} @ {}", services, instance);
        return registryService.renew(instance, services.split(","));
    }

    @RequestMapping("/version")
    public long version(@RequestParam String service)
    {
        log.info(" ===> version {}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam String services)
    {
        log.info(" ===> versions {}", services);
        return registryService.versions(services.split(","));
    }
}
