package io.github.tomcatlab.kfc.registry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class: Server
 * Author: cola
 * Date: 2024/4/16
 * Description: server instance
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Server {
    private String url;
    private boolean status;
    private boolean leader;
    private long version;
}
