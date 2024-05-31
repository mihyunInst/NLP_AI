package com.nlpai.demo.npl.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nlpai.demo.npl.NLPModel;
import com.nlpai.demo.npl.mapper.DoctorMapper;
import com.nlpai.demo.npl.model.dto.Doctor;
import com.nlpai.demo.npl.model.dto.DoctorMajor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoctorService {

	private final DoctorMapper doctorMapper;

	public List<Doctor> getAllDoctors() {
		return doctorMapper.findAllDoctors();
	}

	public List<DoctorMajor> getAllDoctorMajors() {
		return doctorMapper.findAllDoctorMajors();
	}

	public List<Doctor> getDoctorsByMajorCode(Long majorCode) {
		return doctorMapper.findDoctorsByMajorCode(majorCode);
	}

	// 의사 추천
	public Doctor recommendDoctor(String symptom, NLPModel nlpModel) {
		String majorName = nlpModel.categorize(symptom);
		List<DoctorMajor> majors = getAllDoctorMajors();

		log.info("ai 전공추천 {}", majorName);
		log.info("db 전공리스트 {}", majors);

		for (DoctorMajor major : majors) {
			if (major.getMajorName().contains(majorName)) {
				List<Doctor> doctors = getDoctorsByMajorCode(major.getMajorCode());
				if (!doctors.isEmpty()) {
					return doctors.get(0); // 첫 번째 의사를 추천
				}
			}
		}
		return null; // 추천할 의사가 없는 경우
	}
}
