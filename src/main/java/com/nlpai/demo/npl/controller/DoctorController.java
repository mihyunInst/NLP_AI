package com.nlpai.demo.npl.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nlpai.demo.npl.NLPModel;
import com.nlpai.demo.npl.model.dto.Doctor;
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

		Doctor recommendedDoctor = doctorService.recommendDoctor((String) map.get("symptom"), nlpModel);

		if (recommendedDoctor != null) {
			return "추천 의사 이름 : " + recommendedDoctor.getDoctorName() + "\n전공명: " + recommendedDoctor.getMajorName()
					+ "\n전화번호: " + recommendedDoctor.getDoctorTel();
		} else {
			return "추천의를 찾을 수 없네요..";
		}
	}
}
