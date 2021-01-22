package student.web;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.Session;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;
import student.Room;
import student.Roombill;
import student.Student;
import student.data.BillRoomRepository;
import student.data.RoomRepository;
import student.data.StudentRepository;

@Slf4j
@Controller
@RequestMapping("/room")
public class RoomController {
    private final RoomRepository roomRepo;
    private final StudentRepository studentRepo;
    private final BillRoomRepository Bill;
    public RoomController(RoomRepository roomRepo, StudentRepository studentRepo , BillRoomRepository Bill) {
        this.roomRepo = roomRepo;
        this.studentRepo = studentRepo;
        this.Bill = Bill;
    }
    @GetMapping
    public String getAllRoomForm(Model model , HttpSession session) {
        List<Room> list = (List<Room>) roomRepo.findAll();
        int a = (int) session.getAttribute("dem");
        if(a == 0){
            session.setAttribute("message1", null);
        }
        else{
            session.setAttribute("dem", 0);
        }
        model.addAttribute("rooms", list);
        return "allroom";
    }

    @RequestMapping(value = "addroom", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        Room room = new Room();
        model.addAttribute("room", room);
        return "addroom";
    }

    @PostMapping
    public String createRoom(@Valid Room room, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult);
            return "addroom";
        }

        else {
			roomRepo.save(room);
			return "redirect:/room";
		}	
    }

    @GetMapping("/addtoroom")
    public String addStudentToRoomForm(HttpServletRequest request , HttpSession session ){
        String s = request.getParameter("id");
        session.setAttribute("RoomID", s);
        return "addtoroom";
    }

    @PostMapping("/addtoroom")
    public String addStudentToRoom(HttpServletRequest request ,  HttpSession session , Model model){
        String StudentID = request.getParameter("StudentID");
        String RoomId = (String) session.getAttribute("RoomID");

        System.out.println(RoomId);
        
        if(StudentID == null || StudentID ==""){
            model.addAttribute("message", "you have not entered any infomation");
            return "addtoroom"; 
        }

            Optional<Student> student = studentRepo.findById(StudentID);
            Student stu;
            if(student.isEmpty()){
                model.addAttribute("message", "Wrong student id");
                return "addtoroom";
            }
            else{
                stu = student.get();
            }

            Optional<Room> rooms = roomRepo.findById(RoomId);
            Room room = rooms.get(); 

            if(room.getStudents().size() >= room.getMaximum()){
                model.addAttribute("message", "No Empty Slot in this Room");
                return "addtoroom";
            }

            ArrayList<Roombill> list = (ArrayList<Roombill>) Bill.findAll();
            boolean haveRoom = false;

            Roombill newBill = null;
            for(Roombill rb : list){
                if(rb.getStudent().getSv_id()==stu.getSv_id() && rb.isStatus()== true){
                    newBill = rb;
                    break;
                }
            }
            if(newBill== null){
                Roombill bill = new Roombill();
                Date date = new Date();
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int month = localDate.getDayOfMonth();

                Calendar cal = Calendar.getInstance();
                int res = cal.getActualMaximum(Calendar.DATE);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,res-month);
                Date d1=c.getTime();
                bill.setTimeEnd(d1);
                bill.setRoom(room);
                bill.setStudent(stu);
                room.getStudents().add(stu);
                bill.setStatus(true);
                Bill.save(bill);
                roomRepo.save(room);
                return "redirect:/room";
            }
            else if(newBill.getRoom().getId() == room.getId()){
                model.addAttribute("message", "Student is already in this room");
                return "addtoroom";
            }
            else{
                model.addAttribute("message", "Student is already in this room , Please Delete this student in that room");
                return "addtoroom";
            }
    }
}