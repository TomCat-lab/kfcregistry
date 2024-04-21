package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.config.KfcRegistryConfigProperties;
import io.github.tomcatlab.kfc.registry.http.HttpInvoker;
import io.github.tomcatlab.kfc.registry.model.Server;
import io.github.tomcatlab.kfc.registry.service.KfcRegistryService;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class: Cluster
 * Author: cola
 * Date: 2024/4/16
 * Description: cluster
 */
@Data
@Slf4j
public class Cluster {
    @Value("${server.port}")
   private String port;
   private static String host;
    private KfcRegistryConfigProperties registryConfigProperties;

    Server MYSELF;

    @Getter
    private List<Server> servers;

    public Cluster(KfcRegistryConfigProperties registryConfigProperties) {
        this.registryConfigProperties = registryConfigProperties;

    }

    static {
        try {
            host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackAddress().getHostAddress();
        }catch (Exception e){
            host = "127.0.0.1";
        }

    }
    public void init() {
        self();
        initServers();
        new ServerHealth(this).check();

    }

    private void initServers() {
        servers = new ArrayList<>();
        for (String url : registryConfigProperties.getServerlist()) {
            if (url.contains("localhost")) {
                url = url.replace("localhost", host);
            } else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", host);
            }
            if (url.equals(MYSELF.getUrl())) {
                servers.add(MYSELF);
            } else {
                Server server = new Server();
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1L);
                servers.add(server);
            }

        }
    }


    public Server self() {
        if (MYSELF != null) {
            MYSELF.setVersion( KfcRegistryService.VERSION.get());
            return MYSELF;
        }
        log.info("host:{}", host);
        log.info("port:{}", port);
        MYSELF = new Server("http://" + host + ":" + port, true, false, KfcRegistryService.VERSION.get());
        return MYSELF;

    }

    public Server leader() {
        return this.servers.stream().filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }
}
