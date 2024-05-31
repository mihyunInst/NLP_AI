package com.nlpai.demo.npl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.nlpai.demo.npl.model.dto.Doctor;
import com.nlpai.demo.npl.model.dto.DoctorMajor;

@Mapper
public interface DoctorMapper {

	 @Select("SELECT * FROM DOCTOR")
	 List<Doctor> findAllDoctors();

	 @Select("SELECT * FROM DOCTOR_MAJOR")
	 List<DoctorMajor> findAllDoctorMajors();

	 @Select("SELECT * FROM DOCTOR WHERE MAJOR_CODE = #{majorCode}")
	 List<Doctor> findDoctorsByMajorCode(Long majorCode);
}
