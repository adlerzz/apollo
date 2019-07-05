package org.adlerzz.apollo.app.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("params")
public class ParamsConfig {

    private static final Logger log = LoggerFactory.getLogger(ParamsConfig.class);

    public ParamsConfig() {
        log.debug("Params created");
    }

    private Map<String, Object> map;

    public Map<String, Object> getMap() {
        return this.map;
    }

    public void setMap(Map<String, Object> params) {
        log.debug("params set map: {}", params);
        this.map = params;
    }
}
