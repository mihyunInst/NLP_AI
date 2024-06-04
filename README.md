# AI 의사 추천 시스템

이 프로젝트는 OpenNLP를 사용하여 사용자로부터 제공받은 증상을 기반으로 의사를 추천하는 AI 시스템입니다. <br>
텍스트 전처리, 불용어 제거, 모델 학습 기능을 포함하고 있습니다.

![test영상](https://github.com/mihyunInst/NLP_AI/assets/118782275/88de035e-bdfa-44d9-9e9d-64111564924f)

## 특징

- **증상 분류**: 사용자로부터 입력받은 증상을 다양한 의학 분야로 분류
- **불용어 제거**: 사용자 입력에서 불필요한 단어를 제거하여 분류 정확도를 향상시킴
- **모델 학습**: 제공된 학습 데이터를 사용하여 모델을 학습시킴

### 24.06.04 기능 추가
- **새로운 데이터 추가 학습** : 사용자가 입력한 증상을 직접 AI에게 학습시키고 싶을 때 [증상과 선택한 진료과목 분야]로 분류시켜 학습시킴

![test영상2](https://github.com/mihyunInst/NLP_AI/assets/118782275/c82574c8-199c-4594-8cee-8f8b5c84a481)


## 프로젝트 구조

- `src/main/java/com/nlpai/demo/npl`:
  - `NLPModel.java`: 텍스트 전처리, 모델 학습, 증상 분류 등을 처리
  - `StopWords.java`: 불용어 목록과 단어가 불용어인지 확인하는 메서드를 포함
  - `DoctorService.java / DoctorMapper.java`: 비즈니스 로직과 데이터베이스 작업 포함
  - `RecommendationResult.java`: AI 모델의 증상 판독 분류 내용 저장 DTO

- `src/main/resources`:
  - `config.properties`: 경로 및 기타 변수를 설정하기 위한 구성 파일(깃헙에 올리지 않음 - 요청 시 전달드릴게요)

## 서버 컴퓨터 C:/ 모 패키지 내부 (깃헙에 올리지 않음 - 요청 시 전달드릴게요)
  - `trainingData.txt`: 모델 학습을 위한 학습 데이터를 포함(24.06.04 기능 추가 후 새로운 데이터 계속적으로 업데이트) 
  - `stopwords.txt` : 불용어 처리를 위한 한글 불용어 데이터를 포함
