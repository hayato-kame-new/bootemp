package com.kame.springboot.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kame.springboot.model.Department;

@Component
public class ViewBean {  // このクラスは、表示用なので、コントローラのクラスでBeanインスタンスとして使う

	// コンストラクタ
	@Autowired
	public ViewBean() {
		super();
	}
	
	// ビューで使用する、都道府県リストを返すインスタンスメソッド コントローラで使う
	// 都道府県リスト
	public List<String> getPrefList() {	
		List<String> prefList = new ArrayList<String>(Arrays.asList("東京都", "神奈川県", "埼玉県", "千葉県", "茨城県"));
		return prefList;
	}
	
	
	public Map<String, String> getDepartmentMap(List<Department> depList) {
		
		 Map<String, String> map = new LinkedHashMap<String, String>();
		 //	ループでputしていく
		for(Department dep : depList) {
			map.put(dep.getDepartmentId(), dep.getDepartmentName()); // D01=総務部  D02=営業部  と ループで putされてる
		}
		 
		
		return map;
	}

	

}
