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
	private String mime;  // contentTypeのことです   "image/jpeg"   "image/png"など  "タイプ/サブタイプ"  MIMEタイプは常にタイプとサブタイプの両方を持ち、一方だけで使われることはありません。
	// nullでも構わないように null許可
	
	// Photo側は 社員を持っています。Photo側が、親エンティティ
//	 mappedByを使うには、テーブル設計で、子テーブル側に外部制約と をつける必要があります。また、
//	 必要なら、cascadeも子テーブル側に書く
	 
	// テーブルに専用のカラムもありません。 mappedBy親がわにつけるらしい これによって、参照管理テーブル(3つめの中間テーブル）が作られなくなります。
		// 参照管理テーブルがない場合、employeesの中身は従業員テーブルから自動的に作られます。
	// mappedByに指定する値は「対応する(＠OneToOneがついた)フィールド変数名」になります。
	// photoテーブルには、employeeのカラムはありません。mappedBy 親がわにつける mappedBy = "photo",
	// 中間テーブルを作らないようにするためには、もう一個方法がある、 myppedByとの併用ができないので、myppedByを消す
	// myppedByだと、どのカラムと、連携するのか、親側から、指定できないため 使わない??
	// @OneToOne( mappedBy = "photo", cascade = CascadeType.ALL)
	 @OneToOne( cascade = CascadeType.ALL) // 1対1 写真は、カスケードつける
	 @JoinColumn(name = "photoid")
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
