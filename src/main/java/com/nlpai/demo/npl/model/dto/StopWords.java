package com.nlpai.demo.npl.model.dto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

//불용어(불필요한 단어를 제거)
@Slf4j
@Component
@PropertySource("classpath:/config.properties")
public class StopWords {

	private static final Set<String> stopWords = new HashSet<>();

	private static String stopWordsFilePath;

	@Value("${nlp.stopwords.path}")
	public void setStopWordsFilePath(String filePath) {
		stopWordsFilePath = filePath;
		loadStopWords();
	}

	private static void loadStopWords() {
		try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFilePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				stopWords.add(line.trim());
			}
		} catch (IOException e) {
			System.err.println("불용어 파일을 읽는 동안 오류가 발생했습니다: " + e.getMessage());
		}
	}

	public static boolean isStopWord(String word) {
		log.info("불용어 거르는 중... {}", word);

		if (stopWords.contains(word)) {
			log.info("걸러진 불용어 {}", word);
		}

		return stopWords.contains(word);
	}
}
