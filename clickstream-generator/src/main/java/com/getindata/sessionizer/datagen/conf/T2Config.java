package com.getindata.sessionizer.datagen.conf;

import com.typesafe.config.Config;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.URI;

@Getter
public class T2Config {

    private URI baseUrl;

    @SneakyThrows
    public T2Config(Config conf) {
        var t2Config = conf.getConfig("t2");
        baseUrl = new URI(t2Config.getString("baseUrl"));
    }
}
