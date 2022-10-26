package org.openpaas.paasta.portal.log.api.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBConfiguration {

    private final InfluxDBProperties properties;

    public InfluxDBConfiguration(InfluxDBProperties properties) {
        this.properties = properties;
    }

    @Bean
    public InfluxDB influxDB() {
        InfluxDB influxDB = InfluxDBFactory.connect(
                properties.getUrl(),
                properties.getUsername(),
                properties.getPassword()
        );

        influxDB.setDatabase(properties.getDatabase());

        return influxDB;
    }
}
