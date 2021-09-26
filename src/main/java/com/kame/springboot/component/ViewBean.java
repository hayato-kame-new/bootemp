package com.kame.springboot.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewBean {  // このクラスは、表示用なので、コントローラのクラスでBeanインスタンスとして使う

	@Autowired
	public ViewBean() {
		super();
	}
	
	// ビューで使用する、都道府県リストを返すインスタンスメソッド コントローラで何回も使う
	// 都道府県リスト
	public List<String> getPrefList() {
		List<String> prefList = new ArrayList<String>(Arrays.asList("東京都", "神奈川県", "埼玉県", "千葉県", "茨城県"));
		return prefList;
	}
	
	

}
