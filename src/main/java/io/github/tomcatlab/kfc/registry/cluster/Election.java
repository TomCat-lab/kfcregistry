package io.github.tomcatlab.kfc.registry.cluster;

import io.github.tomcatlab.kfc.registry.model.Server;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor
public class Election {
  public  void elect(List<Server> servers) {
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
            log.info("elect failde,no master need elect");
        }
    }
}
