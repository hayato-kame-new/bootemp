package com.kame.springboot.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name="department") // postgresだと、全て小文字なので小文字にする
public class Department {
	
	@Id
	@Column(name = "departmentid") // カラム名は、postgreSQL 全て小文字なので小文字にする
	private String departmentId;  // 新規の時には、nullで渡っていくので、バリデーションを@NotEmpty を付けない  リレーションのカラム
	
	// departmentテーブルの departmentnameカラムには 一意をつける
	// ALTER TABLE department ADD CONSTRAINT constraint_name UNIQUE (departmentName);
	// @UniqueDepName   //  自作したアノテーション 部署名はユニークでなければいけない これも使えます。 こっちを使わない時には、例外処理を使っています。
	@NotEmpty  // String型にはこれを使う。@NotNullだと、String型には効かない unique = true つけて、データベースのテーブルのカラムにも一意制約つける
	@Column(name = "departmentname", length = 20, nullable = false, unique = true) // カラム名は、postgreSQL の全て小文字に合わせる unique = true をつけて、テーブル定義でも、最初にUNIQUEをつけておくこと
	private String departmentName;
	
	// リレーション 相互参照なEntityクラス化する   部署側(親)が社員クラスのリストを持っています
	// @JoinColumn(name = "departmentid") をつければ、中間テーブルは作られなくなる。
	// 参照管理テーブル(中間テーブル)がない場合、employeesの中身は従業員テーブルから自動的に作られます。
	// こっちは、親テーブル側です。必要なら、子テーブル側にcascadeを書きますが、  cascade必要なければ付けない 今回は、子テーブルのemployeeのデータを全て消しても、Departmentは残したいので、削除については子テーブル側にはcascadeはつけません
//	@JoinColumn(name = "departmentid")  //中間テーブルを作らないようにするためのもの@JoinColumn
//	@OneToMany  
//	List<Employee> employees;  // OneToMany だと、フィールド名を複数形にする アクセッサの ゲッター セッターも追加する
	
	
	
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "headerEntity")
	@OneToMany(mappedBy = "departmentId", cascade = CascadeType.ALL)
	List<Employee> employees;
	
//	 public void addEmployee(Employee employee) {
//		 employees.add(employee);
//		 employee.setDepartment(this);
//	    }
//	    public void removeEmployee(Employee employee) {
//	    	employees.remove(employee);
//	    	employee.setDepartment(null);
//	    }

	    
	    
	
	// 引数なしコンストラクタ
	public Department() {
		super();
	}
	
	// 引数ありコンストラクタ
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
	// リレーションのゲッター
	public List<Employee> getEmployees() {
		return employees;
	}
	// リレーションのセッター
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
}
