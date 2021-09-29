package com.kame.springboot;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kame.springboot.service.DepartmentService;



@Component
public class DepartmentNameValidator implements ConstraintValidator<UniqueDepName, String> {

	@Autowired
	DepartmentService departmentService;

	
	@Override
	public void initialize(UniqueDepName constraintAnnotation) {
		// TODO 自動生成されたメソッド・スタブ
		ConstraintValidator.super.initialize(constraintAnnotation);
	}


	// 引数は、部署名
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value == null || value.isEmpty()) {
			return false;
		}

        return departmentService.findByDepName(value) == null;
	}

}
