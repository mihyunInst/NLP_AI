package com.nlpai.demo.npl.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nlpai.demo.npl.NLPModel;
import com.nlpai.demo.npl.mapper.DoctorMapper;
import com.nlpai.demo.npl.model.dto.Doctor;
import com.nlpai.demo.npl.model.dto.DoctorMajor;
import com.nlpai.demo.npl.model.dto.RecommendationResult;

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
	public RecommendationResult recommendDoctor(String symptom, NLPModel nlpModel) {
		// 증상을 분류하여 전공명 추천
		String majorName = nlpModel.categorize(symptom);
		
		if (majorName.equalsIgnoreCase("nodata")) {
	        return new RecommendationResult(false, "nodata", null);
	    }
		
		List<DoctorMajor> majors = getAllDoctorMajors();
		
		log.info("ai 전공추천 {}", majorName);
		log.info("db 전공리스트 {}", majors);

		// 추천받은 전공명을 DB의 전공명과 비교
        for (DoctorMajor major : majors) {
            if (major.getMajorName().contains(majorName)) {
                // 전공 코드에 해당하는 의사 목록 조회
                List<Doctor> doctors = getDoctorsByMajorCode(major.getMajorCode());
                if (!doctors.isEmpty()) {
                    return new RecommendationResult(true, "추천 의사가 있습니다.", doctors.get(0)); // 첫 번째 의사를 추천
                }
            }
        }

        // 추천할 의사가 없는 경우
        return new RecommendationResult(true, "추천 의사가 없습니다.", null);
	}
}
