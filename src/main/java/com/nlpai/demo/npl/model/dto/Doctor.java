package com.nlpai.demo.npl.model.dto;

import lombok.Data;

@Data
public class Doctor {
	private Long doctorNo;
    private Long majorCode;
    private String doctorName;
    private String doctorTel;
    
    // 전공명 join
    private String majorName;
}
