package com.kame.springboot.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.component.LogicBean;
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
		// findAll()メソッドは、JpaRepositoryに辞書によってメソッドの自動生成機能がある リポジトリのメソッド自動生成 idが、employeeIdのため、自動生成使えない？らしい
		// エラー
		return employeeRepository.findByEmployeeIdIsNotNullOrderByEmployeeIdAsc();  
	}
	
	@SuppressWarnings("unchecked")
	public List<Employee> getEmpListOrderByAsc() {
		
		Query query = entityManager.createNativeQuery("select * from employee  order by employeeid asc");
		List<Employee> empList = query.getResultList();
		return empList;
	}
	
	/**
	 * これ リレーションついてるから、
	 * 今回は、 id でなく、employeeId なので、リポジトリの自動生成機能で作るfindByIdを使えないため、
	 * リポジトリとは関係なく、
	 * リポジトリの、メソッド自動生成でできないような複雑なデータベースアクセスをするので、　EntityManager と Query　を使う。
	 * サービスクラスに定義して利用する。
	 * JPQLクエリーの where employeeid のところ、employeeid  という風に全て小文字にすること
	 * @param employeeId
	 * @return employee
	 */
	public Employee getEmp(String employeeId) {		
		Query query = entityManager.createNativeQuery("select * from employee where employeeid = ?");
		query.setParameter(1, employeeId);		
		List<Employee> list =  (List<Employee>) query.getResultList(); // OK
		Iterator itr = list.iterator();
		Employee emp = null;
		List<Employee> resultlist = new ArrayList<Employee>(); // リストは、newして確保しておくこと
		
		while(itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
			String id = String.valueOf(obj[0]); 
			String name = String.valueOf(obj[1]);
			int age = Integer.parseInt(String.valueOf(obj[2]));
			int gender = Integer.parseInt(String.valueOf(obj[3]));
			int photoId = Integer.parseInt(String.valueOf(obj[4]));
			String zipNumber = String.valueOf(obj[5]);
			String pref = String.valueOf(obj[6]);
			String address = String.valueOf(obj[7]);
			String departmentId = String.valueOf(obj[8]); // ここ編集時nullになってしまう。直すこと
			
			java.sql.Date sqlHireDate = (Date) obj[9]; // 取れてる！！  1999-11-11
			
			java.util.Date utilHireDate = new Date( sqlHireDate.getTime()); // 942246000000
			
			// 退職日は、nullかもしれないので、java.sql.Date　でnullだったら、java.util.Date でもnullのまま
			java.util.Date utilRretirementDate = null; 
			
			if (obj[10] != null) {
				java.sql.Date sqlRretirementDate = (Date) obj[10]; // 取れてる！！
				utilRretirementDate = new java.util.Date(sqlRretirementDate.getTime());  // 取れてる
			}
			emp = new Employee();
			emp.setEmployeeId(id);
			emp.setName(name);
			emp.setAge(age);
			emp.setGender(gender);
			emp.setPhotoId(photoId);
			emp.setZipNumber(zipNumber);
			emp.setPref(pref);
			emp.setAddress(address);
			emp.setDepartmentId(departmentId);
			emp.setHireDate(utilHireDate);
			emp.setRetirementDate(utilRretirementDate);

			// 引数ありコンストラクタを使うと
			//  Employee emp = new Employee(id, name, age,gender,photoId,zipNumber,pref,address,departmentId,utilHireDate, utilRretirementDate);
			resultlist.add(emp);
		}
		System.out.println(resultlist.size());
		Employee returnEmp = resultlist.get(0);
		
		return returnEmp;
		
		
		// Queryインスタンスが持っている getSingleResult() インスタンスメソッドの戻り値は java.lang.Object です。
		// 一つの型のない結果を返します。だから、キャストが必要です。
		// List<Employee[]> list = query.getResultList();
		 // Object obj = query.getSingleResult();
		//  Employee[] array = (Employee[]) query.getSingleResult();
		
		//String id = obj[0];
		// Employee employee = new Employee(obj[0],obj[1],obj[2],obj[3],obj[4],obj[5],obj[6],obj[7],obj[8],obj[9],obj[10],obj[11]);
		//query.getResultList() 戻り値 List<Object[]>     List<Object[]> res = query.getResultList();
		
//		Object[] array = list.get(0);
//		System.out.println(array.length);
//		System.out.println(array[0]);
//		System.out.println(array[1]);
//		System.out.println(array[2]);
//		System.out.println(array[3]);
//		System.out.println(array[4]);
		
		// Employee employee = new Employee(array[0],array[1], array[2],array[3],array[4], array[5],array[6],array[7],array[8],array[9],array[10],array[11]);
	}
	
	//サービスから、ロジックを呼び出して使う ロジックは、サービス同士で共通の処理をまとめるための場所
	// サービスの中で、リポジトリをフィールドとしてBeanインスタンスをメンバとしているように、
	// ロジックのクラスも、まず、Bean化できるように、Beanクラスとして作り、サービスの中で、@Autowiredを使って、インスタンスを自動瀬星できるようにしていく。
	public void logic_test_from_service() {
		logicBean.logic_test(); 
	}
	
	// 社員ID生成する リレーションのアノテーションを付けたら、エラーで使えなくなりました。
	// 使いません。
	public String generateEmpIdFromCriteria() {
		
		String generatedEmpId = null;
		
		CriteriaBuilder builder = 
				entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Employee> query = 
				builder.createQuery(Employee.class);
		
		Root<Employee> root = query.from(Employee.class);
		
		query.select(root).orderBy(builder.desc(root.get("employeeId")));
		
		List<Employee> list = (List<Employee>)entityManager.createQuery(query)
				.setFirstResult(0)
				.setMaxResults(1)
				.getResultList();
		// 社員IDが 辞書順に並び替えて、最後にあるものを取得した。それがリストに１つ入ってるはず、リストに要素が一つも無かったら、まだ、全く登録されていないので
		if (list.size() == 0) {
			 // まだ、ひとつも、employeeデータが登録されてなかったら、
			generatedEmpId = "EMP0001"; // 一番最初になります。
		 } else {	
			Employee employee = list.get(0);
			String getLastId = employee.getEmployeeId(); // 最後に登録されているId
			// 文字列切り取りして、数値に変換して、 +1 する それをまた、文字列にフォーマットで変換する
			generatedEmpId = String.format("EMP%04d", Integer.parseInt(getLastId.substring(3)) + 1);
		}
		return generatedEmpId;
	}
	
	// 手動で、社員IDを生成する リレーションをつけると、こっちでしかできない
	// エンティティのリレーションを付けたので、こっちを使う、上のCriteria APIだとリレーションつけたら、エラーになるようになった。
	public String generateEmpId() {
		// まず、最後尾の社員IDをとってくる order by 辞書順で並べ替えて、desc をして limit 1 
		// employeeid カラムは、全てを小文字にすること postgreSQLだから テーブル名 カラム名 全て小文字
		Query query = entityManager.createNativeQuery("select employeeid from employee order by employeeid desc limit 1");
		// 戻り値は、型のないObjectになるので、キャストすること
		String lastStringEmpId = (String)query.getSingleResult();
		int plusOne = Integer.parseInt(lastStringEmpId.substring(3)) + 1;		
		String getGeneratedEmpId = String.format("EMP%04d", plusOne);		
		return getGeneratedEmpId;		
	}
	
	// これだと、リレーションのアノテーションをつけた時にエラーとなるので 使わない
	// 社員登録する employee には、フォームからのデータがセットされてる。そして、さらに、
	// 新しく登録した photoId　と、 自分で手動で、生成したemployeeIdの 値もセットして、更新してある。employeeインスタンスです。
