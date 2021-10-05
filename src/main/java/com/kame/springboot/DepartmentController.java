package com.kame.springboot;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.model.Department;
import com.kame.springboot.service.DepartmentService;
// コントローラには @Transactional をつけないこと サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので
@Controller
public class DepartmentController {
	
	// フィールドとして組み込む @Autowired でインスタンスを生成してくれる（コンストラクタを呼んでnewすることを自動で行う)
	// このサービスのフィールドに、DepartmentRepositoryインスタンスBeanが組み込まれてるので、このコントローラに、わざわざリポジトリのフィールドはいらない
	@Autowired
	DepartmentService departmentService;  // DAOのインスタンスを使わずに、サービスのインスタンスをBeanにして使う

	
	/**
	 * 部署一覧表示画面
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "/department", method = RequestMethod.GET)
	public ModelAndView index(
			Model model, // Flash Scopeから値の取り出しに必要
			ModelAndView mav) {
		mav.setViewName("department");
		mav.addObject("title", "index");
		mav.addObject("msg", "部署データ一覧です");
		
		// DAOからじゃなくて、サービスから呼び出す		
	// 	List<Department> departmentList = departmentService.findAllDepartmentData();
		
		// 上のだと、並び順が、部署名の更新をすると、変わってしまうため、下のものを使う 
		// PostgreSQL だと、order by departmentId を付けないと、順番が、更新されたのが一番最後の順になってします。
		// ちなみに、PostgreSQLだと、departmentid と内部では小文字になるようにしないとエラー  @Column(name = "departmentid", length = 20)で カラム名を小文字にしてるので大丈夫です
		String flashMsg = "";
		// リダイレクトしてくる  フラッシュメッセージ Flash Scopeから値の取り出し Model model を引数に書いて、 modelインスタンスのgetAttribute(キー）で値をString flashMsg = "";
		// Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
		if (model.getAttribute("flashMsg") != null){
			flashMsg = (String) model.getAttribute("flashMsg");// 返り値がObject型なので、キャストすること
		}
		mav.addObject("flashMsg", flashMsg);
		List<Department> departmentList = departmentService.findAllOrderByDepId();
		mav.addObject("departmentList", departmentList);
		return mav;
	}
	
	
	/**
	 * 画面を表示する リクエストハンドラ
	 * @param action
	 * @param departmentId
	 * @param departmentName
	 * @param department
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.GET)
	public ModelAndView depDisplay(
			@RequestParam(name = "action") String action, // デフォルトだと、必須パラメータで、nullじゃいけないエラー発生 
			@RequestParam(name = "departmentId", required = false) String departmentId, // hiddenフィールドから required = false とすると、任意パラメータとなり、nullでもよくなる
			@RequestParam(name = "departmentName", required = false) String departmentName, // hiddenフィールドから required = false とすると、任意パラメータとなり、nullでもよくなる
			@ModelAttribute("formModel") Department department,
			ModelAndView mav) {
		
		if(action.equals("depAdd")) {			
			// "formModel"という変数を用意して、空のインスタンスの入ったdepartmentインスタンスを送る。
			// ただし、各フィールドには、各データ型の規定値が入ってます。
			// 新規では、action だけが、GETだから、クエリー文字列で送られてきている。actionも、次へ送る
		} else if (action.equals("depEdit")) {
			// 編集のときには、"formModel"という変数には、フォームからのデータが入っているが、hiddenで送られているので、departmentの各フィールドは、最初に新規で送られたもの同じです
			// 規定値になってるので、hiddenタグから送られたパラメータの値をセットします。	
				
			// この２行でもOK。employeeみたいなメソッドを作って、エンティティを検索してきて、それをaddObject("formModel", インスタンスの入った変数)としてもいい
			// department.setDepartmentId(departmentId); // これでもいい
			// department.setDepartmentName(departmentName); // これでもいい
			
			Department findDepartment = departmentService.getByDepartmentId(departmentId);
			mav.addObject("formModel", findDepartment );  // この１行必要
		}
		
		mav.setViewName("departmentAddEdit");
		mav.addObject("action", action);
		mav.addObject("title", action);
		return mav;
	}
	
	
	/**
	 * 登録や編集をする @Validated が必要
	 * import org.springframework.transaction.annotation.Propagation;
	 * ここには @Transactional つけないこと @Transactional をつけないこと サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので
	 * このリクエストハンドラ内でtry〜catchでエラーを処理するので。
	 * @param action
	 * @param departmentId
	 * @param departmentName
	 * @param department
	 * @param result
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.POST)
	public ModelAndView depAddUpdate(  //   このリクエストハンドラには、@Transactional つけないこと このリクエストハンドラ内で、エラーをキャッチして処理したいから @Transactionalは、サービスクラスのメソッドについています。
			@RequestParam(name = "action") String action,  // 必須パラメータにしてる
			@RequestParam(name = "departmentId", required = false)String departmentId,  // departmentIdは、hiddenフィールド 新規の時は、nullなのでエラーにならないように 任意パラメータにする
			@RequestParam(name = "departmentName", required = false )String departmentName, // 必須パラメータにしてる required = false は、必要です。削除するときに、nullになるから、エラーにならないように 任意パラメータにする
			@ModelAttribute("formModel")@Validated Department department,
			BindingResult result,
			ModelAndView mav) {
		
		
		ModelAndView resMav = null;
		
		if (!result.hasErrors()) {
			// バリデーションエラーが発生しなかったので、処理に進む
			switch(action) {
			case "depAdd": 				
				// 新規では、部署名は、フォームから取得するが、部署IDは hiddenタグから送られてきて null が入ってる
				// 文字列の部署IDは、自分で作成する必要がある。
				String resultGeneratedId = departmentService.generatedId();
				// null　から上書きする
				department.setDepartmentId(resultGeneratedId);
				// データベースに新規保存する。サービスのメソッドを呼び出す
                // departmentNameカラム  にユニーク制約がかかっているので、
				// すでにある部署名を登録しようとすると、ユニーク制約に引っ掛かる、
				// ここのsaveAndFlushDepartmentData から更に呼び出される saveAndFlushで、duplicate key value violates unique constraint "constraint_name"
				//   詳細: Key (departmentname)=(開発部) already exists. と、エラーが出るため、例外をキャッチする必要がある org.springframework.dao.DataIntegrityViolationException
				// ユニークの自作のバリデーションを使わないで、例外処理をして対処する
				try {
					Department savedDepartment = departmentService.saveAndFlushDepartmentData(department); //  saveAndFlushDepartmentDataに @Transactional(readOnly=false , rollbackFor=Exception.class )  Exception.class にすることで、実行時例外もキャッチして、ロールバックできる						
				} catch (DataIntegrityViolationException e) {
					// 自作のアノテーション@UniqueDepNameを使わない時に、エラー処理で対処する。
					mav.setViewName("departmentAddEdit");
					mav.addObject("msg", "部署名はユニークです。同じ名前で登録できません。");
					mav.addObject("formModel", department);
					mav.addObject("action", action);
					resMav = mav;
					return resMav;	// ここですぐにreturnします。	以降の行は実行されません。			
				}
					
				// ここに来たら、無事新規保存できてる
				
				break; // switch文を抜ける			
			case "depEdit": // 編集の時には、必ずdepartmentIdの値が入ってるnullじゃない
				// 変更したインスタンスをデータベースに保存する。サービスのメソッドを呼び出す 
				try {
					Department savedDepartment = departmentService.saveAndFlushDepartmentData(department); //  saveAndFlushDepartmentDataに @Transactional(readOnly=false , rollbackFor=Exception.class )  Exception.class にすることで、実行時例外もキャッチして、ロールバックできる						
				} catch (DataIntegrityViolationException e) {  // 自作のアノテーション@UniqueDepNameを使わない時に、エラー処理で対処する。
					// キャッチ
					mav.setViewName("departmentAddEdit");
					mav.addObject("msg", "部署名はユニークです。同じ名前で登録できません。");
					mav.addObject("formModel", department);
					mav.addObject("action", action);
					resMav = mav;
					return resMav;	// ここですぐにreturnします。	以降の行は実行されません。			
				}
				// ここに来たら、成功してる
				break; // switch文を抜ける
			}
			// 部署一覧へリダイレクトする リダイレクトは、リダイレクト先のリクエストハンドラを実行します
			resMav =  new ModelAndView("redirect:/department");
		} else { // バリデーションエラーが発生した時
			mav.setViewName("departmentAddEdit");
			mav.addObject("msg", "入力エラーが発生しました。");
			mav.addObject("formModel", department);
			mav.addObject("action", action);
			resMav = mav;
		}
		return resMav;  // mavインスタンスを返してます
	}
	
	/**
	 * 削除する
	 * サービスのメソッドを使って、hiddenタグで渡ってきたdepartmentIdを下にして、該当するエンティティを取得する
	 * 
	 * departmentId　なので、 idじゃないので、リポジトリのメソッド自動生成機能は使えない idだったら下のメソッド使える
	 * Optional<Department> optionalObject = departmentService.findByIdDepartmentData(departmentId);
	 * データベースから削除する  サービスのメソッドを呼び出す 戻り値はない
	 * 引数を idにした  こっちを使ってもいいし、
	 * departmentService.deleteByIdDepartmentData(departmentId);
	 *
	 * 今回は、使えないが、リポジトリの機能の自動生成の deleteByIdメソッドの引数は、idの他、オブジェクトを引数にして、削除してもいい
	 * ラッパークラスのOptionalから インスタンスメソッドのget() を呼び出すと、ラップしたインスタンスが取得できます。
	 * departmentService.deleteByEntityObject(optionalObject.get());
	 * 
	 * 
	 * ここでは　@Transactional  つけないでください
	 * @param action
	 * @param departmentId
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "/dep_delete", method = RequestMethod.POST)
	public ModelAndView depDelete(
			@RequestParam(name = "action") String action,  // 必須パラメータにしてる
			@RequestParam(name = "departmentId")String departmentId,  // 必須パラメータにしてる
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {
		
		String flashMsg = "部署を削除しました";
		boolean result = true;
		try {
			result = departmentService.delete(departmentId); // PersistenceException発生する可能性あるメソッドです　　メソッドでthrows宣言してます
		} catch (PersistenceException | ConstraintViolationException e) { // 問題が発生したときに永続化プロバイダーによってスローされます
			flashMsg = "削除しようとした部署には、所属している社員がいるので、削除できませんでした。";
			result = false;
		}
		// Flash Scopeに保存して、リダイレクトする
	//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
		redirectAttributes.addFlashAttribute("flashMsg", flashMsg);
		
		return new ModelAndView("redirect:/department");
	}
}
