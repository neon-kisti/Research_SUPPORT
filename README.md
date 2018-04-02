# Research_SUPPORT
문헌 연구 지원
# SCOPUS Analysis Query
SCOPUS 계량 통계 분석 추출을 위해 사용된 쿼리 정보를 공유합니다.
# SCOPUS_IBS_SEARCH
이클립스 상에서 실행 가능하도록 처리 됨
##### 실행방법 
    1. 이클립스의 RUN 메뉴 선택
	2. Run Configure 선택
    3. Run Configure 화면에서 Java Application 선택
    4. 오른쪽 버튼 선택 후 단축메뉴에서 New 선택
    5. 이름은 클래스명 또는 다른이름으로 입력
    6. Browse 버튼 선택후 프로젝트를 SCOPUS_IBS_APPLICATION 선택
    7. Main 클래스를 ExecutorIBSSearch 선택
    8. Arguments 탭에서 Program Arguments 항목에서 -sr${분석을 위한 수집 데이터 경로}  -tg${검색된 결과를 저장하기 위한 경로}
    9. Apply 버튼을 누르고 창을 Run Configures 화면을 닫는다.
    10. src 폴더의 log4j.xml에서 로그파일 저장할 위치 설정 -> 해당 검색정보에 대해서 검색식, 검색 대상 데이터 Cell 위치, 분석 데이터 정보 확인 
    11. ExecutorIBSSearch 클래스를 열고 F11 또는 Run Counfigures를 통해 해당 애플리케이션 선택후 Run버튼 클릭 
    
**그이후 해당 클래스 실행시 Program Arguments 항목만 수정해서 중복 실행 가능합니다.**

# G-PASS EXPORT
##### 파일목록
	1. G-PASS_EXPORT_SEARCH.zip : 특허검색식을 활용하여 데이터 Export 모듈이 포함된 프로젝트
	2. 특허 검색식을 활용하여 데이터 Export.txt : Export 모듈 실행 방법 설명