package com.kame.springboot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.model.Employee;
import com.kame.springboot.service.DepartmentService;
import com.kame.springboot.service.EmployeeService;

@Controller
public class FindController {
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	DepartmentService departmentService;
	
	@Autowired
	ViewBean viewBean;
	
	// 検索画面を表示する
		@RequestMapping(value = "/find", method = RequestMethod.GET)
		public ModelAndView find(
				ModelAndView mav) {
			// 部署セレクトタグのドロップボタン表示用
			Map<String, String> depMap = viewBean.getDepartmentMap(); // 取れてる {D01=総務部, D02=営業部, D03=開発部, D06=営業部９９９, D07=A部, D08=あいう, D09=新しい部署}
			mav.addObject("depMap", depMap);
			mav.setViewName("find");
			mav.addObject("title", "search");		
			return mav;
		}
		
		// 検索をする 結果を /employee で表示するために、リダイレクトする 
		// Flash Scope に、検索結果のリストを保存して /employeeへ送ります。
	//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
		@RequestMapping(value = "/find", method = RequestMethod.POST)
		public String search(
				@RequestParam(name = "departmentId", required = false)String departmentId,
				@RequestParam(name = "employeeId", required = false)String employeeId,
				@RequestParam(name = "word", required = false)String word,

				RedirectAttributes redirectAttributes
				) {
			
			// フラッシュメッセージをFlash Scopeへ保存して リダイレクトする
			String flashMsg = "検索結果です";
			List<Employee> employeeList = new ArrayList<Employee>(); // new で確保
			// 検索結果はリストが返る、一つもデータが検索されない場合空のリストが返る ArrrayaListは、内部は、配列なので、[]が返る
			employeeList = employeeService.find(departmentId, employeeId, word);
			if(employeeList.size() == 0) { // 空のリストが返ってきたら 検索結果が0
				flashMsg = "検索結果はありませんでした";
			}
			//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
			redirectAttributes.addFlashAttribute("employeeList", employeeList);
			redirectAttributes.addFlashAttribute("flashMsg", flashMsg);
			redirectAttributes.addFlashAttribute("action", "find");
			return "redirect:/employee";
			
		}

}
