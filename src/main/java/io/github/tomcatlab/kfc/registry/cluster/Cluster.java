package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.config.KfcRegistryConfigProperties;
import io.github.tomcatlab.kfc.registry.http.HttpInvoker;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class: Cluster
 * Author: cola
 * Date: 2024/4/16
 * Description: cluster
 */
@Data
public class Cluster {
    @Value("${server.port}")
    String port;
    String host ;
    private KfcRegistryConfigProperties registryConfigProperties;
    private List<Server> servers = new ArrayList<>();
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 50_000;
    Server MYSELF;


    public Cluster(KfcRegistryConfigProperties registryConfigProperties) {
        this.registryConfigProperties = registryConfigProperties;
        self();
    }

    public void  init(){
        servers = new ArrayList<>();
        for (String url : registryConfigProperties.getServerlist()) {
            if (url.contains("localhost")){
                url = url.replace("localhost",host);
            }else if (url.contains("127.0.0.1")){
                url = url.replace("127.0.0.1",host);
            }
            if (url.equals(MYSELF.getUrl())){
                servers.add(MYSELF);
            }else {
                Server server = new Server();
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1L);
                servers.add(server);
            }

        }

        executor.scheduleWithFixedDelay(()->{
            checkHealth();

        },10,10, TimeUnit.SECONDS);
    }

    private void checkHealth(){
        servers.forEach(
                server -> {
                    try {
                        Server getServer = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                        if (getServer != null) {
                            server.setStatus(true);
                            server.setLeader(getServer.isLeader());
                            server.setVersion(getServer.getVersion());
                        }else{
                            server.setStatus(false);
                        }
                    }catch (Exception e){
                        server.setStatus(false);
                    }

                }
        );
    }

    public Server self(){
        if (MYSELF == null) {
            host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackAddress().getHostAddress();
            MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
            return MYSELF;
        }
        return MYSELF;
    }
}
