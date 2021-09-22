package com.kame.springboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.model.Department;


// サービスクラスを作って、そのフィールドに、このリポジトリのBeanインスタンスをフィールドとして組み込む
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

	
	 <Deprtment extends Department> Department saveAndFlush(Department department);

	
	
}
