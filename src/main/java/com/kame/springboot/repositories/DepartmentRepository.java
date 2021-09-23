package com.kame.springboot.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.model.Department;


// サービスクラスを作って、そのフィールドに、このリポジトリのBeanインスタンスをフィールドとして組み込む
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

	// インタフェースなので、抽象メソッドを宣言するだけです。
	 <Deprtment extends Department> Department saveAndFlush(Department department);

	public Optional<Department> findById(String departmentId);

	// PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
	public List<Department> findByDepartmentIdIsNotNullOrderByDepartmentIdAsc();

	/**
	 * 実際は使ってないメソッドです プライマリーキーが idじゃなくて departmentIdだから、リポジトリのメソッド自動生成機能のdeleteById は使えない
	 * リポジトリのメソッド自動生成機能のdeleteById は、引数が、プライマリーキーでもいいし、エンティティインスタンスでもいい
	 *  
	 */
	//  void deleteById(String departmentId);  // 引数 id
	
	/**
	 * 実際は使ってないメソッドです プライマリーキーが idじゃなくて departmentIdだから、リポジトリのメソッド自動生成機能のdeleteById は使えない
	 * リポジトリのメソッド自動生成機能のdeleteById は、引数が、プライマリーキーでもいいし、エンティティインスタンスでもいい
	 *  抽象メソッドの多重定義 シグネチャが違えば、(引数の型と数が違えば) 同じメソッド名で、内容は違うが似たような処理を多重定義ができる
	 */
	//  void deleteById(Department department); // 引数 エンティティオブジェクト
}
