package com.kame.springboot.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.model.Department;
import com.kame.springboot.repositories.DepartmentRepository;

@Service
@Transactional
public class DepartmentService {
	
		// フィールドとして、DepartmentRepositoryインタフェースを実装した内部クラスのインスタンスを リポジトリとして 組み込む
	@Autowired
	DepartmentRepository departmentRepository; // コンポーネント Beanとしてインスタンス生成される

	// リポジトリには、限界がある リポジトリのメソッド自動生成できないものは、idを使ったものです。今回は、departmentIdですので。
	// DAOに書かずに、サービスに定義します。
	// リポジトリの、メソッド自動生成でできないような複雑なデータベースアクセスをするので、　EntityManager と Query　を使う。
	@PersistenceContext  // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。 その他の場所には書けません。１箇所だけ
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
	public String generatedId() {
		
		String departmentGeneratedId = null;
		
		CriteriaBuilder builder = 
				entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Department> query = 
				builder.createQuery(Department.class);
		
		Root<Department> root = query.from(Department.class);
		
		query.select(root).orderBy(builder.desc(root.get("departmentId")));
		
		List<Department> list = entityManager
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
	/**
	 * 保存 リポジトリのメソッド自動生成について p248 リポジトリのメソッドを呼び出して結果の戻り値をリターンしてる
	 * @return Department
	 */
	public Department saveAndFlushDepartmentData(Department department) { // 簡単なものはリポジトリの メソッド自動生成機能で
		// findAll()メソッドは、JpaRepositoryに辞書によってメソッドの自動生成機能がある リポジトリのメソッド自動生成
		return departmentRepository.saveAndFlush(department);  
	}

	/**
	 * 実際使ってないメソッド id じゃなくて、 departmentId　だから、メソッド自動生成機能は使えないので
	 * 主キーから、エンティティを取得 リポジトリのメソッド自動生成について p248  リポジトリのメソッドを呼び出して結果の戻り値をリターンしてる
	 * @return Optional<Department>
	 */
//	public Optional<Department> findByIdDepartmentData(String departmentId){
//		return departmentRepository.findById(departmentId);
//	}
	
	/**
	 * 実際使ってないメソッド id じゃなくて、 departmentId　だから、メソッド自動生成機能は使えないので
	 * 主キーから、エンティティを削除 リポジトリのメソッド自動生成について p248  リポジトリのメソッドを呼び出して結果の戻り値をリターンしてる
	 * @return void
	 */
//	public void deleteByIdDepartmentData(String departmentId) {
//		// 注意 戻り値はないので、 return 付けません。
//		departmentRepository.deleteById(departmentId);		
//	}

	/**
	 * 実際使ってないメソッド id じゃなくて、 departmentId　だから、メソッド自動生成機能は使えないので
	 * エンティティを引数にして、エンティティを削除 リポジトリのメソッド自動生成について p248  リポジトリのメソッドを呼び出して結果の戻り値をリターンしてる
	 * @return void
	 */
//	public void deleteByEntityObject(Department department) {
//		// TODO 自動生成されたメソッド・スタブ
//		// 注意 戻り値はないので、 return 付けません。
//				departmentRepository.deleteById(department);
//	}
	
	
	/**
	 * DAOじゃなくてサービスを使って定義する
	 * EntityManager　と Query　を使う。JPQLクエリー
	 * @Column(name = departmentid)  にして置く必要がある カラム名を全て小文字の設定にしておく
	 */
	public boolean deleteJPQL(String departmentId) {
		Query query = entityManager.createQuery("delete from Department where departmentid = :departmentId");
		query.setParameter("departmentId", departmentId);
		int result = query.executeUpdate(); // 成功したデータ数が返る 戻り値 int型
		if(result != 1) { // 失敗
			return false;
		} 
		return true;
	}
	
	


	
}
