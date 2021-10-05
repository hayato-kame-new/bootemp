package com.kame.springboot.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "photo") // PostgreSQL のテーブル名やカラム名が全て小文字になるので全て小文字のカラム名に
public class Photo {
	
	@Id  // プライマリーキー
	@GeneratedValue(strategy = GenerationType.AUTO)  // 自動採番(オートインクリメント)設定 
	@Column(name = "photoid")  // 全て小文字に
	@NotNull  // int型には @NotNullが使える
	private int photoId;
	
	@Column(name = "photodata")  // 全て小文字
	private byte[] photoData;  // nullでも構わないので、バリデーションつけない
	
	@Column(name = "mime")  // 全て小文字
	private String mime;  // contentTypeのこと "image/jpeg"  "image/png"など 　MIMEタイプ "タイプ/サブタイプ" nullでも構わない
	
	// Photo側が、親エンティティ Photo側は 社員を持っています。1対1のリレーション
	// 必要なら、cascadeも子テーブル側に書く
	// @JoinColumn(name = "photoid")  をつけることによって参照管理テーブル(3つめの中間テーブル）が作られなくなります
	// 参照管理テーブルがない場合、employeeの中身は従業員テーブルから自動的に作られます。	
	 @OneToOne( cascade = CascadeType.ALL) // 1対1 写真は、カスケードつける
	 @JoinColumn(name = "photoid")  // 参照管理テーブル(3つめの中間テーブル）が作られなくなります
	 Employee employee;  // OneToOne  なので、フィールド名は単数形にしてください。
	
	/**
	 * 引数なしのコンストラクタ
	 */
	public Photo() {
		super();
	}

	// アクセッサ	
	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}
	
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public byte[] getPhotoData() {
		return photoData;
	}

	public void setPhotoData(byte[] photoData) {
		this.photoData = photoData;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}	
}
