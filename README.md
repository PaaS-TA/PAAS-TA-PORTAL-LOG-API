# PAAS-TA-PORTAL-LOG-API
## Portal LOG API
Portal LOG API 란? CF Tail 및 APP 로그 서비스를 위해 구현한 서비스이다.
- CF Tail - 각각의 API 서비스로 제공(CF, DB, OBJECT STORAGE, LOG)
- App log - InfluxDB 접속 및 쿼리를 위한 API 제공

## 유의사항
- java 1.8
- SpringCloud Edgware.RELEASE 
- TomcatEmded 8.5.14
- Gradle 4.10.3
- Spring-boot 1.5.10
- Soket IO 1.7.14
- influxdb-java 2.14
