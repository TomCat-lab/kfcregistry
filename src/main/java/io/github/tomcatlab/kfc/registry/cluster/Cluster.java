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
        new ServerHealth(this).check();

    }

    public void electLeader() {
        List<Server> master = servers.stream().filter(Server::isLeader).filter(Server::isStatus).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(master)) {
            log.info("no master need elect");
            new Election().elect(servers);
        } else if (master.size() > 1) {
            log.info("more than one master need elect");
            new Election().elect(servers);
        } else {
            log.info("no need elect master is {}", master.get(0));
        }
    }


    public long syncSnapshotFromLeader() {
        Server leader = leader();
        Server self = self();
        log.debug("leader version:{},my version:{}",leader.getVersion(),self.getVersion());
        if (!self.isLeader() && self.getVersion()<leader.getVersion()) {
            try {
                log.debug(" =========>>>>> syncSnapshotFromLeader {}", this.leader().getUrl() + "/snapshot");
                Snapshot snapshot = HttpInvoker.httpGet(this.leader().getUrl() + "/snapshot", Snapshot.class);
                return KfcRegistryService.restore(snapshot);
            } catch (Exception ex) {
                log.error(" =========>>>>> syncSnapshotFromLeader failed.", ex);
            }
        }
        return -1;
    }


    public Server self() {
        if (MYSELF != null) {
            MYSELF.setVersion( KfcRegistryService.VERSION.get());
            return MYSELF;
        }

        MYSELF = new Server("http://" + host + ":" + port, true, false, KfcRegistryService.VERSION.get());
        return MYSELF;

    }

    public Server leader() {
        return this.servers.stream().filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }
}
