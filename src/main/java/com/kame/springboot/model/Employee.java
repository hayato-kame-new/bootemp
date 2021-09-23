package com.kame.springboot.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
// javax.validation.constraints.NotEmpty  こちらを使う
import javax.validation.constraints.NotEmpty;  

// Departmentエンティティ  Photoエンティティの従エンティティ(子エンティティ)

@Entity
@Table(name = "employee")
public class Employee {
	
	@Id
	@Column(name = "employeeid")  // 全て小文字にしてください、PostgreSQL のカラム名は全て小文字じゃないとエラーになるので
	private String employeeId;
	
	
	@Column(name = "name",length = 20)
	@NotEmpty
	private String name;
	
	@Column(name = "age")
	@Min(0)
	@Max(110)
	private int age;
	
	@Column(name = "gender")
	@Min(value = 1)
	@Max(value = 2)
	private int gender; // 性別 1:男 2:女
	
	@Column(name = "photoid")  // 全て小文字のカラム名を指定すること
	private int photoId;  // リレーションがあるカラム
	
	@Column(name = "zipnumber")  // 全て小文字のカラム名を指定すること
	private String zipNumber;
	
	@Column(name = "pref")
	private String pref;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "departmentid") // 全て小文字のカラム名を指定すること
	private String departmentId; // リレーションのあるカラム
	
	@Column(name = "hiredate") // 全て小文字のカラム名を指定すること
	private Date hireDate;  // java.util.Date 
	
	@Column(name = "retirementdate", nullable = true) // 全て小文字のカラム名を指定すること
	private Date retirementDate;  // java.util.Date 

	/**
	 * 引数なしのコンストラクタ
	 */
	public Employee() {
		super();
	}

	/**
	 * 引数ありのコンストラクタ
	 * @param employeeId
	 * @param name
	 * @param age
	 * @param gender
	 * @param photoId
	 * @param zipNumber
	 * @param pref
	 * @param address
	 * @param departmentId
	 * @param hireDate
	 * @param retirementDate
	 */
	public Employee(String employeeId, @NotEmpty String name, @Min(0) @Max(110) int age, @Min(1) @Max(2) int gender,
			int photoId, String zipNumber, String pref, String address, String departmentId, Date hireDate,
			Date retirementDate) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.photoId = photoId;
		this.zipNumber = zipNumber;
		this.pref = pref;
		this.address = address;
		this.departmentId = departmentId;
		this.hireDate = hireDate;
		this.retirementDate = retirementDate;
	}

	/**
	 * 住所を表示する fullAdressプロパティ を取得する
	 * @return String
	 */
	public String getFullAddress() {
		return "〒" + this.zipNumber + this.pref + this.address;
	}
	
	/**
	 * 性別をint型からString型の表示にする
	 * @param gender
	 * @return str
	 */
	public String getStringGender(int gender) {
		String str = "";
		switch(gender) {
		case 1:
			str = "男";
			break;
		case 2:
			str = "女";
			break;
		}
		return str;
	}
	
	// アクセッサ
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	public String getZipNumber() {
		return zipNumber;
	}

	public void setZipNumber(String zipNumber) {
		this.zipNumber = zipNumber;
	}

	public String getPref() {
		return pref;
	}

	public void setPref(String pref) {
		this.pref = pref;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getRetirementDate() {
		return retirementDate;
	}

	public void setRetirementDate(Date retirementDate) {
		this.retirementDate = retirementDate;
	}

	
	
}
