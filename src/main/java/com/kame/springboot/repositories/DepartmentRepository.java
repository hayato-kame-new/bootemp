package com.kame.springboot.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.model.Department;


// サービスクラスを作って、そのフィールドに、このリポジトリのBeanインスタンスをフィールドとして組み込む
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

	
	 <Deprtment extends Department> Department saveAndFlush(Department department);

	public Optional<Department> findById(String departmentId);

	// PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
	public List<Department> findByDepartmentIdIsNotNullOrderByDepartmentIdAsc();
	
}
