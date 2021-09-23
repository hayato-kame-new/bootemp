package com.kame.springboot.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.model.Employee;


@Repository // コンポーネントです
public interface EmployeeRepository extends JpaRepository<Employee, String> {

	// リポジトリの メソッド自動生成機能findByIdは、id じゃないと使えないので、注意 今回は employeeId になってます。
	
	// インタフェースには、抽象メソッドの宣言
	// PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
		public List<Employee> findByEmployeeIdIsNotNullOrderByEmployeeIdAsc();
	
}
