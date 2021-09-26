package com.kame.springboot.service;

import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.component.LogicBean;
import com.kame.springboot.repositories.PhotoRepository;

@Service
@Transactional
public class PhotoService {
	
	@Autowired
	PhotoRepository photorepository;
	
	// リポジトリには、限界がある リポジトリのメソッド自動生成できないものは、idを使ったものです。今回は、departmentIdですので。
		// DAOに書かずに、サービスに定義します。@PersistenceContextは一つしかつけれない もしもコントローラに付けたら、コントローラの方を削除しないといけない
		// リポジトリの、メソッド自動生成でできないような複雑なデータベースアクセスをするので、　EntityManager と Query　を使う。
		@PersistenceContext  // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。 その他の場所には書けません。１箇所だけ
		private EntityManager entityManager;
	
	@Autowired
	LogicBean logicBean;
	
	/**
	 * データを全て読み込んでbyte配列に格納して返す インスタンスメソッド
	 * 
	 * このサービスで、定義したメソッドは、中身でロジッククラスのインスタンスメソッドを呼び出している
	 * コントローラクラスでは、このサービククラスのインスタンスメソッドとして呼び出して使う。
	 * @param is
	 * @return byte[]
	 * @throws IOException
	 */
	 public byte[] readAll_fromService(InputStream is) throws IOException {
		 return logicBean.readAll(is);
	 }
	 
	 /**
	  * photoテーブルを新規に登録する 成功したらtrue 失敗するとfalseを返す
	  * リポジトリは関係ない EntityManage と Query を使ってJPQLクエリー文で作る JPQLクエリー文 カラム名を小文字にすること photodata
	  * @param photoData
	  * @param mime
	  * @return true:成功<br />false:失敗
	  */
	 public boolean photoDataAdd(byte[] photoData, String mime) {		 
		 // createNativeQuery を使う時には、PostgreSqlだと、 テーブル名も小文字です。createNativeQuery は、JPQLを使わないで普通のSQL文になる
		 Query query = entityManager.createNativeQuery("insert into photo ( photodata , mime )" +  " values ( :a, :b)");
		 
		 query.setParameter("a", photoData);
		 query.setParameter("b", mime);
		// executeUpdate() で実行する

		 int result = query.executeUpdate(); // 戻り値は、データの更新や、削除に成功したエンティティの数です
		if (result != 1) { // 失敗
			return false;  // 0が入ってる photoIdを返します、失敗すると0が返る		
		}		
		// 成功したら、
		return true;
	 }
	 
	 // 失敗したら、０を返す エンティティにリレーションを付けたら、エラーになった。CriteriaBuilderだと、エンティティにリレーションつけるとエラー？
//	 public int getLastPhotoIdFromCriteria() {		 
//		  int getId = 0;
//		 CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//		 
//		 CriteriaQuery<Photo> query = builder.createQuery(Photo.class);
//		 
//		 Root<Photo> root = query.from(Photo.class);
//		 
//		 query.select(root);
//		 
//		 query.orderBy(builder.desc(root.get("photoId")));		 
//		 // ここでエラー
//		 List<Photo> list = (List<Photo>)entityManager
//				 .createQuery(query)
//				 .setFirstResult(0)
//				 .setMaxResults(1)
//				 .getResultList();
//		 
//		 if (list.size() == 0) {
//			 return getId;  // 0が入ってる
//		 } else {			 
//			 Photo photo = list.get(0);
//			 getId = photo.getPhotoId();
//			 return getId;
//		 }
//	 }
	 
	 /**
	  * リレーションを付けた後に作った。こっちは、エラーにならない。こっちを使う。
	  * @return
	  */
	 public int getLastPhotoId() {
		 //  createNativeQueryの引数は、JPQLクエリーじゃない 普通のSQL文です PostgreSQL は、カラム名を全て小文字にしてください テーブル名も全て小文字です。photoid にすること
		 Query query = entityManager.createNativeQuery("select photoid from photo order by photoid desc limit 1");
		 int getPhotoId = (int)query.getSingleResult();
		 return getPhotoId;  // 最後のphotoidの値を取得して返す
	 }

}
