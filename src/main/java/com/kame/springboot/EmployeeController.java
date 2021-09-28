package com.kame.springboot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
// ファイルのアップロードに必要  org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.model.Department;
import com.kame.springboot.model.Employee;
import com.kame.springboot.service.DepartmentService;
import com.kame.springboot.service.EmployeeService;
import com.kame.springboot.service.PhotoService;

@Controller // コンポーネントです
public class EmployeeController { // コントローラでは、サービスクラスを利用する

	// フィールドには、サービスを置いて サービスの中から、いろんなものを呼び出す。
	@Autowired
	EmployeeService employeeService;

	@Autowired
	PhotoService photoService;

	@Autowired
	DepartmentService departmentService;

	// コントローラでは、 ビューのコンポーネントをインスタンスとしてもつ
	@Autowired
	ViewBean viewBean;

	/**
	 * 社員一覧表示
	 * 
	 * @param mav
	 * @return mav
	 */
	@SuppressWarnings("uncheckd")
	@RequestMapping(value = "/employee", method = RequestMethod.GET)
	public ModelAndView index(Model model, ModelAndView mav) {
		mav.setViewName("employee");
		mav.addObject("title", "index");
		// Flash Scopeから値の取り出し Model model を引数に書いて、 modelインスタンスのgetAttribute(キー）で値を
		// Flash Scope から取り出す
		// String flashMsg = (String) model.getAttribute("flashMsg"); //
		// 返り値がObject型なので、キャストすること
		// mav.addObject("flashMsg", flashMsg);
		List<Employee> employeeList = employeeService.getEmpListOrderByAsc();
		mav.addObject("employeeList", employeeList);
		return mav;
	}

	/**
	 * 新規登録・編集 画面表示
	 * 
	 * @param action
	 * @param employeeId
	 * @param employee
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "emp_add_edit", method = RequestMethod.GET)
	public ModelAndView empDisplay(@RequestParam(name = "action") String action,
			@RequestParam(name = "employeeId", required = false) String employeeId,
			@ModelAttribute("formModel") Employee employee, ModelAndView mav) {

		mav.setViewName("employeeAddEdit");
		mav.addObject("action", action);
		List<String> prefList = viewBean.getPrefList();
		mav.addObject("prefList", prefList);
		List<Department> depList = departmentService.findAllOrderByDepId();
		mav.addObject("depList", depList);
		mav.addObject("title", action);

		switch (action) {
		case "add":
			// 新規だと、空のEmployeeインスタンスが用意されている、各フィールドには、各データ型の規定値が入ってる
			// このまま
			break; // switch文を抜ける
		case "edit":
			// 編集だと、employeeIdの値が hiddenで送られきます。employeeIdの値で、検索してエンティティを取得します。
			// しかし、id じゃなくて、 employeeId なので、リポジトリの findByIdの 自動生成するメソッドは、使えません。findById
			// は、ちなみに引数には、エンティティインスタンスでも良い。
			// EntityManager と Query を使ったJPQLのメソッドも使えない

			Employee findEmployee = employeeService.getEmp(employeeId);
			// これを、フォームにバインドする@ModelAttribute("formModel") Employee employee の
			// そもそも フォームからの値が入っていた訳ですが、employeeIdしか、hiddenタグで送られていませんでした。
			mav.addObject("formModel", findEmployee); // この１行がとても重要。
			break;
		}
		return mav;
	}

	// 新規 編集する
	// Spring
	// Bootでは、デフォルトでファイルサイズの上限が1MB(1024*1024=1048576bytes)となっています。アップロードしたファイルのサイズがこれより大きい場合、MaxUploadSizeExceededExceptionがスローされ、リクエストは処理されません。
	// propertiesファイルの場合 以下の２行を加えてください。
	// spring.servlet.multipart.max-file-size=30MB
	// spring.servlet.multipart.max-request-size=30MB
	@RequestMapping(value = "emp_add_edit", method = RequestMethod.POST)
	public ModelAndView empAddUpdate(@RequestParam(name = "action") String action,
			@RequestParam(name = "employeeId", required = false) String employeeId,
			@RequestParam(name = "upload_file", required = false) MultipartFile multipartFile,
			@RequestParam(name = "hireDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hireDate,
			@RequestParam(name = "retirementDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date retirementDate,
			@ModelAttribute("formModel") Employee employee, ModelAndView mav) {

		// アップロードされたファイルは「org.springframework.web.multipart.MultipartFile」で受け取ります。

		String mime = multipartFile.getContentType(); // contentTypeを取得します。 "image/jpeg"
		long size = multipartFile.getSize(); // 3633674

		InputStream is = null;
		try {
			is = multipartFile.getInputStream(); // まだクローズしない。下のメソッドで使う
		} catch (IOException e) {
			e.printStackTrace();
		}
		// byte[] photoData = employeeService.readAll_fromService(is); // 例外処理をする
		byte[] photoData = null; // 配列は参照型
		try {
			photoData = photoService.readAll_fromService(is); // 取得した画像ストリームを、byte配列に格納して返すメソッド
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // つけたし
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// フォワード先のパス のローカル変数
		String path = "";
		// 結果ページへ送るためのデフォルト値
		String title = "成功";
		String msg = "データベースへ登録に成功しました。";

		boolean result = true;

		switch (action) {
		case "add":
			result = photoService.photoDataAdd(photoData, mime); // photoテーブルを新規に登録する 成功すればtrue 失敗するとfalse が返る
			if (!result) { // falseが返ったら、失敗
				msg = "写真データの新規登録に失敗しました。"; // 結果ページへの出力のため
				title = "失敗"; // 結果ページへの出力のため
				break; // case句を抜ける
			} else { // tureが返ったら、成功
				// 一番最後のphotoIdを取得して、それをemployeeインスタンスののphotoIdの値に更新する
				int lastPhotoId = photoService.getLastPhotoId(); // 戻り値データベースに登録されてる一番最後のphotoId(int型)
																	// まだ、一つも作られていない場合見つからないとき 1 が返る
				// まず、新規登録用に、社員IDを生成します。
				String generatedEmpId = employeeService.generateEmpId(); // 社員IDを生成
				employee.setEmployeeId(generatedEmpId); // フォームから送られてきた時点ではemployeeIdの値は 規定値(String型の初期値)の null
														// になってるので、生成したIDで上書きする
				employee.setPhotoId(lastPhotoId); // フォームから送られてきた時点ではphotoIdの値は 規定値(int型の初期値)の 0
													// になってるので、さっきphotoテーブルに新規登録した際に、自動生成されたphotoIdを取得してきたので、それで上書きする
				String getDepartmentId = employee.getDepartment().getDepartmentId(); // リレーションの値を取得してきて、
				employee.setDepartmentId(getDepartmentId); // フォームから送られてきた時点ではdepartmentIdの値は 規定値(String型の初期値)の null
															// になってるので、リレーションの値を取得してきて、その値で更新する
				// データベースに登録する 引数のemployeeは、フォームから送られたデータが入ってて、さらに、employeeId photoId
				// departmentId を上書きしている(更新している)インスタンスになってる
				result = employeeService.empAdd(employee);
				if (!result) { // 失敗
					msg = "社員データの新規登録に失敗しました。"; // 結果ページへの出力のため
					title = "失敗"; // 結果ページへの出力のため
					break; // case句を抜ける
				}
				// ここまできたら成功
			}
			break;
		case "edit":
			break;
		}
		
		mav.setViewName("result");
		mav.addObject("msg", msg);
		mav.addObject("title", title);
		return mav;
	}
	

}
