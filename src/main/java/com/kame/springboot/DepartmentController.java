package com.kame.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
	// 	List<Department> departmentList = departmentService.findAllDepartmentData();
		
		// 上のだと、並び順が、部署名の更新をすると、変わってしまうため、下のものを使う 
		// PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
		// ちなみに、PostgreSQLだと、departmentid と内部では小文字になるようにしないとエラー  @Column(name = "departmentid", length = 20)で カラム名を小文字にしてるので大丈夫です
		List<Department> departmentList = departmentService.findAllOrderByDepId();
		mav.addObject("departmentList", departmentList);
		return mav;
	}
	
	// 画面を表示する depNewEditリクエストハンドラ
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.GET)
	public ModelAndView depNewEdit(
			@RequestParam(name = "action") String action, // デフォルトだと、必須パラメータで、nullじゃいけないエラー発生 
			@RequestParam(name = "departmentId", required = false) String departmentId, // hiddenフィールドから required = false とすると、任意パラメータとなり、nullでもよくなる
			@RequestParam(name = "departmentName", required = false) String departmentName, // hiddenフィールドから required = false とすると、任意パラメータとなり、nullでもよくなる
			@ModelAttribute("formModel")Department department, 
			ModelAndView mav) {
		// @ModelAttribute("formModel")Department department では、新しいインスタンスを生成してます。各フィールドの値は、各データ型の規定値になってる。
		
		if(action.equals("depAdd")) {
			
			// "formModel"という変数を用意して、空のインスタンスの入ったdepartmentインスタンスを送る。
			// ただし、各フィールドには、各データ型の規定値が入ってます。
			// 新規では、action だけが、GETだから、クエリー文字列で送られてきている。actionも、次へ送る
		} else if (action.equals("depEdit")) {
			
			// 編集のときには、"formModel"という変数には、フォームからのデータが入っているが、hiddenで送られているので、departmentの各フィールドは、最初に新規で送られたもの同じです
			// 規定値になってるので、hiddenタグから送られたパラメータの値をセットします。
			
			department.setDepartmentId(departmentId);
			department.setDepartmentName(departmentName);
			}
		mav.setViewName("departmentAddEdit");
		mav.addObject("action", action);
		return mav;
	}
	
	
	// 登録や編集をする depAddUpdateリクエストハンドラ
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.POST)
	@Transactional(readOnly=false)
	public ModelAndView depAddUpdate(
			@RequestParam(name = "action") String action,  // 必須パラメータにしてる
			@RequestParam(name = "departmentId", required = false)String departmentId,  // departmentIdは、hiddenフィールド 新規の時は、nullなのでエラーにならないように 任意パラメータにする
			@RequestParam(name = "departmentName", required = false )String departmentName, // 必須パラメータにしてる required = false は、必要です。削除するときに、nullになるから、エラーにならないように 任意パラメータにする
			@ModelAttribute("formModel"
					)Department department, ModelAndView mav) {
		switch(action) {
		case "depAdd": 
			
			// 新規では、部署名は、フォームから取得するが、部署IDは hiddenタグから送られてきて null が入ってる
			// 文字列の部署IDは、自分で作成する必要がある。
			String resultGeneratedId = departmentService.generatedId();
			// null　から上書きする
			department.setDepartmentId(resultGeneratedId);
			// データベースに新規保存する。サービスのメソッドを呼び出す
			departmentService.saveAndFlushDepartmentData(department);			
			break; // switch文を抜ける			
		case "depEdit": 
			// 編集の時には、必ずdepartmentIdの値が入ってるnullじゃない
			
			// 変更したインスタンスをデータベースに保存する。サービスのメソッドを呼び出す
			departmentService.saveAndFlushDepartmentData(department);
			break; // switch文を抜ける
		case "delete":
			// 削除には、プライマリーキーの値だけ必要 departmentId がhiddenで送られてきてる
			
			// サービスのメソッドを使って、hiddenタグで渡ってきたdepartmentIdを下にして、該当するエンティティを取得する
			//  Optional<Department> optionalObject = departmentService.findByIdDepartmentData(departmentId);
			 // データベースから削除する  サービスのメソッドを呼び出す 戻り値はない
			 // 引数を idにした  こっちを使ってもいいし、
			//  departmentService.deleteByIdDepartmentData(departmentId);
			 
			 // リポジトリの機能の自動生成の deleteByIdメソッドの引数は、idの他、オブジェクトを引数にして、削除してもいい
			 // ラッパークラスのOptionalから インスタンスメソッドのget() を呼び出すと、ラップしたインスタンスが取得できます。
			//  departmentService.deleteByEntityObject(optionalObject.get());
			boolean result = departmentService.deleteJPQL(departmentId);
			// 削除失敗したら、メッセージ
			
			break;
		}
		// 部署一覧へリダイレクトする リダイレクトは、リダイレクト先のリクエストハンドラを実行します
		return new ModelAndView("redirect:/department");
	}
	
}
