package com.nlpai.demo.npl.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nlpai.demo.npl.NLPModel;
import com.nlpai.demo.npl.model.dto.Doctor;
import com.nlpai.demo.npl.model.dto.FeedbackRequest;
import com.nlpai.demo.npl.model.dto.RecommendationResult;
import com.nlpai.demo.npl.model.service.DoctorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class DoctorController {

	private final DoctorService doctorService;

	private final NLPModel nlpModel;

	@GetMapping
	public String index() {
		return "index";
	}

	@GetMapping("/doctors")
	@ResponseBody
	public List<Doctor> getAllDoctors() {
		return doctorService.getAllDoctors();
	}

	@PostMapping("/recommend")
	@ResponseBody
	public String getRecommendation(@RequestBody Map<String, Object> map) {

		log.info("입력된 증상 {}", (String) map.get("symptom"));

		RecommendationResult result = doctorService.recommendDoctor((String) map.get("symptom"), nlpModel);

		if (result.getDoctor() != null) {
			return "추천 의사 이름 : " + result.getDoctor().getDoctorName() + "\n전공명: " + result.getDoctor().getMajorName()
					+ "\n전화번호: " + result.getDoctor().getDoctorTel();
		} else {
			return result.getMessage();
		}
	}
	
	/**
	 * @param fr
	 * @return
	 */
	@ResponseBody
	@PostMapping("/feedback")
    public ResponseEntity<String> feedback(@RequestBody FeedbackRequest fr) {
        try {
        	// 피드백 요청으로부터 받은 증상(symptom)과 진료 과(department)를 학습 데이터에 추가
            nlpModel.addTrainingData(fr.getSymptom(), fr.getDepartment());
            
            // 성공적으로 처리되었음을 클라이언트에 알림
            return ResponseEntity.ok("피드백 수신 및 학습 진행");
        } catch (Exception e) {
        	// 처리 중 에러가 발생한 경우, 에러 메시지와 함께 500 상태 코드 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("피드백 과정 오류 발생 : " + e.getMessage());
        }
    }
	
}
