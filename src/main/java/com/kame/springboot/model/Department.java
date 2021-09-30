package com.kame.springboot.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.kame.springboot.annotation.UniqueDepName;

@Entity
@Table(name="department") // postgresだと、全て小文字なので合わせる
public class Department {
	
	@Id
	@Column(name = "departmentid") // カラム名は、postgreSQL の全て小文字に合わせる
	private String departmentId;  // 新規の時には、nullで渡っていくので、バリデーションを@NotEmpty　を付けてはいけない
	
	
	/**
	 * 後で、ユニークにするためのカスタムバリデーションを作ること
	 */
	
	@NotEmpty  // String型にはこれを使う。@NotNull　だと、String型には効かない unique = true 機能しません　一意のアノテーションもありません カスタムバリデーションを作る必要があります。
	@Column(name = "departmentname", length = 20, nullable = false, unique = true) // カラム名は、postgreSQL の全て小文字に合わせる
	@UniqueDepName   //  自作したアノテーション 部署名はユニークでなければいけない
	private String departmentName;
	
	// 相互参照なEntityクラス化する  リレーション
	// 相互参照なので、ER図にはないですが部署側が社員クラスのリストを持っています。
	// テーブルに専用のカラムもありません。 mappedBy親がわにつけるらしい これによって、参照管理テーブルが作られなくなります。
	// 参照管理テーブルがない場合、employeesの中身は従業員テーブルから自動的に作られます。
	// mappedBy は親がわにつける
//	 mappedByを使うには、テーブル設計で、子テーブル側に外部制約と をつける必要があります。また、
//	 必要なら、cascadeも子テーブル側に書く  cascade必要なければ付けない
	// @OneToManyで用意されているmappedByオプションを使います。これを使うと、３つのテーブルで管理されていた、参照用の中間テーブルが作られなくなり、管理されます。
	 // mappedByに指定する値は「対応する(＠ManyToOneがついた)フィールド変数名」になります。mappedBy = "department"
	 
	// @OneToMany(mappedBy = "department")  // これだと、カラム見つからないと言われた。
	// @OneToMany( cascade = CascadeType.MERGE) // 更新だけ  cascadeする？？で良いのかな
	@OneToMany 
	@JoinColumn(name = "departmentid")  //中間テーブルを作らないようにするためのもの@JoinColumn　とmappedBy = "department"は併用できない
	List<Employee> employees;  // OneToMany だと、フィールド名を複数形にしてください アクセッサの ゲッター セッターも追加忘れずに
	
	


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
	public List<Employee> getEmployees() {
		return employees;
	}
	
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	
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
