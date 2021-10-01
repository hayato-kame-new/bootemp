package com.kame.springboot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	  // ファイルのアップロード時のパターンチェック
    private final java.util.regex.Pattern PATTERN_IMAGE = java.util.regex.Pattern.compile("^image\\/(jpeg|jpg|png)$");


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
		mav.addObject("selectedPref" , employee.getPref());
		
		List<Department> depList = departmentService.findAllOrderByDepId();
		// depList  を引数にして呼び出す 表示のためにMapを取得する
		
		Map<String, String> depMap = viewBean.getDepartmentMap(depList); // 取れてる {D01=総務部, D02=営業部, D03=開発部, D06=営業部９９９, D07=A部, D08=あいう, D09=新しい部署}
		mav.addObject("depMap", depMap);
	// 	String depId = employee.getDepartment().getDepartmentId();
	//	mav.addObject("selectedDep", depId);
		
		mav.addObject("title", action);

		switch (action) {
		case "add":
			// 新規だと、空のEmployeeインスタンスが用意されている、各フィールドには、各データ型の規定値が入ってるので このままbreak; で
			// switch文を抜ける
			break;
		case "edit":
			// 編集だと、employeeIdの値が hiddenで送られてくる
			Employee findEmployee = employeeService.getEmp(employeeId);
			// @ModelAttribute("formModel") Employee employee
			// のところは空の(フィールドが規定値の)Employeeインスタンスだから キー "formModel" 値を findEmployee でセットする
			mav.addObject("formModel", findEmployee); // 更新の時の画面表示に この１行必要
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
	// 列挙型 DateTimeFormat.ISO　でパターンが列挙されてるので選んで使うこと  @DateTimeFormat(pattern = "yyyy-MM-dd") にすると、バリデーション後表示する際に表示がおかしいので使わない
	@RequestMapping(value = "emp_add_edit", method = RequestMethod.POST)
	public ModelAndView empAddUpdate(@RequestParam(name = "action") String action,
			@RequestParam(name = "employeeId", required = false) String employeeId,
		    @RequestParam(name = "upload_file", required = false) MultipartFile multipartFile,
			@ModelAttribute("formModel") @Validated Employee employee, BindingResult result, ModelAndView mav) {

		ModelAndView resMav = null;
		// バリデーションOKなら結果ページへ送る バリデーションエラーの時は、入力画面へ送る
		String title = "成功";
		String msg = "データベースへ登録に成功しました。";
		
		// result の errors の 
		
		List<ObjectError> list =  result.getAllErrors();
//		for(ObjectError error : list) {
//			error.
//		}
		
		
		boolean part = multipartFile.isEmpty(); // アップロードしてこないと true 空のファイルでも trueとなり、空ファイルの判定もできる
		long size = multipartFile.getSize(); // 3633674 ファイルアップロードしない時には、 0 と入ってきてる
		String mime = multipartFile.getContentType(); // contentTypeを取得します。 "image/jpeg" など入ってる アップロードをしてこない時は
														// application/octet-stream となる

		// アノテーションをつけずに(つけられないから)、エラ〜メッセージを追加して表示したい
		// もし、新規の時に、ファイルをアップロードしない時には、エラーメッセージに追加し、エラーメッセージをフォームのところで表示。
		// 編集時には、アップロードをしても、しなくても良い
		// FieldErrorオブジェクトを生成して、ResultインスタンスのインスタンスメソッドaddErrorで エラーメッセージに追加
		if (action.equals("add") && multipartFile.isEmpty()) {  // アノテーションをつけずに、エラ〜メッセージを追加して表示したい
			FieldError fieldError = new FieldError(result.getObjectName(), "photoId", "画像ファイルを選択してください"); //result.getObjectName()  で "formModel" が取れる
			result.addError(fieldError);
		}
		// ファイルのアップロードがあり、かつ、パターンチェックに合ってない時は、エラーメッセージに追加する
		if(!multipartFile.isEmpty() &&  !PATTERN_IMAGE.matcher(mime).matches()) {
			FieldError fieldError = new FieldError(result.getObjectName(), "photoId", "画像の形式はJPEGまたはJPGおよびPNGにしてください");
			result.addError(fieldError);
		}
		
		if ( multipartFile.isEmpty()) {  // アノテーションをつけずに、エラ〜メッセージを追加して表示したい
			// エラーメッセージに追加する
			FieldError fieldError = new FieldError(result.getObjectName(), "photoId", "画像ファイルを選択してください2");
			result.addError(fieldError);
		}

		// hasErrorsメソッドで囲う処理を
		if (!result.hasErrors()) { // バリデーションエラーが発生しないので、処理できる

			// アップロードされたファイルは「org.springframework.web.multipart.MultipartFile」で受け取ります。

			// アップロードをしてきたのか、isEmpty()で判断できる
			// file.isEmpty()メソッド。値がnullの場合trueとなるが、ファイルサイズが0の場合もtrueとなるため、空ファイルの判定もできる。
			// 新規の時には、写真のアップロードが必須なので、0 だったら、だめ、とするようにしないといけない
//			boolean part = multipartFile.isEmpty(); // アップロードしてこないと true 空のファイルでも trueとなり、空ファイルの判定もできる
//			long size = multipartFile.getSize(); // 3633674 ファイルアップロードしない時には、 0 と入ってきてる
//			String mime = multipartFile.getContentType(); // contentTypeを取得します。 "image/jpeg" など入ってる アップロードをしてこない時は
//															// application/octet-stream となる

			InputStream is = null;
			byte[] photoData = null;
			try {
				is = multipartFile.getInputStream();
				photoData = photoService.readAll_fromService(is); // 取得した画像ストリームを、byte配列に格納して返すメソッド
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// データベースの成功したかどうか
			boolean success = true;

			switch (action) {
			case "add":
				// 新規では、ファイルのアップロードをしてくる必須にする???
				 // 
				success = photoService.photoDataAdd(photoData, mime); // photoテーブルを新規に登録する 成功すればtrue 失敗するとfalse が返る
				if (!success) { // falseが返ったら、失敗
					msg = "写真データの新規登録に失敗しました。"; // 結果ページへの出力のため
					title = "失敗"; // 結果ページへの出力のため
					break; // case句を抜ける
				} else { // tureが返ったら、成功
					// 一番最後のphotoIdを取得して、それをemployeeインスタンスののphotoIdの値に更新する
					int lastPhotoId = photoService.getLastPhotoId(); // 戻り値データベースに登録されてる一番最後のphotoId(int型)が返る
																		// まだ、一つも作られていない場合 1 が返る
					// まず、新規登録用に、社員IDを生成します。
					String generatedEmpId = employeeService.generateEmpId(); // 社員IDを生成
					employee.setEmployeeId(generatedEmpId); // フォームから送られてきた時点ではemployeeIdの値は 規定値(String型の初期値)の null
															// になってるので、生成したIDで上書きする

					employee.setPhotoId(lastPhotoId); // フォームから送られてきた時点ではphotoIdの値は 規定値(int型の初期値)の 0
														// になってるので、さっきphotoテーブルに新規登録した際に、自動生成されたphotoIdを取得してきたので、それで上書きする
					
					// ここ直してください！！！ フォームでは、リレーションじゃなくて、普通のからむにデータ尾を送ってるから
					
					// String getDepartmentId = employee.getDepartment().getDepartmentId(); // リレーションの値を取得してきて、
					
					// これいらない 逆にリレーションにセットしないと。
					// employee.setDepartmentId(getDepartmentId); // フォームから送られてきた時点ではdepartmentIdの値は 規定値(String型の初期値)の null
																// になってるので、リレーションの値を取得してきて、その値で更新する
					Department department = departmentService.getByDepartmentId(employee.getDepartmentId());
					employee.setDepartment(department);  // リレーションに設定しました。これで、リレーションのdepartment.deprtmentId や department.departmentName　にデータをセットできてるはず
					
					
					// データベースに登録する 引数のemployeeは、フォームから送られたデータが入ってて、さらに、employeeId photoId
					// departmentId を上書きしている(更新している)インスタンスになってる
					success = employeeService.empAdd(employee);
					if (!success) { // 失敗
						msg = "社員データの新規登録に失敗しました。"; // 結果ページへの出力のため
						title = "失敗"; // 結果ページへの出力のため
						break; // case句を抜ける
					}
					// ここまできたら成功
				}
				break;
			case "edit":
				// 編集では、ファイルのアップロードは無いかもしれない。

				break;
			}

			mav.setViewName("result");
			mav.addObject("msg", msg);
			mav.addObject("title", title);
			mav.addObject("action", action);
			resMav = mav;

		} else { // バリデーションエラー発生したので、
			title = "入力エラー";
			msg = "入力エラーが発生しました。";

			mav.setViewName("employeeAddEdit");
			// 表示用
			List<String> prefList = viewBean.getPrefList();
			mav.addObject("prefList", prefList);
			mav.addObject("selectedPref" , employee.getPref());
			// 表示用
			List<Department> depList = departmentService.findAllOrderByDepId();
			// depList  を引数にして呼び出す 表示のためにMapを取得する
			Map<String, String> depMap = viewBean.getDepartmentMap(depList); // 取れてる {D01=総務部, D02=営業部, D03=開発部, D06=営業部９９９, D07=A部, D08=あいう, D09=新しい部署}
			mav.addObject("depMap", depMap);
		    Employee target = (Employee) result.getTarget();
			// String depId = target.getDepartment().getDepartmentId();  // 前のフォームで選択をしたもの！！
		    String depId = target.getDepartmentId();
			mav.addObject("selectedDep", depId); // 前のフォームで選択をしたもの！！ 選択したままにする
			
			
			mav.addObject("msg", msg);
			mav.addObject("title", title);
			mav.addObject("action", action);
			resMav = mav;
		}

		return resMav;
	}

}
