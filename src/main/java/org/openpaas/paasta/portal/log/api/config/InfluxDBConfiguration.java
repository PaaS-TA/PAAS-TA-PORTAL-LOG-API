package org.openpaas.paasta.portal.log.api.config;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

@Configuration
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBConfiguration.class);

    private final InfluxDBProperties properties;

    public InfluxDBConfiguration(InfluxDBProperties properties) {
        this.properties = properties;
    }

    @Bean
    public InfluxDB influxDB() throws Exception {
        InfluxDB influxDB = null;
        String url = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();

        if(properties.getHttpsEnabled()) {
            String sslKeyPath = properties.getSslKeyPath();
            String sslPassword = properties.getSslPassword();

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(sslKeyPath + "/keystore.p12"), sslPassword.toCharArray());
            keyStore.load(new FileInputStream(sslKeyPath + "/keystore.p12"), sslPassword.toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(sslKeyPath + "/truststore.jks"), sslPassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("SSL");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, sslPassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, new SecureRandom());
            sslContext.getDefaultSSLParameters().setNeedClientAuth(true);

            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
            okHttpClientBuilder.hostnameVerifier((hostname, session) -> {
                if(hostname.equalsIgnoreCase(properties.getIp())) {
                    return true;
                } else {
                    return false;
                }
            });

            influxDB = InfluxDBFactory.connect(url, username, password, okHttpClientBuilder);
        } else {
            influxDB = InfluxDBFactory.connect(url, username, password);
        }

        influxDB.setDatabase(properties.getDatabase());

        return influxDB;
    }
}
