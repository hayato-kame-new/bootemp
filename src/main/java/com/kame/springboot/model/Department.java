package com.kame.springboot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name="department")
public class Department {
	
	@Id
	private String departmentId;
	
	
	@NotEmpty
	private String departmentName;
	
	

}
