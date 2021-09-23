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
	private String departmentId;  // 新規の時には、nullで渡っていくので、バリデーションを@NotEmpty　を付けてはいけない
	
	
	@NotEmpty  // String型にはこれを使う。@NotNull　だと、String型には効かない unique = true 機能しません　一意のアノテーションもありません カスタムバリデーションを作る必要があります。
	@Column(name = "departmentname", length = 20, nullable = false, unique = true) // カラム名は、postgreSQL の全て小文字に合わせる
	private String departmentName;
	
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
