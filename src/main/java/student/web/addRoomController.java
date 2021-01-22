package student.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import student.Room;
import student.Roombill;
import student.Student;
import student.data.BillRoomRepository;
import student.data.RoomRepository;
import student.data.StudentRepository;

@Controller
@RequestMapping("/room_details")
public class addRoomController {
	private final BillRoomRepository bill;
	private  final RoomRepository roomRepo ;
	private final StudentRepository studentRepo;
	String temp ;
    public addRoomController(RoomRepository roomRepo , BillRoomRepository bill , StudentRepository studentRepo) {
		this.roomRepo = roomRepo;
		this.bill = bill;
		this.studentRepo = studentRepo;
    }
    @GetMapping
	public String getDetailsForm(Model model,HttpServletRequest request , HttpSession session){
		String id = request.getParameter("id");
		Optional<Room> stu = roomRepo.findById(id);
		Room room = stu.get();
		session.setAttribute("old_list", room.getStudents());
		model.addAttribute("room", room);
		return "room_details";
    }
    @PostMapping
	public String saveDetails(@Valid Room room , Errors errors , HttpSession session) {
		ArrayList<Student> list = (ArrayList<Student>) session.getAttribute("old_list");
		if(errors.hasErrors()) {
			return "room_details";
		}
		else {
			System.out.println("hello");
			room.setStudents(list);
			roomRepo.save(room);
			return "redirect:/room";
		}
    }
    
    @GetMapping("/delete")
	public String deleteRoom(Model model ,HttpServletRequest request , HttpSession session) {
		String id = request.getParameter("id");
		Optional<Room> stu = roomRepo.findById(id);
		Room room = stu.get();
		if(room.getStudents().size() > 0){
			session.setAttribute("message1", "Can't delete this room . Please delete all student in this room");
			session.setAttribute("dem", 1);
			return "redirect:/room";
		}
		session.setAttribute("message1", null);
		roomRepo.save(room);
		roomRepo.delete(room);
		return "redirect:/room";
	}

	@GetMapping("/changeRoom")
	public String changeRoom(HttpServletRequest request){
		temp = request.getParameter("sv_id");
		return "changeRoom";
	}
	
	@PostMapping("/changeRoom")
	public String postChangeRoom(HttpServletRequest request , Model model){
		String studentID = temp;
		String newRoomName = request.getParameter("Room");
		System.out.println(newRoomName);
		Optional<Student> student = studentRepo.findById(studentID);
		Student stu = student.get();
		if(newRoomName == "" || newRoomName == null){
			model.addAttribute("message", "you have not entered any infomation");
			return "changeRoom";
		}

		Optional<Room> rooms = roomRepo.findById(newRoomName);
		if(rooms.isEmpty()){
			model.addAttribute("message", "Wrong student id");
			return "changeRoom";
		}
		
		Room room = rooms.get();
		if(room.getStudents().size() >= room.getMaximum()){
			model.addAttribute("message", "Room is Full");
			return "changeRoom";
		}
		ArrayList<Roombill> list = (ArrayList<Roombill>) bill.findAll();
		Roombill newBill = null;

        for(Roombill rb : list){
            if(rb.getStudent().getSv_id()==stu.getSv_id() && rb.isStatus()== true){
                newBill = rb;
                break;
            }
		}
		Roombill bill1 = new Roombill();
		newBill.setStatus(false);
		newBill.setTimeEnd(new Date());
		bill.save(newBill);
		room.getStudents().add(stu);
		bill1.setRoom(room);
		bill1.setStudent(stu);
		bill1.setStatus(true);
		bill.save(bill1);


		return "redirect:/room";
	}
}
