package org.openpaas.paasta.portal.log.api.common;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.openpaas.paasta.portal.log.api.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.Enumeration;

/**
 * Created by indra on 2018-05-09.
 */
@Component
public class TailSocket implements CommandLineRunner {


    private static final Logger LOGGER = LoggerFactory.getLogger(TailSocket.class);

    @Autowired
    public Environment env;


    @Value("${tailsocket.port}")
    public Integer tailPort;


    @Autowired
    private AppService appService;

    @Override
    public void run(String... args) throws Exception {
        startServer();
    }


    public void startServer() {
        String active = "";
        String hostName = "";

        Configuration config = new Configuration();
        try {
            for (String str : env.getActiveProfiles()) {
                active = str;
            }
            LOGGER.info("active=" + active);

            if (active.equals("local")) {
                hostName = "localhost";
            } else if (active.equals("dev")) {

                hostName = InetAddress.getLocalHost().getHostAddress();
                try {

                    Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
                    while (nienum.hasMoreElements()) {
                        NetworkInterface ni = nienum.nextElement();
                        Enumeration<InetAddress> kk = ni.getInetAddresses();
                        while (kk.hasMoreElements()) {
                            InetAddress inetAddress = kk.nextElement();
                            if (!inetAddress.isLoopbackAddress() &&
                                    !inetAddress.isLinkLocalAddress() &&
                                    inetAddress.isSiteLocalAddress()) {

                                hostName = inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                LOGGER.info("InetAddress.getLocalHost().getHostAddress()=" + hostName);


            } else {
                hostName = "0.0.0.0";
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostName = "localhost";
        }

        LOGGER.info("Host ::: " + hostName);

        config.setHostname(hostName);
        config.setPort(tailPort);
        config.setContext("/tailLog");
        final SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {

                LOGGER.info("onConnected");
                //String referer = client.getHandshakeData().getHttpHeaders().get("Referer");
                String appName = client.getHandshakeData().getSingleUrlParam("name");
                String orgName = client.getHandshakeData().getSingleUrlParam("org");
                String spaceName = client.getHandshakeData().getSingleUrlParam("space");

                try {
                    appName = URLDecoder.decode(appName, "UTF-8");
                    orgName = URLDecoder.decode(orgName, "UTF-8");
                    spaceName = URLDecoder.decode(spaceName, "UTF-8");
                }catch (Exception e){

                }
                LOGGER.info(appName);
                LOGGER.info(spaceName);
                LOGGER.info(orgName);

                appService.socketTailLogs(client, appName, orgName, spaceName);
            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {

                LOGGER.info("onDisconnected");
            }
        });
        server.addEventListener("send", ChatObject.class, new DataListener<ChatObject>() {

            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackSender) throws Exception {
                LOGGER.info("onSend: " + data.toString());
                server.getBroadcastOperations().sendEvent("message", data);
            }
        });
        LOGGER.info("Starting server...");
        serverInfo(server);
        server.start();
        LOGGER.info("Server started...");
    }

    public void serverInfo(SocketIOServer server){
        LOGGER.info("HostName :::: " +server.getConfiguration().getHostname());
        LOGGER.info("Context :::: " +server.getConfiguration().getContext());
        LOGGER.info("Port :::: " + server.getConfiguration().getPort());
        LOGGER.info("PingInterval :::: " +server.getConfiguration().getPingInterval());
        LOGGER.info("PingTimeout :::: " +server.getConfiguration().getPingTimeout());
    }
}
