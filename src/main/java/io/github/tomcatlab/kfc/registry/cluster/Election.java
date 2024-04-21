package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.model.Server;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class Election {
  public void elect(List<Server> servers) {
        Server candidate = null;
        for (Server server : servers) {
            if (!server.isStatus()) continue;
            server.setLeader(false);
            if (candidate == null) {
                candidate = server;
            } else {
                if (server.hashCode() > candidate.hashCode()) {
                    candidate = server;
                }
            }
        }

        if (candidate != null) {
            candidate.setLeader(true);
            log.info("elect master is {}", candidate);
        } else {
            log.info("elect failed,no master,servers:{}",servers);
        }
    }

    public void electLeader(List<Server> servers) {
        List<Server> master = servers.stream().filter(Server::isLeader).filter(Server::isStatus).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(master)) {
            log.warn("no master need elect");
             elect(servers);
        } else if (master.size() > 1) {
            log.warn("more than one master need elect");
            elect(servers);
        } else {
            log.debug("no need elect master is {}", master.get(0));
        }
    }
}
