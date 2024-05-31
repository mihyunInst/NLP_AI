package com.nlpai.demo.npl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.nlpai.demo.npl.model.dto.Doctor;
import com.nlpai.demo.npl.model.dto.DoctorMajor;

@Mapper
public interface DoctorMapper {

	@Select("SELECT DOCTOR_NO, MAJOR_CODE, DOCTOR_NAME, DOCTOR_TEL, MAJOR_NAME " 
			+ "FROM DOCTOR "
			+ "JOIN DOCTOR_MAJOR USING (MAJOR_CODE)")
	List<Doctor> findAllDoctors();

	@Select("SELECT * FROM DOCTOR_MAJOR")
	List<DoctorMajor> findAllDoctorMajors();

	@Select("SELECT DOCTOR_NO, MAJOR_CODE, DOCTOR_NAME, DOCTOR_TEL, MAJOR_NAME " 
			+ "FROM DOCTOR "
			+ "JOIN DOCTOR_MAJOR USING (MAJOR_CODE) " + "WHERE MAJOR_CODE = #{majorCode}")
	List<Doctor> findDoctorsByMajorCode(Long majorCode);
}
