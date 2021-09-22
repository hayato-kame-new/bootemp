package com.kame.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.model.Department;
import com.kame.springboot.service.DepartmentService;

@Controller
public class DepartmentController {
	
	// フィールドとして組み込む @Autowired でインスタンスを生成してくれる（コンストラクタを呼んでnewすることを自動で行う)
	// このサービスのフィールドに、DepartmentRepositoryインスタンスBeanが組み込まれてるので、このコントローラに、わざわざリポジトリのフィールドはいらない
	@Autowired
	DepartmentService departmentService;  // DAOのインスタンスを使わずに、サービスのインスタンスをBeanにして使う

	// 部署一覧表示画面
	@RequestMapping(value = "/department", method = RequestMethod.GET)
	public ModelAndView department(ModelAndView mav) {
		mav.setViewName("department");
		mav.addObject("title", "Department Page");
		mav.addObject("msg", "部署データ一覧です");
		// DAOからじゃなくて、サービスから呼び出す
		List<Department> departmentList = departmentService.findAllDepartmentData();
		mav.addObject("departmentList", departmentList);
		return mav;
	}
	
	// 画面を表示する depNewEditリクエストハンドラ
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.GET)
	public ModelAndView depNewEdit(@RequestParam String action, @ModelAttribute("formModel")Department department, ModelAndView mav) {
		// @ModelAttribute("formModel")Department department では、新しいインスタンスを生成してます。各フィールドの値は、各データ型の規定値になってる。
		if(action.equals("depAdd")) {
			System.out.println("部署新規作成です。");
			// "formModel"という変数を用意して、空のインスタンスの入ったdepartmentインスタンスを送る。
			// ただし、各フィールドには、各データ型の規定値が入ってます。
			// 新規では、action だけが、GETだから、クエリー文字列で送られてきている。actionも、次へ送る
		} else if (action.equals("depEdit")) {
			System.out.println("部署の名前を編集します。部署IDは編集できない。");
			// 編集のときには、"formModel"という変数には、フォームからのデータが入っているので、フォームからのデータが入ったインスタンスが用意されてる
			// それをそのまま送って表示する
			
		}
		mav.setViewName("departmentAddEdit");
		mav.addObject("action", action);
		return mav;
	}
	
	
	// 登録や編集をする depAddUpdateリクエストハンドラ
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.POST)
	public ModelAndView depAddUpdate(@RequestParam String action, @ModelAttribute("formModel")Department department, ModelAndView mav) {
		switch(action) {
		case "depAdd": 
			System.out.println("部署データを新規作成します。");
			// 新規では、部署名は、フォームから取得するが、部署IDは hiddenタグから送られてきて null が入ってる
			// 文字列の部署IDは、自分で作成する必要がある。
			String resultGeneratedId = departmentService.generatedId();
			// null　から上書きする
			department.setDepartmentId(resultGeneratedId);
			
			departmentService.saveAndFlush(department);
			
			break; // switch文を抜ける			
		case "depEdit": 
			System.out.println("部署データを編集します。");
			
			break; // switch文を抜ける
		}
		// 部署一覧へリダイレクトする
		return new ModelAndView("redirect:/department");
	}
	
}
