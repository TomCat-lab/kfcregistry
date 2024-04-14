package io.github.tomcatlab.kfc.registry.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
/**
 * Class: InstanceMeta
 * Author: cola
 * Date: 2024/4/4
 * Description: 描述服务实例元数据
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"scheme", "host", "port", "context"})
public class InstanceMeta {
    private String scheme; // http or https
    private String host;
    private int port;
    private String context;

    private boolean status; // this insatance inline or offline

    private Map<String,String> parameters;

    public InstanceMeta(String sceme, String host, int port, String context) {
        this.scheme = sceme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMeta toHttp(String host,Integer port) {
        return new InstanceMeta("http", host, port, "kfcrpc");
    }



    public String toUrl() {
        return String.format("%s://%s:%d/", scheme,host,port);
    }

    public String toMetas(){
        return JSON.toJSONString(parameters);
    }


    public String toPath() {
        return String.format("%s_%d",host,port);
    }
}
