package com.kame.springboot.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.model.Department;
import com.kame.springboot.repositories.DepartmentRepository;


@Transactional   // クラスに対して記述した設定はメソッドで記述された設定で上書きされる
@Service
public class DepartmentService { //リレーションの 主テーブルの方は、普通にリポジトリの自動生成が使える
	
		// フィールドとして、DepartmentRepositoryインタフェースを実装した内部クラスのインスタンスを リポジトリとして 組み込む
	@Autowired
	DepartmentRepository departmentRepository; // コンポーネント Beanとしてインスタンス生成される

	// リポジトリには、限界がある リポジトリのメソッド自動生成できないものは、idを使ったものです。今回は、departmentIdですので。
	// DAOに書かずに、サービスに定義します。
	// リポジトリの、メソッド自動生成でできないような複雑なデータベースアクセスをするので、　EntityManager と Query　を使う。
	@PersistenceContext  // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。 その他の場所(コントローラーなど）には書けません。１箇所だけ
	private EntityManager entityManager;
	
	/**
	 *  このメソッドだと、更新をした後に、更新したデータが一番後ろになってしまうため、使わない。
	 *  PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
	   * レコードを全件取得する。 リポジトリのメソッド自動生成について p248
	   * @return List<Department>
	   */
	@SuppressWarnings("unchecked")
	public List<Department> findAllDepartmentData() { // 簡単なものはリポジトリの メソッド自動生成機能で
		// findAll()メソッドは、JpaRepositoryに辞書によってメソッドの自動生成機能がある リポジトリのメソッド自動生成
		return departmentRepository.findAll();  
	}
	
	/**
	 *  リポジトリの自動生成機能で作る、findAll()メソッドだと、更新をした後に、更新したデータが一番後ろになってしまうため、使わない。
	 *  PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
	   * レコードを全件取得する。 リポジトリのメソッド自動生成について p248
	   * @return List<Department>
	   */
	@SuppressWarnings("unchecked")
	public List<Department> findAllOrderByDepId() { // 簡単なものはリポジトリの メソッド自動生成機能で
		// findAll()メソッドは、JpaRepositoryに辞書によってメソッドの自動生成機能がある リポジトリのメソッド自動生成
		return departmentRepository.findByDepartmentIdIsNotNullOrderByDepartmentIdAsc();  
	}
	
	// 複雑なものは、DAOを定義して利用しますが、DAOの代わりにサービスに定義して利用する 
	// リポジトリのメソッド自動生成機能は 一切使ってないので、リポジトリは関係ない
	/**
	   * 部署IDを生成する
	   * @return departmentGeneratedId
	   */
	@Transactional(readOnly=false)
	public String generatedId() {
		
		String departmentGeneratedId = null;
		
		CriteriaBuilder builder = 
				entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Department> query = 
				builder.createQuery(Department.class);
		
		Root<Department> root = query.from(Department.class);
		
		query.select(root).orderBy(builder.desc(root.get("departmentId")));
		
		List<Department> list = (List<Department>)entityManager
				.createQuery(query)
				.setFirstResult(0)
				.setMaxResults(1)
				.getResultList();
		
		if (list.size() == 0) {
			departmentGeneratedId = "D01";
		} else {
			Department lastGetDepartment = list.get(0);
			String lastID = lastGetDepartment.getDepartmentId();
			// 切り取る 数値変換 
			int n = Integer.parseInt(lastID.substring(1, 3)) + 1 ;
			String result = String.format("D%02d", n);
			departmentGeneratedId = result;
		}
		return departmentGeneratedId;
	}
	
	// ここで例外処理をしてはいけない ネストされている @Transaction だから、呼び出しもとで処理する
	// // UnexpectedRollbackException トランザクションをコミットしようとした結果、予期しないロールバックが発生した場合にスローされます。
    // 実行時例外もしたい rollbackFor=Exception.classによって、 UnexpectedRollbackException　は、発生しなくなった
//	@Transactional(readOnly=false , rollbackFor=Exception.class ) // rollbackFor=Exception.class  全ての例外が発生した場合、ロールバックさせる  
//	public Department saveAndFlushDepartmentData(Department department) { // 簡単なものはリポジトリの メソッド自動生成機能で
//		Department savedDepartment = null;
//		try {
//			 savedDepartment = departmentRepository.saveAndFlush(department);  // DataIntegrityViolationExceptionが発生します。
//		} catch (DataIntegrityViolationException | UnexpectedRollbackException e) {  // // 実行時例外もしたい rollbackFor=Exception.classによって、 UnexpectedRollbackException　は、発生しなくなった
//			System.out.println("DataIntegrityViolationExceptionをここでキャッチできた");  
//			return null; // 例外発生したら、ここで、キャッチをして、すぐにreturn  呼び出しもとにnullを返します。
//		}
//
//		return savedDepartment;  // エラーなければ、保存したエンティティオブジェクトを返す
//	}
	
	
	/**
	 * ロールバックの注意点として、非検査例外(RuntimeException及びそのサブクラス)が発生した場合はロールバックされるが、検査例外(Exception及びそのサブクラスでRuntimeExceptionのサブクラスじゃないもの)が発生した場合はロールバックされずコミットされる
	 * RuntimeException以外の例外が発生した場合もロールバックしたいので @Transactional(rollbackFor = Exception.class)としてExceptionおよびExceptionを継承しているクラスがthrowされるとロールバックされるように設定します。
	 * 呼び出し元つまりコントローラ のメソッドでtry-catchする ここで例外処理をしてはいけない コントローラには @Transactional をつけないこと
	 * @Transactional(readOnly=false, rollbackFor=Exception.class) をつけること // throws宣言が必要  呼び出しもとへ投げる
	 * throws DataIntegrityViolationException　が必要　
	 * @param department
	 * @return
	 * @throws DataIntegrityViolationException
	 */
	@Transactional(readOnly=false, rollbackFor=Exception.class) // サービスクラスにつける
	public Department saveAndFlushDepartmentData(Department department) throws DataIntegrityViolationException { // throws宣言が必要
			// saveAndFlushは、DataIntegrityViolationException例外を発生させる可能性があるので、ここでは、キャッチしないで、例外を呼び出しもとへ、スロー投げます。
			Department savedDepartment = departmentRepository.saveAndFlush(department);  // DataIntegrityViolationExceptionが発生します。

			return savedDepartment;  // エラーなければ、保存したエンティティオブジェクトを返す
	}
	
