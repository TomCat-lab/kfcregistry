package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.http.HttpInvoker;
import io.github.tomcatlab.kfc.registry.model.Server;
import io.github.tomcatlab.kfc.registry.service.KfcRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ServerHealth {
    final Cluster cluster;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long interval = 5_000;

    public void check() {
        executor.scheduleWithFixedDelay(() -> {
            checkHealth();
            doElect();
            syncSnapshotFromLeader();
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    private void doElect() {
        new Election().electLeader(cluster.getServers());
    }


    public long syncSnapshotFromLeader() {
        Server leader = cluster.leader();
        Server self = cluster.self();
        log.debug("leader version:{},my version:{}",leader.getVersion(),self.getVersion());
        if (!self.isLeader() && self.getVersion()<leader.getVersion()) {
            try {
                log.debug(" =========>>>>> syncSnapshotFromLeader {}", leader.getUrl() + "/snapshot");
                Snapshot snapshot = HttpInvoker.httpGet(leader.getUrl() + "/snapshot", Snapshot.class);
                return KfcRegistryService.restore(snapshot);
            } catch (Exception ex) {
                log.error(" =========>>>>> syncSnapshotFromLeader failed.", ex);
            }
        }
        return -1;
    }

    private void checkHealth() {
       cluster.getServers().stream().filter(s->!cluster.MYSELF.equals(s)).parallel().forEach(this::serverInfo);
    }

    private void serverInfo(Server server) {
            try {
                Server getServer = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                if (getServer != null) {
                    server.setStatus(true);
                    server.setLeader(getServer.isLeader());
                    server.setVersion(getServer.getVersion());
                    log.info("checkHealth success:{}", getServer);
                } else {
                    server.setStatus(false);
                    log.error("checkHealth error:{}", server);
                }
            } catch (Exception e) {
                server.setStatus(false);
                log.error("checkHealth error", e);
            }

    }

}
