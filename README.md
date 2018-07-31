# PAAS-TA-PORTAL-LOG-API
## 버전 정보
Portal v1.0 - CF API, DB API, OBJECT STORAGE API 통합 버전
Portal v2.0 - CF API만 제공하는 서비스로 수정중


## Portal LOG API
Portal LOG API 란? CF Tail 로그서비스를 위해서 별도의 서비스를 구현하였다.
- v1.0 - FINAL까지는 통합으로 제공
- v2.0 - 각각의 API 서비스로 제공

## 유의사항

개발 정보 - (v1.0 - FINAL)
- gradle 2.14 버전
- java 1.8 버전

개발 정보 - (v2.0 - SP1)
- java 1.8 버전
- SpringCloud Edgware.RELEASE 
- TomcatEmded 8.5.14
- Gradle 4.4.1
- Spring-boot 1.5.9
- Redis 1.3.1
