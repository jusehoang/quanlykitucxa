package student.web;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javassist.expr.NewArray;
import lombok.extern.slf4j.Slf4j;
import student.Room;
import student.Roombill;
import student.Student;
import student.guest;
import student.data.BillRoomRepository;
import student.data.GuestRepository;
import student.data.RoomRepository;
import student.data.StudentRepository;

@Slf4j
@Controller
@RequestMapping("/all")
public class studentController {

	private final StudentRepository studentRepo;
	private final RoomRepository roomRepo;
	private final GuestRepository guestRepo;
	private final BillRoomRepository BillRepo;

	public studentController(StudentRepository studentRepo, RoomRepository roomRepo, GuestRepository guestRepo , BillRoomRepository BillRepo) {
		this.studentRepo = studentRepo;
		this.roomRepo = roomRepo;
		this.guestRepo = guestRepo;
		this.BillRepo = BillRepo;
	}

	@GetMapping
	public String showAllForm(Model model, HttpSession session) {
		List<Student> list = (List<Student>) studentRepo.findAll();
		session.setAttribute("dem", 0);
		model.addAttribute("students", list);
		return "all";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String showCreateForm(Model model) {
		Student student = new Student();
		model.addAttribute("student", student);
		return "add";
	}

	@PostMapping
	public String createSuccess(@Valid Student student, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult);
			return "add";
		}

		else {
			log.info("Order submitted: " + student);
			studentRepo.save(student);
			return "redirect:/all";
		}
	}

	@GetMapping("/addGuest")
	public String getGuestForm(Model model) {
		model.addAttribute("guest", new guest());
		return "addGuest";
	}

	@GetMapping("/allGuest")
	public String getAllGuestForm(Model model) {
		List<guest> list = (List<guest>) guestRepo.findAll();
		model.addAttribute("guests", list);
		return "guestManager";
	}

	@PostMapping("/allGuest")
	public String addGuest(@Valid guest Guest, BindingResult bindingResult, Model model, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			return "addGuest";
		}

		String studentString = request.getParameter("Student_ID");
		Optional<Student> stu = studentRepo.findById(studentString);
		if(stu.isEmpty()){
			model.addAttribute("wrong", "Wrong student id");
			return "addGuest";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DD-mm");

		ArrayList<guest> list = (ArrayList<guest>) guestRepo.findAll();
		ArrayList<guest> list1 = new ArrayList<>();
		int check = 0 ;
		for(guest g : list){
			if(g.getCMT().equalsIgnoreCase(Guest.getCMT())){
				list1.add(g);
			}
		}
		for(guest g : list1){
			if(sdf.format(g.getDate()).equalsIgnoreCase(sdf.format(new Date()))){
				check = 1;
				break;
			}
		}
		if(check == 1){
			model.addAttribute("wrong", "this guest is in day");
			return "addGuest";
		}
		Student student = stu.get();
		Guest.setDate(new Date());
		student.getGuests().add(Guest);
		Guest.setStudentID(student.getSv_id());
		guestRepo.save(Guest);
		studentRepo.save(student);
		return "redirect:/all/allGuest";
	}

	@GetMapping("/viewGuest")
	public String getAllGuest(Model model , HttpServletRequest request){
		Optional<Student> stu = studentRepo.findById(request.getParameter("sv_id"));
		Student student = stu.get();
		ArrayList<guest> list = (ArrayList<guest>) guestRepo.findAll();
		ArrayList<guest> list1 = new ArrayList<>();
		for(guest g : list){
			if(g.getStudentID().equalsIgnoreCase(request.getParameter("sv_id"))){
				list1.add(g);
			}
		}
		model.addAttribute("list", list1);
		return "allGuest";
	}
}
