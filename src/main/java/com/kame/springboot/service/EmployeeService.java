package com.kame.springboot.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.logic.LogicBean;
import com.kame.springboot.model.Employee;
import com.kame.springboot.repositories.EmployeeRepository;

@Service
@Transactional
public class EmployeeService {
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	// リポジトリには、限界がある リポジトリのメソッド自動生成できないものは、idを使ったものです。今回は、departmentIdですので。
	// DAOに書かずに、サービスに定義します。@PersistenceContextは一つしかつけれない もしもコントローラに付けたら、コントローラの方を削除しないといけない
	// リポジトリの、メソッド自動生成でできないような複雑なデータベースアクセスをするので、　EntityManager と Query　を使う。
	@PersistenceContext  // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。 その他の場所には書けません。１箇所だけ
	private EntityManager entityManager;
	
	@Autowired
	LogicBean logicBean;
	
	
	/**
	 *  リポジトリの自動生成機能で作る、findAll()メソッドだと、更新をした後に、更新したデータが一番後ろになってしまうため、使わない。
	 *  PostgreSQL だと、order by employeeId を付けないと、順番が、更新されたのが一番最後の順になってします。
	   * レコードを全件取得する。 リポジトリのメソッド自動生成について p248
	   * @return List<Department>
	   */
	@SuppressWarnings("unchecked")
	public List<Employee> findAllOrderByEmpId() { // 簡単なものはリポジトリの メソッド自動生成機能で
		// findAll()メソッドは、JpaRepositoryに辞書によってメソッドの自動生成機能がある リポジトリのメソッド自動生成
		return employeeRepository.findByEmployeeIdIsNotNullOrderByEmployeeIdAsc();  
	}
	
	/**
	 * 今回は、 id でなく、employeeId なので、リポジトリの自動生成機能で作るfindByIdを使えないため、
	 * リポジトリとは関係なく、
	 * リポジトリの、メソッド自動生成でできないような複雑なデータベースアクセスをするので、　EntityManager と Query　を使う。
	 * サービスクラスに定義して利用する。
	 * JPQLクエリーの where employeeid のところ、employeeid  という風に全て小文字にすること
	 * @param employeeId
	 * @return employee
	 */
	public Employee getByEmployeeId(String employeeId) {		
		Query query = entityManager.createQuery("from Employee where employeeid = :employeeId");
		query.setParameter("employeeId", employeeId);
		// Queryインスタンスが持っている getSingleResult() インスタンスメソッドの戻り値は java.lang.Object です。
		// 一つの型のない結果を返します。だから、キャストが必要です。
		Employee employee = (Employee) query.getSingleResult();
		return employee;
	}
	
	//サービスから、ロジックを呼び出して使う ロジックは、サービス同士で共通の処理をまとめるための場所
	// サービスの中で、リポジトリをフィールドとしてBeanインスタンスをメンバとしているように、
	// ロジックのクラスも、まず、Bean化できるように、Beanクラスとして作り、サービスの中で、@Autowiredを使って、インスタンスを自動瀬星できるようにしていく。
	public void logic_test_from_service() {
		logicBean.logic_test(); 
	}
}
