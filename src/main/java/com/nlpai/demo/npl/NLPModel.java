package com.nlpai.demo.npl;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.nlpai.demo.npl.model.dto.StopWords;

import lombok.extern.slf4j.Slf4j;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

@Slf4j
@Component
@PropertySource("classpath:/config.properties")
public class NLPModel {

	private final String trainingDataPath;

	private DocumentCategorizerME categorizer; // 문서 분류기

	// 생성자 주입을 활용하는 방법 (객체 생성 후 필드 주입이 뒤늦게 이루어지기 때문에.. )
	public NLPModel(@Value("${nlp.training.data.path}") String trainingDataPath) throws Exception {
		this.trainingDataPath = trainingDataPath;
		initializeModel();
	}

	private void initializeModel() throws Exception {
		log.info("모델 초기화 시작");
		InputStreamFactory inputStreamFactory = new InputStreamFactory() {
			@Override
			public InputStream createInputStream() throws FileNotFoundException {

				return new FileInputStream(trainingDataPath);
			}
		};

		try (ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8)) {

			log.info("모델 학습 시작...");

			ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream) {
				@Override
				public DocumentSample read() throws IOException {
					String line = lineStream.read();
					if (line == null) {
						return null;
					}
					String[] parts = line.split(",", 2);
					if (parts.length != 2) {
						return null;
					}
					String[] tokens = parts[0].trim().split("\\s+");
					return new DocumentSample(parts[1].trim(), tokens);
				}
			};

			// 모델 학습 시 사용하는 설정 클래스
			TrainingParameters params = new TrainingParameters();

			params.put(TrainingParameters.ITERATIONS_PARAM, 100); // 학습 반복 횟수 - 100번 반복하여 학습함

			params.put(TrainingParameters.CUTOFF_PARAM, 1); // 학습 데이터 컷오프 값
			// 특정 피처가 몇번이상 등장해야 학습에 포함할 지 결정
			// 컷오프 값 = 1 이면 한번만 등장해도 그 피처를 학습에 포함함
			// -> 컷오프 값 높으면 : 가끔 등장하는 피처 무시
			// -> 컷오프 값 낮으면 : 가끔 등장하는 피처 학습

			DoccatModel model = DocumentCategorizerME.train("ko", sampleStream, params, new DoccatFactory());
			this.categorizer = new DocumentCategorizerME(model);

			log.info("모델이 학습을 성공적으로 완료했습니다~");
		}
	}

	// 증상 분류 메서드
	public String categorize(String symptom) {
		// 입력 문장 전처리
		List<String> docWordsList = preprocess(symptom); // 입력된 증상을 단어로 분리
		String[] docWords = docWordsList.toArray(new String[0]);
		double[] outcomes = categorizer.categorize(docWords); // 분류 결과 얻기
		
		log.info("단어 배열 {}", (Object) docWords);
		log.info("카테고리 일치 확률 {}", outcomes);
		
		// outcomes의 모든 확률이 동일한 경우 감지 (== AI가 정확한 판단을 하지 못함)
	    boolean allEqual = true;
	    for (int i = 1; i < outcomes.length; i++) {
	        if (outcomes[i] != outcomes[0]) {
	            allEqual = false;
	            break;
	        }
	    }
	    
	    if (allEqual) {
	        log.warn("모든 카테고리의 일치 확률이 동일합니다. 모델이 올바르게 분류하지 못했습니다.");
	        return "nodata";
	    }

		return categorizer.getBestCategory(outcomes); // 가장 가능성이 높은 카테고리 반환
	}

	// 입력 문장 전처리 및 n-그램 생성 메서드
	// n-그램(n-gram) : 연속된 n개의 아이템으로 이루어진 시퀀스
	private List<String> preprocess(String symptom) {
		log.info("전처리 시작...");
		// 불필요한 공백 및 특수문자 제거
		symptom = symptom.trim().replaceAll("[^가-힣a-zA-Z0-9\\s]", ""); // 한글, 영문자, 숫자, 공백을 제외한 모든 문자를 제거
		String[] words = symptom.split("\\s+"); // 하나 이상의 공백 문자를 의미

		log.info("전처리 후 단어 배열: {}", (Object) words); // 전처리 후 단어 배열 로그 출력

		// 불용어 필터링 및 n-그램 생성 (1-그램 및 2-그램)
		List<String> ngrams = new ArrayList<>();

		for (int i = 0; i < words.length; i++) { // words 각 요소 순회
			log.info("현재 단어: {}", words[i]); // 현재 단어 로그 출력

			if (!StopWords.isStopWord(words[i])) { // 불용어 필터링

				ngrams.add(words[i]); // 1-그램(단일 단어) 추가

				// 현재 단어가 배열의 마지막 단어가 아니고, 다음단어도 불용어가 아닐 때
				if (i < words.length - 1 && !StopWords.isStopWord(words[i + 1])) {
					ngrams.add(words[i] + " " + words[i + 1]); // 2-그램 추가
				}
			}

		}

		log.info("생성된 n-그램: {}", ngrams); // 생성된 n-그램 로그 출력
		log.info("전처리 완료!");
		return ngrams;
	}

	/**
	 * 새로운 학습 데이터를 추가하고 모델을 재학습시키는 메서드
	 * 
	 * @param symptom
	 * @param department
	 * @throws Exception 
	 */
	public void addTrainingData(String symptom, String department) throws Exception {
		// 파일에 새로운 학습 데이터를 추가하기 위해 FileWriter와 BufferedWriter를 사용
		try (FileWriter fw = new FileWriter(trainingDataPath, true); // 파일을 append 모드로 열기
				BufferedWriter bw = new BufferedWriter(fw)) { // 버퍼를 사용해 파일에 쓰기
			
			bw.newLine(); // 새로운 라인 추가
			bw.write(symptom + "," + department); // 새로운 학습 데이터를 파일에 쓰기
		}

		// 새로운 데이터를 반영하여 모델을 재학습시키는 메서드 호출
		initializeModel();

	}

}

/*
 * Performing 100 iterations. 1: ... loglikelihood=-776.9180683866715
 * 0.1111111111111111 2: ... loglikelihood=-462.24446061968683
 * 0.9845679012345679 3: ... loglikelihood=-325.71850864498776
 * 0.9938271604938271 4: ... loglikelihood=-250.64916359131857
 * 0.9969135802469136 5: ... loglikelihood=-203.27878242612152
 * 0.9969135802469136 6: ... loglikelihood=-170.73873097026637
 * 0.9969135802469136 7: ... loglikelihood=-147.06011520444238
 * 0.9969135802469136 8: ... loglikelihood=-129.09185726567983 1.0 9: ...
 * loglikelihood=-115.01237709784371 1.0 10: ...
 * loglikelihood=-103.69578688623884 1.0
 * 
 * 순서대로 Iteration (반복) - 모델 학습이 몇 번째 반복되는지 loglikelihood (로그우도) - 현재 모델이 데이터를
 * 얼마나 잘 설명하는지(값이 작을수록 모델이 데이터 설명을 더 잘함) Accuracy (정확도) - 학습 데이터에 대한 모델의 정확도(0과
 * 1 사이의 값으로 표시되며, 1에 가까울수록 정확도가 높음)
 * 
 * 
 */
