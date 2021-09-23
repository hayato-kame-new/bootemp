package com.kame.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.model.Employee;
import com.kame.springboot.service.EmployeeService;

@Controller  // コンポーネントです
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	@RequestMapping(value = "/employee", method = RequestMethod.GET)
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName("employee");
		mav.addObject("title", "Emploee Page");
		mav.addObject("msg", "従業員一覧です");
		List<Employee> employeeList = employeeService.findAllOrderByEmpId();
		mav.addObject("employeeList", employeeList);
		return mav;
	}
	
	// 画面を表示するための
	@RequestMapping(value = "emp_add_edit", method = RequestMethod.GET)
	public ModelAndView empAddEdit(
			@RequestParam(name = "action") String action, 
			@RequestParam(name = "employeeId", required = false)String employeeId,
			@ModelAttribute("formModel") Employee employee,
			ModelAndView mav) {
		
		mav.setViewName("employeeAddEdit");
		mav.addObject("action", action);
		switch(action) {
		case "add":
			// 新規だと、空のEmployeeインスタンスが用意されている、各フィールドには、各データ型の規定値が入ってる
			// このまま
			break; // switch文を抜ける
		case "edit":
			// 編集だと、employeeIdの値が hiddenで送られきます。employeeIdの値で、検索してエンティティを取得します。
			// しかし、id　じゃなくて、 employeeId　なので、リポジトリの findByIdの 自動生成するメソッドは、使えません。findById　は、ちなみに引数には、エンティティインスタンスでも良い。
			// EntityManager と Query を使ったJPQLのメソッドを サービスクラスに作ったので、それを使う。
			Employee findEmployee = employeeService.getByEmployeeId(employeeId);
			//これを、フォームにバインドする@ModelAttribute("formModel") Employee employee の  
			// そもそも フォームからの値が入っていた訳ですが、employeeIdしか、hiddenタグで送られていませんでした。 
			mav.addObject("formModel", findEmployee); // この１行がとても重要。
			break;
		}
		
		return mav;
	}

}
