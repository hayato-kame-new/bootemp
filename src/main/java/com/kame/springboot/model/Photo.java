package com.kame.springboot.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "photo") // 全て小文字にしてくださいPostgreSQL のテーブル名やカラム名が全て小文字になるので、合わせる必要がある。
public class Photo {
	
	@Id  // プライマリーキー
	@GeneratedValue(strategy = GenerationType.AUTO)  // 自動採番してくれる設定 オートインクリメントのこと
	@Column(name = "photoid")  // 全て小文字にしてください
	@NotNull  // intには、使える
	private int photoId;  // フィールド名が、キャメル記法で、大文字が入ってるので、@Column(name = "photoid")　の設定が必要です。
	
	@Column(name = "photodata")  // 全て小文字にしてください
	private byte[] photoData;  // nullでも構わない　null許可
	
	
	@Column(name = "mime")  // 全て小文字にしてください
	private String mine;  // contentTypeのことです   "image/jpeg"   "image/png"など  "タイプ/サブタイプ"  MIMEタイプは常にタイプとサブタイプの両方を持ち、一方だけで使われることはありません。
	// nullでも構わないように null許可
	
	// Photo側は 社員を持っています。Photo側が、親エンティティ
	// photoテーブルには、employeeのカラムはありません。mappedBy 親がわにつける mappedBy = "photo",
	 @OneToOne( cascade = CascadeType.ALL) // 1対1 写真は、カスケードつける
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


	public String getMine() {
		return mine;
	}


	public void setMine(String mine) {
		this.mine = mine;
	}
	
	
	
}
