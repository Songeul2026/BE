<div align="center">

# ✍️ 손글 (SonGeul) [BackEnd]
### 손글씨가 금융이 된 순간
60세 이상 고령층이 종이 메모지에 손으로 적은 송금 정보를  
스마트폰으로 촬영하면 AI-OCR이 자동 인식하여  
송금을 완료하는 초간편 모바일 뱅킹 서비스

<br/>

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white"/>
<img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white"/>
<img src="https://img.shields.io/badge/AWS S3-569A31?style=flat-square&logo=amazons3&logoColor=white"/>
<img src="https://img.shields.io/badge/CLOVA OCR-03C75A?style=flat-square&logo=naver&logoColor=white"/>
<img src="https://img.shields.io/badge/Google Vision-4285F4?style=flat-square&logo=google&logoColor=white"/>
<img src="https://img.shields.io/badge/PaddleOCR-0062B0?style=flat-square&logo=paddlepaddle&logoColor=white"/>

<br/>
<br/>

| 이름 | 역할 | 담당 |
| --- | --- | --- |
| 김주희 | 팀장 | AI/OCR, Back-End |
| 박재영 | 팀원 | Back-End |
| 박혜송 | 팀원 | AI/OCR |
| 정은별 | 팀원 | Design |
| 김다은 | 팀원 | Front-End |

</div>

<br/>


## 🧩 About SonGeul

**SonGeul : 손글**은 고령층이 익숙하게 사용해 온 종이 메모지 작성 방식을 그대로 디지털 금융으로 전환한 서비스입니다.

기존 모바일뱅킹이 요구하는 복잡한 터치 조작 대신,
**촬영 → 확인 → 송금**의 3단계만으로 송금이 가능합니다.

3중 OCR 앙상블(CLOVA OCR + Google Vision API + PaddleOCR 자체 파인튜닝)을 통해
고령층 손글씨 인식률을 극대화하고, 신뢰도 기반 사용자 검증 UI로 오류를 사전에 차단합니다.

<br/>

## ✨ Main Features

### 1️⃣ 메모지 촬영 (OCR Upload)
손으로 적은 송금 정보를 스마트폰 카메라로 촬영하여 AI-OCR에 전달합니다.

- 메모지 촬영 (가이드 박스 제공)
- 자동 촬영 감지 (초점 맞춰지면 자동 캡처)
- 촬영 이미지 업로드
- 인식 실패 시 재촬영 요청

<br/>

### 2️⃣ AI-OCR 자동 인식 및 확인 (OCR Result)
3중 앙상블 엔진이 이미지 속 글씨를 인식하고, 필드별 신뢰도와 함께 결과를 표시합니다.

- CLOVA OCR + Google Vision + PaddleOCR 3중 앙상블
- 이름·은행·계좌번호·금액 자동 필드 분리
- 필드별 신뢰도 점수 시각화
- 신뢰도 70% 미만 항목 "확인 필요" 표시
- 사용자 직접 수정 기능

<br/>

### 3️⃣ 송금 확인 (Transfer Confirm)
인식된 정보를 최종 확인하고 송금을 실행합니다.

- 이름·은행·계좌번호·금액 최종 확인 화면
- 큰 글씨 UI로 고령층 가독성 확보
- 송금 실행
- 송금 완료 화면 제공

<br/>

### 4️⃣ 사용자 관리 (User)
기본적인 회원가입과 로그인 기능을 제공합니다.

- 회원가입 (이름, 전화번호, 비밀번호)
- 로그인 (JWT 토큰 인증)
- 송금 이력 조회
