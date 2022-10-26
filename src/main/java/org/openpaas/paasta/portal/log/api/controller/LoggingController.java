package org.openpaas.paasta.portal.log.api.controller;

import org.openpaas.paasta.portal.log.api.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoggingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingController.class);

    @Autowired
    private LoggingService loggingService;

    /**
     * 해당 조직에 대한 앱 로그를 조회한다.
     *
     * @return Map(자바클래스)
     */
    @GetMapping("/log")
    public List<Object> getLogData(
            @RequestParam(name = "name") String appName,
            @RequestParam(name = "org") String orgName,
            @RequestParam(name = "stime", required = false) String startTime,
            @RequestParam(name = "etime", required = false) String endTime,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {

        LOGGER.debug("getLogData() :: name = " + appName + ", org = " + orgName + ", startTime = " + startTime + ", endTime = " + endTime + ", keyword = " + keyword);

        return loggingService.getLogData(appName, orgName, startTime, endTime, keyword);
    }

}
