package com.nlpai.demo.npl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.nlpai.demo.npl.model.dto.StopWords;

import jakarta.annotation.PostConstruct;
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
		InputStreamFactory inputStreamFactory = new InputStreamFactory() {
			@Override
			public InputStream createInputStream() throws FileNotFoundException {
				log.info("trainingDataPath {}", trainingDataPath);
				return new FileInputStream(trainingDataPath);
			}
		};

		try (ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8)) {
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

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 200);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);

			DoccatModel model = DocumentCategorizerME.train("ko", sampleStream, params, new DoccatFactory());
			this.categorizer = new DocumentCategorizerME(model);
		}
	}

	// 증상 분류 메서드
	public String categorize(String symptom) {
		// 입력 문장 전처리
		List<String> docWordsList = preprocess(symptom); // 입력된 증상을 단어로 분리
		String[] docWords = docWordsList.toArray(new String[0]);
		double[] outcomes = categorizer.categorize(docWords); // 분류 결과 얻기

		log.info("입력 증상 {}", docWords);
		log.info("분류 결과 {} ", outcomes);

		return categorizer.getBestCategory(outcomes); // 가장 가능성이 높은 카테고리 반환
	}

	// 입력 문장 전처리 및 n-그램 생성 메서드
	private List<String> preprocess(String symptom) {
		// 불필요한 공백 및 특수문자 제거
		symptom = symptom.trim().replaceAll("[^가-힣a-zA-Z0-9\\s]", "");
		String[] words = symptom.split("\\s+");

		// 불용어 필터링 및 n-그램 생성 (1-그램 및 2-그램)
		List<String> ngrams = new ArrayList<>();
		for (int i = 0; i < words.length; i++) {
			if (!StopWords.isStopWord(words[i])) {
				ngrams.add(words[i]); // 1-그램 추가
				if (i < words.length - 1 && !StopWords.isStopWord(words[i + 1])) {
					ngrams.add(words[i] + " " + words[i + 1]); // 2-그램 추가
				}
			}
		}
		return ngrams;
	}
}
