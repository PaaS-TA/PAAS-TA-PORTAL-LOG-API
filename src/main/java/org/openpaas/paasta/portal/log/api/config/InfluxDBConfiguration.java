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

    private static final String INFLUXDB_SSL_KEY_PATH = "/var/vcap/jobs/paas-ta-portal-log-api/data/";
    private static final String INFLUXDB_SSL_PASSWORD = "paasta2022";

    private final InfluxDBProperties properties;

    public InfluxDBConfiguration(InfluxDBProperties properties) {
        this.properties = properties;
    }

    @Bean
    public InfluxDB influxDB() throws Exception {
        InfluxDB influxDB = null;

        if(properties.getHttpsEnabled()) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(INFLUXDB_SSL_KEY_PATH + "keystore.p12"), INFLUXDB_SSL_PASSWORD.toCharArray());
            keyStore.load(new FileInputStream(INFLUXDB_SSL_KEY_PATH + "keystore.p12"), INFLUXDB_SSL_PASSWORD.toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(INFLUXDB_SSL_KEY_PATH + "truststore.jks"), INFLUXDB_SSL_PASSWORD.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("SSL");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, INFLUXDB_SSL_PASSWORD.toCharArray());

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

            influxDB = InfluxDBFactory.connect(
                    properties.getUrl(),
                    properties.getUsername(),
                    properties.getPassword(),
                    okHttpClientBuilder
            );
        } else {
            influxDB = InfluxDBFactory.connect(
                    properties.getUrl(),
                    properties.getUsername(),
                    properties.getPassword()
            );
        }

        influxDB.setDatabase(properties.getDatabase());

        return influxDB;
    }
}
