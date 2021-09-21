package com.kame.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kame.springboot.model.Department;
import com.kame.springboot.repositories.DepartmentRepository;

@Service
public class DepartmentService {
	
		// フィールドとして、リポジトリを組み込む
	@Autowired
	DepartmentRepository departmentRepository; // コンポーネント Beanとしてインスタンス生成される

	/**
	   * レコードを全件取得する。 リポジトリのメソッド自動生成について p248
	   * @return
	   */
	@SuppressWarnings("unchecked")
	public List<Department> findAllDepartmentData() {
		// findAll()メソッドは、JpaRepositoryに辞書によってメソッドの自動生成機能がある
		return departmentRepository.findAll();  
	}
	
}