	/**
	 * 実際使ってないメソッド id じゃなくて、 departmentId　だから、メソッド自動生成機能は使えないので
	 * @return Optional<Department>
	 */
//	public Optional<Department> findByIdDepartmentData(String departmentId){
//		return departmentRepository.findById(departmentId);
//	}
	
	/**
	 * 実際使ってないメソッド id じゃなくて、 departmentId　だから、メソッド自動生成機能は使えないので
	 * @return void
	 */
//	public void deleteByIdDepartmentData(String departmentId) {
//		departmentRepository.deleteById(departmentId);// 注意 戻り値はないので、 return 付けません
//	}

	/**
	 * 実際使ってないメソッド id じゃなくて、 departmentId だから、メソッド自動生成機能は使えないので
	 * @return void
	 */
//	public void deleteByEntityObject(Department department) {
//				departmentRepository.deleteById(department);// 注意 戻り値はないので、 return 付けません。
//	}
	
	/**
	 * EntityManagerとQueryを使う。JPQLクエリー文 createQueryメソッド使う
	 * エンティティに@Column(name = departmentid)  departmentid小文字で書く必要がある カラム名を全て小文字の設定にしておく
	 * ロールバックの注意点として、非検査例外(RuntimeException及びそのサブクラス)が発生した場合はロールバックされるが、検査例外(Exception及びそのサブクラスでRuntimeExceptionのサブクラスじゃないもの)が発生した場合はロールバックされずコミットされる
	 * RuntimeException以外の例外が発生した場合もロールバックしたいので @Transactional(rollbackFor = Exception.class)としてExceptionおよびExceptionを継承しているクラスがthrowされるとロールバックされるように設定します。
	 * 呼び出し元つまりコントローラ のメソッドでtry-catchする ここで例外処理をしてはいけない コントローラには @Transactional をつけないこと
	 * @Transactional(readOnly=false, rollbackFor=Exception.class) をつけること
	 * throws PersistenceException が必要　
	 * 
	 * @param departmentId
	 * @return
	 * @throws 
	 */
	@Transactional(readOnly=false, rollbackFor=Exception.class) // ここに@Transactionalをつけて、コントローラにはつけないでください
	public boolean delete(String departmentId) throws PersistenceException {  // throwsして、呼び出しもとで
		Query query = entityManager.createQuery("delete from Department where departmentid = :departmentId");
		query.setParameter("departmentId", departmentId);
		int result = query.executeUpdate(); // 成功したデータ数が返る 戻り値 int型
		if(result != 1) { // 失敗
			return false;
		} 
		return true;
	}
	
	/**
	 * 今回は、 id でなく、departmentId なので、リポジトリの自動生成機能で作るfindByIdを使えないため、
	 * リポジトリの、メソッド自動生成は使えないデータベースアクセスをするので、リポジトリとは関係なく、EntityManager と Queryを使う。JPQLクエリー文を使う
	 * JPQLクエリーの where departmentid のところ、departmentid  という風に全て小文字にすること エンティティで設定した@Column(name = "departmentid")  全て小文字のカラム名
	 * @param departmentId
	 * @return department
	 */
	public Department getByDepartmentId(String departmentId) {
		Query query = entityManager.createQuery("from Department where departmentid = :departmentId"); // カラム名は、
		query.setParameter("departmentId", departmentId);
		Department department = (Department) query.getSingleResult(); // getSingleResult() インスタンスメソッドの戻り値は java.lang.Object 一つの型のない結果を返す
		return department;		
	}

	/**
	 * 部署の名前から部署インスタンスが要素になるリストを取得する
	 * @param depName
	 * @return list
	 */
	public List<Department> findByDepName(String departmentName) {
		List<Department> depList = new ArrayList<Department>();  // new して確保すること
		Query query = entityManager.createNativeQuery("select * from department where departmentname = ?");  // createNativeQuery なので JPQLクエリーではなく 普通のSQL文
		query.setParameter(1, departmentName);  // 一意のカラムだから、存在しても、一つの結果しか返らないが、リストで受け取る
		@SuppressWarnings("unchecked")
		List<Department> list = (List<Department>)query.getResultList();  // SELECTクエリーを実行し、問合せ結果を型のないリストとして返します キャスト必要
		return list; // もし、存在してないなら、空のリストを返します。
	}

	
}
