package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.http.HttpInvoker;
import io.github.tomcatlab.kfc.registry.model.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerHealth {
    final Cluster cluster;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 5_000;

    public void check() {
        executor.scheduleWithFixedDelay(() -> {
            checkHealth();
            cluster.electLeader();
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void checkHealth() {
       cluster.getServers().stream().filter(s->!cluster.MYSELF.equals(s)).forEach(this::serverInfo);
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
