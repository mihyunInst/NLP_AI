package com.nlpai.demo.npl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationResult {
	private boolean isConfident; // AI 모델이 증상을 분류하는 데 확신이 있는지 여부
    private String message; // 추천 결과에 대한 설명 메시지
    private Doctor doctor; // 추천된 의사에 대한 정보
}
