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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.model.Department;
import com.kame.springboot.model.Employee;
import com.kame.springboot.service.DepartmentService;
import com.kame.springboot.service.EmployeeService;
import com.kame.springboot.service.PhotoService;

@Controller  // コンポーネントです
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
	 * @param mav
	 * @return mav
	 */
	@SuppressWarnings("uncheckd")
	@RequestMapping(value = "/employee", method = RequestMethod.GET)
	public ModelAndView index(Model model, ModelAndView mav) {
		mav.setViewName("employee");
		mav.addObject("title", "index");		
		// Flash Scopeから値の取り出し  Model model  を引数に書いて、 modelインスタンスのgetAttribute(キー）で値を Flash Scope　から取り出す
		String flashMsg = (String) model.getAttribute("flashMsg");  // 返り値がObject型なので、キャストすること
		mav.addObject("flashMsg", flashMsg);
		List<Employee> employeeList = employeeService.getEmpListOrderByAsc();
		mav.addObject("employeeList", employeeList);		
		return mav;
	}
	
	
	
	
	/**
	 * 新規登録・編集 画面表示
	 * @param action
	 * @param employeeId
	 * @param employee
	 * @param mav
	 * @return mav
	 */
	@RequestMapping(value = "emp_add_edit", method = RequestMethod.GET)
	public ModelAndView empDisplay(
			@RequestParam(name = "action") String action, 
			@RequestParam(name = "employeeId", required = false)String employeeId,
			@ModelAttribute("formModel") Employee employee,
			ModelAndView mav) {
		
		mav.setViewName("employeeAddEdit");
		mav.addObject("action", action);
		List<String> prefList = viewBean.getPrefList();
		mav.addObject("prefList", prefList);
		List<Department> depList = departmentService.findAllOrderByDepId();
		mav.addObject("depList", depList);
		mav.addObject("title" , action);
		
		switch(action) {
		case "add":
			// 新規だと、空のEmployeeインスタンスが用意されている、各フィールドには、各データ型の規定値が入ってる
			// このまま
			break; // switch文を抜ける
		case "edit":
			// 編集だと、employeeIdの値が hiddenで送られきます。employeeIdの値で、検索してエンティティを取得します。
			// しかし、id　じゃなくて、 employeeId　なので、リポジトリの findByIdの 自動生成するメソッドは、使えません。findById　は、ちなみに引数には、エンティティインスタンスでも良い。
			// EntityManager と Query を使ったJPQLのメソッドも使えない
			
			
			Employee findEmployee = employeeService.getEmp(employeeId);
			//これを、フォームにバインドする@ModelAttribute("formModel") Employee employee の  
			// そもそも フォームからの値が入っていた訳ですが、employeeIdしか、hiddenタグで送られていませんでした。 
			 mav.addObject("formModel", findEmployee); // この１行がとても重要。
			break;
		}		
		return mav;
	}
	
	
	// 新規 編集する
	// Spring Bootでは、デフォルトでファイルサイズの上限が1MB(1024*1024=1048576bytes)となっています。アップロードしたファイルのサイズがこれより大きい場合、MaxUploadSizeExceededExceptionがスローされ、リクエストは処理されません。
	// propertiesファイルの場合 以下の２行を加えてください。
	// spring.servlet.multipart.max-file-size=30MB
	// spring.servlet.multipart.max-request-size=30MB
	@RequestMapping(value = "emp_add_edit" , method = RequestMethod.POST)
	public ModelAndView empAddUpdate(
			RedirectAttributes redirectAttributes,
			@RequestParam(name = "action") String action, 
			@RequestParam(name = "employeeId", required = false)String employeeId,
			@RequestParam(name = "upload_file", required = false) MultipartFile multipartFile,
			// @RequestParam(name = "photoId")int photoId, // intで良いのかな いらない
			@RequestParam(name = "hireDate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date hireDate,
			@RequestParam(name = "retirementDate", required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") Date retirementDate,
			@ModelAttribute("formModel") Employee employee,
			ModelAndView mav) {
		
			// アップロードされたファイルは「org.springframework.web.multipart.MultipartFile」で受け取ります。
		
		

// コントローラで@ModelAttributeを使用したため、すべてのパラメータが文字列形式で渡されます。Date型に変換すべきです?
		System.out.println(employee.getHireDate());
			
			String mime = multipartFile.getContentType();  // contentTypeを取得します。 "image/jpeg"
			long size = multipartFile.getSize();  // 3633674
			
			// InputStream is = multipartFile.getInputStream();
			// 例外処理をするように書く  クローズするのかな
			
			InputStream is = null;
			try {
				is = multipartFile.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//  取得した画像ストリームを、byte配列に格納して返すメソッドを使う readAll(引数)インスタンスメソッドをサービスから呼び出す
		// IOException例外のインスタンスを発生する可能性のあるメソッドです。try catchで囲む
			
		//	byte[] photoData = employeeService.readAll_fromService(is);
			byte[] photoData = null;  // 配列は参照型
		try {
			photoData = photoService.readAll_fromService(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// リダイレクト先に表示するフラッシュメッセージ。
		// Flash Scopeにおく、一回のリダイレクトで有効なスコープ、Request Scope より長く、Session Scopeより短いイメージ
		String flashMsg = "";
		
		switch(action) {
		case "add":
			// サービスで定義したメソッドを呼び出す photoテーブルを新規に登録する
			boolean result = photoService.photoDataAdd(photoData, mime);
			if(!result) { // falseが返ったら、失敗
				// 失敗のメッセージを出す
			}
			// 成功したら、一番最後のphotoIdを取得して、それをemployeeインスタンスののphotoIdの値にする
			
			int lastPhotoId = photoService.getLastPhotoId();
			if (lastPhotoId == 0) {
				//失敗 失敗メッセージを
			}
			
			// 取得に成功したら、このlastPhotoIdの値を employeeオブジェクトの、photoIdカラムに入れます。
			// まず、新規登録用に、社員IDを生成します。
			String generatedEmpId = employeeService.generateEmpId();
			// データベースに登録する  引数のemployeeには、フォームから送られたデータが入っているインスタンスになります。
			
			employee.setEmployeeId(generatedEmpId);  // null　を、生成したIDで上書きする
			employee.setPhotoId(lastPhotoId); // 0 からさっき、作られて、最後から取得してきたIDで上書きする
			// ここで、departmentIdをセットする
			String id = employee.getDepartment().getDepartmentId();
			employee.setDepartmentId(id);
			
			// 更新した employee を引数に当てる サービスのメソッドを呼び出す
			 boolean result3 = employeeService.empAdd(employee);  // 戻り値は、保存したエンティティです。
			
			 if(!result3) {// 失敗のメッセージ出す
				 flashMsg = "登録できませんでした。";
			}
			// 成功したら成功のメッセージを
			 flashMsg = "登録しました。";
			break;
		}
		redirectAttributes.addFlashAttribute("flashMsg", flashMsg);
		
		// 社員一覧のページにリダイレクトします。フラッシュメッセージつき
		return new ModelAndView("redirect:/employee");
	}
	
	
	
	

}
