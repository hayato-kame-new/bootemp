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
// コントローラには @Transactional をつけないこと サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので ここのリクエストハンドラでtry-catchできなくなるので、つけないこと
@Controller
public class DepartmentController {
	
	// フィールド  @Autowiredをつけると自動でインスタンスを生成してくれる
	@Autowired
	DepartmentService departmentService;
	
	/**
	 * 部署一覧を表示する.
	 * @param model
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "/department", method = RequestMethod.GET)
	public ModelAndView index(
			Model model, // Flash Scopeから値の取り出しに必要
			ModelAndView mav) {
		mav.setViewName("department");
		mav.addObject("title", "index");
		mav.addObject("msg", "部署データ一覧です");
		// リダイレクトしてくる  フラッシュメッセージ Flash Scopeから値の取り出す
		String flashMsg = "";
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
	 * フォームの画面を表示する.
	 * @param action
	 * @param department
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.GET)
	public ModelAndView depDisplay(
			@RequestParam(name = "action") String action, // 必須パラメータ(デフォルト)にしてる 渡ってこないとエラーになる リダイレクトしてくる時にも "action" は送られてくる必須
			@ModelAttribute("formModel") Department department,
			ModelAndView mav) {
		
		if(action.equals("depAdd")) { 
			// 新規の時には、@ModelAttributeによってdepartmentが用意されるが、 空のインスタンス(各フィールドには、各データ型の規定値が入ってる)
		} else if (action.equals("depEdit")) { // 編集
			// 編集の時には、@ModelAttributeによってdepartmentには、フォームから送られた値がセットしてある
		}		
		mav.setViewName("departmentAddEdit");
		mav.addObject("formModel", department );
		mav.addObject("action", action);
		mav.addObject("title", action);
		return mav;
	}
	
	/**
	 * 部署を新規登録や編集をする. @Validated が必要.
	 * このリクエストハンドラ及びこのコントローラに @Transactional つけないこと. 
	 * @Transactional をつけないこと 理由 サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるのでエラー発生する.
	 * このリクエストハンドラ内でtry〜catchでエラーを処理するので.
	 * @param action
	 * @param department
	 * @param result
	 * @param redirectAttributes
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "/dep_add_edit", method = RequestMethod.POST)
	public ModelAndView depAddUpdate(  //   このリクエストハンドラ及びコントローラには、@Transactional つけないこと このリクエストハンドラ内で、エラーをキャッチして処理したいから @Transactionalは、サービスクラスのメソッドについています.
			@RequestParam(name = "action") String action,  // 必須パラメータにしてる
			@ModelAttribute("formModel")@Validated Department department,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			ModelAndView mav) {
				
		ModelAndView resMav = null;
		
		if (!result.hasErrors()) {
			// バリデーションエラーが発生しなかったので、処理に進む

			// データベースの成功したかどうか
			boolean success = true;
			// Flash scopeへ保存して リダイレクトする Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
			String flashMsg = "部署を新規登録作成しました";
			
			switch(action) {
			case "depAdd": 				
				// 新規登録をするために、 プライマリーキーで文字列の部署IDは、自分で作成する
				String resultGeneratedId = departmentService.generatedId();
				// 新規登録のときは @ModelAttributeによって規定値(各フィールドのデータ型のデフォルト値)が入ってるdepartmentインスタンスが用意されているので 
				// セッターを使って、null(参照型のデフォルト値)を departmentIdフィールドの値を 上書きします
				department.setDepartmentId(resultGeneratedId);

				try {
					success = departmentService.create(department);
				} catch (DataIntegrityViolationException | ConstraintViolationException | PersistenceException e) {
					// 自作のアノテーション@UniqueDepNameを使わない時に、エラー処理する
					mav.setViewName("departmentAddEdit");
					mav.addObject("msg", "部署名はユニークです。同じ名前で登録できません。");
					mav.addObject("formModel", department);
					mav.addObject("action", action);
					resMav = mav;
					return resMav;	// ここですぐにreturn	以降の行は実行されない		
				} 
				if(success == false) {  // 失敗
					flashMsg = "新規部署は作成できませんでした";
				}				
				break; // switch文を抜ける			
			case "depEdit": // 編集の時には、必ずdepartmentIdの値が入ってるnullじゃない
				// 変更したインスタンスをデータベースに保存する。サービスのメソッドを呼び出す
				
				
				
				try {
					//  savedDepartment = departmentService.saveAndFlushDepartmentData(department); //  saveAndFlushDepartmentDataに @Transactional(readOnly=false , rollbackFor=Exception.class )  Exception.class にすることで、実行時例外もキャッチして、ロールバックできる						
					success = departmentService.update(department);
				} catch (DataIntegrityViolationException | ConstraintViolationException | PersistenceException e) {  // 自作のアノテーション@UniqueDepNameを使わない時に、エラー処理で対処する。
					// キャッチ
					mav.setViewName("departmentAddEdit");
					mav.addObject("msg", "部署名はユニークです。同じ名前で登録できません。");
					mav.addObject("formModel", department);
					mav.addObject("action", action);
					resMav = mav;
					return resMav;	// ここですぐにreturnします。	以降の行は実行されません。			
				}
				if(success == false) {  // 失敗
					flashMsg = "部署データを更新できませんでした";
				}
				// 成功
				flashMsg = "部署データを更新しました";
				break; // switch文を抜ける
			}
			//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
			redirectAttributes.addFlashAttribute("flashMsg", flashMsg);
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
			result = departmentService.delete(departmentId); // PersistenceException発生する可能性あるメソッドです メソッドでthrows宣言してます
			if(result == false) {
				flashMsg = "部署を削除できませんでした";
			}
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