//	public Employee employeeAdd(Employee employee) {
//		return employeeRepository.saveAndFlush(employee);
//	}
	
	// こっちを使う
	public boolean empAdd(Employee employee) {
		Query query = entityManager.createNativeQuery("insert into employee (employeeid, name, age, gender, photoid, zipnumber, pref, address, departmentid, hiredate, retirementdate) values (?,?,?,?,?,?,?,?,?,?,?)");
		query.setParameter(1, employee.getEmployeeId());
		query.setParameter(2, employee.getName());
		query.setParameter(3, employee.getAge());
		query.setParameter(4, employee.getGender());
		query.setParameter(5, employee.getPhotoId());
		query.setParameter(6, employee.getZipNumber());
		query.setParameter(7, employee.getPref());
		query.setParameter(8, employee.getAddress());
		query.setParameter(9, employee.getDepartmentId());
		query.setParameter(10, new java.sql.Date(employee.getHireDate().getTime()), TemporalType.DATE);
		query.setParameter(11, new java.sql.Date(employee.getHireDate().getTime()), TemporalType.DATE);
		
		System.out.println(employee.getDepartmentId());
		
		int result = query.executeUpdate(); // 成功したデータ数が返る
		if(result != 1) {
			//失敗
			return false;
		}
		return true;
	}
}
