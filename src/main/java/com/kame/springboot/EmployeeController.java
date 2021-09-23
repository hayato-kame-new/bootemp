package com.kame.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		mav.addObject("data", employeeList);
		return mav;
	}

}
