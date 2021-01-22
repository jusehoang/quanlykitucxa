package student.web;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import student.Student;
import student.serviceOrder;
import student.data.BillRoomRepository;
import student.data.OrderRepository;
import student.data.RoomRepository;
import student.data.StudentRepository;
import student.data.*;

@Controller
@RequestMapping("/statics")
public class staticController {
    private final RoomRepository roomRepo;
    private final StudentRepository studentRepo;
    private final BillRoomRepository Bill;
    private final OrderRepository orderRepo;
    private final ticketRepo ticketRepo ;

    public staticController(RoomRepository roomRepo, StudentRepository studentRepo, BillRoomRepository bill,
            OrderRepository orderRepo, ticketRepo ticketRepo) {
        this.roomRepo = roomRepo;
        this.studentRepo = studentRepo;
        Bill = bill;
        this.orderRepo = orderRepo;
        this.ticketRepo = ticketRepo;
    }

    @GetMapping
    public String getAllStudentStatic(Model model , HttpServletRequest request){
        Optional<Student> stu = studentRepo.findById(request.getParameter("sv_id"));
        Student student = stu.get();
        ArrayList<serviceOrder> svoList = (ArrayList<serviceOrder>) orderRepo.findAll();
        ArrayList<serviceOrder> svoList1 = new ArrayList<>();
        for(serviceOrder svo : svoList){
            if(svo.getStudent() == student){
                svoList1.add(svo);
            }
        }
        model.addAttribute("list", svoList);
        model.addAttribute("studentID", request.getParameter("sv_id"));
        return "static";
    }
}
