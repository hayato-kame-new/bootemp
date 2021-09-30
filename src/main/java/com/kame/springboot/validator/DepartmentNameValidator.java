package com.kame.springboot.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.kame.springboot.annotation.UniqueDepName;
import com.kame.springboot.service.DepartmentService;


public class DepartmentNameValidator implements ConstraintValidator<UniqueDepName, String> {

	@Autowired
	DepartmentService departmentService;

	
	@Override
	public void initialize(UniqueDepName constraintAnnotation) {
		// TODO 自動生成されたメソッド・スタブ
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	
	//NULLチェックを行い、NULLだった場合はtrueを返してください。それが決まりです。なぜなら、NULLチェックは@NotNullを併用して行うことになっているからです。

	// 引数は、部署名
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value == null || value.isEmpty()) {
			return false;
		}
        return departmentService.findByDepName(value) == null;
	}

}
