# patron-page
simple patron-page service project

## 프로젝트 환경
- JDK 1.8
- Spring Boot 2.1.8.RELEASE
- JPA2
- Gradle 4.8+
- DB : h2
- springfox-swagger-ui 2.9.2

## 프로젝트 실행 방법
1. java jar patron-page.jar (8080 port 사용)
2. application 을 import 후 tomcat deploy

## API 샘플 데이터 
- swagger 접속 : http://localhost:8080/swagger-ui.html#/project-controller
- H2 DB Console : http://localhost:8080/h2 
 (ID/PW : admin/admin, 자세한 jdbc 정보는 application.properties 파일 참조)

1. 더미 데이터 생성 : [GET] http://localhost:8080/project/dummy
2. 특정 프로젝트 생성 : [POST] http://localhost:8080/project 
```
[INPUT]
{
    "project_name" : "COMPUTER000",
    "project_desc" : "컴퓨터000",
    "artist_name" : "USER001",
    "artist_email" : "USER000@gmail.com",
    "artist_phone_number" : "010-1234-0000", 
    "project_start_time" : "2020-08-30 17:00:00",
    "project_end_time" : "2020-09-12 21:00:00",
    "goal_amt" : 10000000,
    "patron_users" : 0,
    "patron_amt" : 0,
    "project_status" : "READY"	
}
```
3. 특정 프로젝트 수정 : [PUT] http://localhost:8080/project 
```
[INPUT]
{
    "project_id" : "b70b3f05-dc0b-44fb-aace-baaaa24b12fb",
    "project_name" : "COMPUTER002",
    "project_desc" : "컴퓨터002",
    "artist_name" : "USER002",
    "artist_email" : "USER002@gmail.com",
    "artist_phone_number" : "010-1234-2222", 
    "project_start_time" : "2020-08-30 17:00:00",
    "project_end_time" : "2020-09-12 21:00:00",
    "goal_amt" : 10000000,
    "patron_users" : 0,
    "patron_amt" : 0,
    "project_status" : "READY"	
}
```
4. 특정 프로젝트 삭제 : [DELETE] http://localhost:8080/project 
```
[INPUT]
{
    "project_id" : "b70b3f05-dc0b-44fb-aace-baaaa24b12fb"
}
```
5. 프로젝트 리스트 보기 : [GET] http://localhost:8080/project
```
parameter 없이 호출하는 경우 : default 보기 (10개 리스트 조회)
정렬된 결과를 원하는 경우 : Pageable 활용

(예제)
시작일 순 : http://localhost:8080/project?sort=projectStartTime
마감일 순 : http://localhost:8080/project?sort=projectEndTime
목표액 순 : http://localhost:8080/project?sort=goalAmt
후원액 순 : http://localhost:8080/project?sort=patronAmt

★ swagger 를 사용하는 경우 [GET] /project API에 sort 를 입력해 조회 가능
```
6. 특정 프로젝트의 상세 정보 보기 : [GET] http://localhost:8080/project/info 
```
[Parameter]
String : projectId (UUID)
- http://localhost:8080/project/info?projectId=93478711-9436-427c-bc2f-123f55cea35c
```
7. 특정 프로젝트 후원하기 : [PUT] http://localhost:8080/project/support
```
[INPUT]
{
    "project_id" : "ee4861a0-0f1b-4ee3-a28e-c114cbc5467c",
    "sponsor_amt" : 200000
}
```

## 핵심 아이디어
1. DB PK 가 UUID 이므로 파라미터로 사용자에게 노출되지 않게 처리
- Request 에서 projectId 를 받아 처리하는 경우, Front-end 에서 일정 수준의 검증을 거친 후 PK가 넘어왔다는 전제사항 필요
2. Sort 시 @Pageable annotaion 활용
3. transaction 발생 시 최대한 DML이 필요한 부분만 발생하도록 프로그램 구현

## 기능 구현 내용
1. 공통
- POST/UPDATE시에는 json 형식의 데이터를 사용합니다. (@RequestBody 적용)
- API 결과는 json 형식의 데이터를 사용합니다. (@ResponseBody 적용) 
2. 프로젝트 등록/수정시
- 사용자 입력값이 조건에 맞지 않을때 에러 메시지를 전달합니다. (@Valid 체크 후 message 세팅)
3. 프로젝트 삭제
- 삭제할 해당 프로젝트가 없을때 에러 메시지를 전달합니다.
4. 프로젝트 리스트 보기
- 리스트에 보여지는 프로젝트 정보 : 프로젝트 제목, 창작자 이름, 목표액, 후원수, 후원액, 프로젝트 상태, 시작일, 마감일
- 시스템은 프로젝트 상태를 업데이트 합니다.
  - 준비중 : 현재시간 < 프로젝트 시작일
  - 진행중 : 프로젝트 시작일 ≤ 현재시간 < 프로젝트 마감일
5. 프로젝트 정보 보기
- 프로젝트 id를 제외한 모든 프로젝트 정보
- 시스템은 프로젝트 상태를 업데이트 합니다.
  - 준비중 : 현재시간 < 프로젝트 시작일
  - 진행중 : 프로젝트 시작일 ≤ 현재시간 < 프로젝트 마감일
6. 사용자가 후원을 할 때
- 시스템은 입력받은 후원액과 전체 후원액을 합산하여 업데이트 합니다.
- 시스템은 후원수를 1 증가시킵니다.
- 시스템은 프로젝트 상태를 업데이트 합니다.
  - 준비중 : 현재시간 < 프로젝트 시작일
  - 진행중 : 프로젝트 시작일 ≤ 현재시간 < 프로젝트 마감일
  - 성공 : 현재시간 ≥ 프로젝트 마감일 && 후원액 ≥ 목표액
  - 실패 : 현재시간 ≥ 프로젝트 마감일 && 후원액 < 목표액


