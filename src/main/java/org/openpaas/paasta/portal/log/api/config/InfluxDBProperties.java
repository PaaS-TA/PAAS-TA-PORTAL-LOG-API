package org.openpaas.paasta.portal.log.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "influxdb")
public class InfluxDBProperties {

    private String ip;
    private String url;
    private String username;
    private String password;
    private String database;
    private String measurement;
    private String limit;
    private boolean httpsEnabled;


    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }

    public String getMeasurement() {
        return measurement;
    }
    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getLimit() {
        return limit;
    }
    public void setLimit(String limit) {
        this.limit = limit;
    }

    public boolean getHttpsEnabled() {
        return httpsEnabled;
    }
    public void setHttpsEnabled(boolean httpsEnabled) {
        this.httpsEnabled = httpsEnabled;
    }
}
