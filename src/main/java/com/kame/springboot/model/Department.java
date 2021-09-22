package com.kame.springboot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name="department") // postgresだと、全て小文字なので合わせる
public class Department {
	
	@Id
	@Column(name = "departmentid", length = 20) // カラム名は、postgreSQL の全て小文字に合わせる
	private String departmentId;
	
	
	@NotEmpty
	@Column(name = "departmentname", length = 20, nullable = false, unique = true) // カラム名は、postgreSQL の全て小文字に合わせる
	private String departmentName;
	 // ここに unique　を付けても、同じ名前で登録できてしまうので、バリデーションをつけて、ユニークにする
	
	// コンストラクタ
	public Department() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	

	// コンストラクタ
	public Department(String departmentId, @NotEmpty String departmentName) {
		super();
		this.departmentId = departmentId;
		this.departmentName = departmentName;
	}




	// アクセッサ
	public String getDepartmentId() {
		return departmentId;
	}


	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}


	public String getDepartmentName() {
		return departmentName;
	}


	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	

}
