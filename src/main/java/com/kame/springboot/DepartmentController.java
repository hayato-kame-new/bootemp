package com.kame.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.model.Department;
import com.kame.springboot.service.DepartmentService;

@Controller
public class DepartmentController {
	
	// フィールドとして組み込む @Autowired でインスタンスを生成してくれる（コンストラクタを呼んでnewすることを自動で行う)
	@Autowired
	DepartmentService departmentService;  // DAOのインスタンスを使わずに、サービスのインスタンスをBeanにして使う

	@RequestMapping(value = "/department", method = RequestMethod.GET)
	public ModelAndView department(ModelAndView mav) {
		mav.setViewName("department");
		mav.addObject("title", "Department Page");
		mav.addObject("msg", "部署データ一覧です");
		List<Department> departmentList = departmentService.findAllDepartmentData();
		mav.addObject("departmentList", departmentList);
		return mav;
	}
	
}
