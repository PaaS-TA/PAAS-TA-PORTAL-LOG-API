package org.openpaas.paasta.portal.log.api.config;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

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


        try {
            if(properties.getHttpsEnabled()) {
                ClassPathResource certificateResource = new ClassPathResource("certs/influx_cert.crt");
                InputStream certificateInputStream = certificateResource.getInputStream();

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);
                keyStore.setCertificateEntry("influxdb", certificate);
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, null);

                OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
                okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
                okHttpClientBuilder.hostnameVerifier((hostname, session) -> hostname.equalsIgnoreCase(properties.getIp()));

                influxDB = InfluxDBFactory.connect(url, username, password, okHttpClientBuilder);
            }
            else {
                    influxDB = InfluxDBFactory.connect(url, username, password);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        influxDB.setDatabase(properties.getDatabase());

        return influxDB;
    }
}
