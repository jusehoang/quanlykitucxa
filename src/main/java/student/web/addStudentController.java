package student.web;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
   
import javassist.expr.NewArray;
import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;
import student.Student;
import student.data.StudentRepository;

@Controller
@RequestMapping("/details")
public class addStudentController{
	private StudentRepository studentRepo;

	@Autowired
	public addStudentController(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}

	@GetMapping
	public String getDetailsForm(Model model,HttpServletRequest request){
		String id = request.getParameter("sv_id");
		Optional<Student> stu = studentRepo.findById(id);
		Student student = stu.get();
		model.addAttribute("student", student);
		return "details";
	}
	
	@PostMapping
	public String saveDetails(@Valid Student student , Errors errors ) {
		if(errors.hasErrors()) {
			return "details";
		}
		else {
			studentRepo.save(student);
			return "redirect:/all";
		}
	}
	@GetMapping("/delete")
	public String deleteStudent(Model model ,HttpServletRequest request) {
		String id = request.getParameter("sv_id");
		Optional<Student> stu = studentRepo.findById(id);
		Student student = stu.get();
		studentRepo.delete(student);
		return "redirect:/all";
	}
}
