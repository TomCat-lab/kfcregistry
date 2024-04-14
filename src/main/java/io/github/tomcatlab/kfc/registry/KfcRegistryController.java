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
}
