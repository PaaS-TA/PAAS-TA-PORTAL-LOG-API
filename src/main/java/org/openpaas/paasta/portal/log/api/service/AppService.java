package org.openpaas.paasta.portal.log.api.service;


import com.corundumstudio.socketio.SocketIOClient;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventResponse;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.LogsRequest;
import org.openpaas.paasta.portal.log.api.common.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 앱 서비스 - 애플리케이션 정보 조회, 구동, 정지 등의 API 를 호출 하는 서비스이다.
 *
 * @author 조민구
 * @version 1.0
 * @since 2016.4.4 최초작성
 */
@Service
public class AppService extends Common {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppService.class);




    private void printLog(LogMessage msg) {
        LOGGER.info(" [" + msg.getSourceType() + "/" + msg.getSourceInstance() + "] [" + msg.getMessageType() + msg.getMessageType() + "] " + msg.getMessage());
    }


    public SocketIOClient socketTailLogs(SocketIOClient client, String appName, String orgName, String spaceName) {
        DefaultCloudFoundryOperations cloudFoundryOperations = cloudFoundryOperations(connectionContext(), tokenProvider(adminUserName,adminPassword), orgName, spaceName);
        cloudFoundryOperations.applications()
                .logs(LogsRequest.builder()
                        .name(appName)
                        .build()
                ).subscribe((msg) -> {
                    printLog(msg);
                    client.sendEvent("message", " [" + msg.getSourceType() + "/" + msg.getSourceInstance() + "] [" + msg.getMessageType() + msg.getMessageType() + "] " + msg.getMessage());
                },
                (error) -> {
                    error.printStackTrace();
                }
        );
        return client;
    }

}
