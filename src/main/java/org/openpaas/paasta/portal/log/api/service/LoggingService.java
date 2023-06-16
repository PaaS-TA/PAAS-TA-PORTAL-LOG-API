package org.openpaas.paasta.portal.log.api.service;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.openpaas.paasta.portal.log.api.config.InfluxDBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoggingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingService.class);

    @Autowired
    private InfluxDB influxDB;

    @Autowired
    private InfluxDBProperties influxDBProperties;

    public List<Object> getLogData(String guid, String sTime, String eTime, String keyword) {
        String measurement = influxDBProperties.getMeasurement();
        String limit = influxDBProperties.getLimit();
        String dbName = influxDBProperties.getDatabase();

        String sql = "SELECT time, message FROM " + measurement + " WHERE time >= '" + sTime + "' AND time <= '" + eTime + "' AND message =~ /\"cf_app_id\":\"" + guid + "\"/";

        if(keyword != null && !keyword.isEmpty() && keyword != "") {
            sql += " AND message =~ /" + keyword + "*/";
        }

        sql += " ORDER BY time DESC LIMIT " + limit;

        List<Object> logList = new ArrayList<>();

        try {
            QueryResult queryResult = influxDB.query(new Query(sql, dbName));
            List<QueryResult.Series> resultList = queryResult.getResults().get(0).getSeries();
            if(resultList != null) {
                logList.addAll(resultList.get(0).getValues());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return logList;
    }
}
