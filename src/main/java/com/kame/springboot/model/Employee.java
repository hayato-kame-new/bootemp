package com.kame.springboot.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
// javax.validation.constraints.NotEmpty  こちらを使う
import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;  

// Departmentエンティティ  Photoエンティティの従エンティティ(子エンティティ)

@Entity
@Table(name = "employee")
public class Employee {
	
	@Id
	@Column(name = "employeeid")  // 全て小文字にしてください、PostgreSQL のカラム名は全て小文字じゃないとエラーになるので
	private String employeeId;
	
	
	@Column(name = "name", length=10485760 )
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
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "hiredate") // 全て小文字のカラム名を指定すること
	private Date hireDate;  // java.util.Date 
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "retirementdate", nullable = true) // 全て小文字のカラム名を指定すること
	private Date retirementDate;  // java.util.Date 

	// 相互参照なEntityクラス化する  リレーション このEmployeeエンティティは、Departmentエンティティに対して、従エンティティです。
	// 従業員側の部署ID(部署のプライマリーキー)は  クラス化した場合に 部署クラスへの参照になります。
	// employeeテーブルにdepartmentのカラムがあるわけではありません。なくて良い。
	// 従テーブルのemployeeテーブルに外部制約つけた
//	ALTER TABLE employee
//	ADD FOREIGN KEY (departmentId) 
//	REFERENCES department (departmentId);
	
	
	//model.Employee column: departmentid (should be mapped with insert="false" update="false")のエラーが出たら、insertable=false,  updatable=false　を書くこと
	
	// @JoinColumn(name="departmentid", insertable=false,  updatable=false) // これを付け足したが、果たして良いのか？？？？
	@ManyToOne  // employeeを全て消しても、Departmentは残したいので、cascadeはつけません
	Department department;  // @ManyToOne  だから、フィールド名は、単数形にしてください。アクセッサも追加すること ゲッター セッター
	
	// 相互参照なEntityクラス化する  リレーション このEmployeeエンティティは、Photoエンティティに対して、従エンティティです。
	// 従業員側のphotoID(photoテーブルのプライマリーキー)は  クラス化した場合に Photoクラスへの参照になります。
	// employeeテーブルにphotoのカラムがあるわけではありません。なくていい。
	// 従テーブルのemployeeテーブルに外部制約つけた
//	ALTER TABLE employee
//	ADD FOREIGN KEY (photoId) 
//	REFERENCES photo (photoId);
	
	@OneToOne
	Photo photo;  // @OneToOne  だから、フィールド名は、単数形にしてください。アクセッサも追加すること ゲッター セッター
	
	

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
	//	this.department = department;
	//	this.photo = photo;
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	
	
	
}
