package student.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import student.Roombill;
import student.Student;
import student.perTicket;
import student.service;
import student.serviceOrder;
import student.ticket;
import student.data.BillRoomRepository;
import student.data.OrderRepository;
import student.data.ServiceRepository;
import student.data.StudentRepository;
import student.data.perTickerRepository;
import student.data.ticketRepo;

@Slf4j
@Controller
@RequestMapping("/service")
public class serviceController {
    private final StudentRepository studentRepo;
    private final ServiceRepository serviceRepo;
    private final OrderRepository orderRepo;
    private final ticketRepo ticketRepo;
    private final BillRoomRepository BillRepo;
    private final perTickerRepository perTicketRepo;

    public serviceController(perTickerRepository perTicketRepo, BillRoomRepository BillRepo,
            StudentRepository studentRepo, ServiceRepository serviceRepo, OrderRepository orderRepo,
            ticketRepo ticketRepo) {
        this.studentRepo = studentRepo;
        this.serviceRepo = serviceRepo;
        this.orderRepo = orderRepo;
        this.ticketRepo = ticketRepo;
        this.BillRepo = BillRepo;
        this.perTicketRepo = perTicketRepo;
    }

    @GetMapping
    public String getServiceForm(Model model) {
        ArrayList<service> list = (ArrayList<service>) serviceRepo.findAll();
        model.addAttribute("services", list);
        return "allService";
    }

    @GetMapping("/create")
    public String getCreateServiceForm(Model model) {
        model.addAttribute("service", new service());
        return "addService";
    }

    @PostMapping
    public String createService(@Valid service Service) {
        serviceRepo.save(Service);
        return "redirect:/service";
    }

    @GetMapping("/getStudent")
    public String getChooseStudentForm(Model model, HttpServletRequest request) {
        return "ChooseStudent";
    }

    @PostMapping("/getStudent")
    public String postChooseStudentForm(Model model, HttpSession session, HttpServletRequest request) {
        String studentID = request.getParameter("student");
        System.out.println(studentID);

        Optional<Student> stu = studentRepo.findById(studentID);
        if (stu.isEmpty()) {
            model.addAttribute("message", "Wrong student ID");
            return "ChooseStudent";
        }
        Student student = stu.get();
        session.setAttribute("student", studentID);
        return "redirect:/service/getService";
    }

    @GetMapping("/getService")
    public String getChooseServiceForm(Model model, HttpServletRequest request, HttpSession session) {
        if (session.getAttribute("student") == null) {
            return "ChooseStudent";

        }
        System.out.println(session.getAttribute("student"));
        ArrayList<service> list = (ArrayList<service>) serviceRepo.findAll();
        model.addAttribute("services", list);
        model.addAttribute("messsage1", "add success");
        return "order";
    }

    @GetMapping("/addToCart")
    public String addToCartForm(Model model, HttpSession session, HttpServletRequest request) {
        if (session.getAttribute("student") == null) {
            return "ChooseStudent";

        }
        if (session.getAttribute("list_service") == null) {
            session.setAttribute("list_service", new ArrayList<>());
        }
        ArrayList<service> list = (ArrayList<service>) session.getAttribute("list_service");
        Optional<service> sv = serviceRepo.findById(Long.valueOf(request.getParameter("service_id")));
        service Service = sv.get();
        if (Service.getService_name().equalsIgnoreCase("Vé tháng")) {
            return "redirect:/service/signTicket";
        }
        list.add(Service);

        session.setAttribute("list_service", list);
        model.addAttribute("messsage1", "add success");
        model.addAttribute("services", list);
        int t = 0;
        for (service s : list) {
            t += s.getPrice();
        }
        String s = "Total :" + String.valueOf(t) + " VND";
        model.addAttribute("price", s);
        return "redirect:/service/getService";
    }

    @PostMapping("/buy")
    public String buyAll(HttpSession session) {
        if (session.getAttribute("student") == null) {
            return "ChooseStudent";
        }
        ArrayList<service> list = (ArrayList<service>) session.getAttribute("list_service");
        Optional<Student> stu = studentRepo.findById(String.valueOf(session.getAttribute("student")));
        Student student = stu.get();
        System.out.println(list);
        for (service s : list) {
            serviceOrder svo = new serviceOrder();
            Calendar c= Calendar.getInstance();
            c.add(Calendar.DATE, s.getTime_to_use());
            Date d1=c.getTime();
            svo.setEnd(d1);
            svo.setService(s);
            svo.setStudent(student);
            svo.setStatus(false);
            orderRepo.save(svo);
        }
        session.setAttribute("student", null);
        session.setAttribute("list_service", null);
        return "redirect:/service/getService";
    }

    @GetMapping("/getCart")
    public String deleteFromCart(HttpSession session, HttpServletRequest request, Model model) {
        ArrayList<service> list = (ArrayList<service>) session.getAttribute("list_service");
        model.addAttribute("services", list);
        int t = 0;
        for (service s : list) {
            t += s.getPrice();
        }
        String s = "Total :" + String.valueOf(t) + " VND";
        model.addAttribute("price", s);
        return "listService";
    }

    @GetMapping("/viewAll")
    public String getAllStudentService(HttpServletRequest request, Model model, HttpSession session) {
        String studentID = request.getParameter("sv_id");
        session.setAttribute("Student_id", request.getParameter("sv_id"));
        ArrayList<serviceOrder> list = (ArrayList<serviceOrder>) orderRepo.findAll();
        Optional<Student> stu = studentRepo.findById(studentID);
        Student student = stu.get();
        ArrayList<serviceOrder> list1 = new ArrayList<>();
        int price = 0;
        for (serviceOrder svo : list) {
            if (svo.getStudent().getSv_id().equalsIgnoreCase(studentID) && !svo.isStatus()) {
                price += svo.getService().getPrice();
                list1.add(svo);
            }
        }
        ArrayList<Roombill> list2 = (ArrayList<Roombill>) BillRepo.findAll();
        ArrayList<Roombill> list3 = new ArrayList<>();
        for (Roombill rb : list2) {
            if (rb.getStudent() == student && !rb.isPay() && !rb.isStatus()) {
                list3.add(rb);
                price += rb.getRoom().getPrice();
            }
        }
        ArrayList<perTicket> list4 = (ArrayList<perTicket>) perTicketRepo.findAll();
        ArrayList<perTicket> list5 = new ArrayList<>();
        for (perTicket pt : list4) {
            if (pt.getNumber() == 3000) {
                list5.add(pt);
                price += 3000;
            }
        }
        String t = "Total :" + String.valueOf(price) + " VND";
        model.addAttribute("list1", list1);
        model.addAttribute("list2", list3);
        model.addAttribute("list3", list5);
        session.setAttribute("list1", list1);
        session.setAttribute("list3", list3);
        model.addAttribute("price", t);
        return "studentService";
    }

    @GetMapping("/search")
    public String search(HttpServletRequest request, Model model, HttpSession session) {
        String studentID = (String) session.getAttribute("Student_id");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sdf.parse(start);
            Date date2 = sdf.parse(end);
        ArrayList<serviceOrder> list = (ArrayList<serviceOrder>) orderRepo.findAll();
        Optional<Student> stu = studentRepo.findById(studentID);
        Student student = stu.get();
        ArrayList<serviceOrder> list1 = new ArrayList<>();
        int price = 0;
        for(serviceOrder svo : list){
            if(svo.getStudent().getSv_id().equalsIgnoreCase(studentID) && date1.compareTo(sdf.parse(sdf.format(svo.getBuyAt()))) < 0 
            && date2.compareTo(sdf.parse(sdf.format(svo.getBuyAt()))) > 0 && !svo.isStatus()){
                price += svo.getService().getPrice();
                list1.add(svo);
            }
        }
        ArrayList<Roombill> list2 = (ArrayList<Roombill>) BillRepo.findAll();
		ArrayList<Roombill> list3 = new ArrayList<>();
		for(Roombill rb : list2){
            if(rb.getStudent()== student && date1.compareTo(sdf.parse(sdf.format(rb.getCreatedAt()))) < 0 
            && !rb.isPay() && !rb.isStatus()){
                list3.add(rb);
                price += rb.getRoom().getPrice();
			}
        }
        ArrayList<perTicket> list4 = (ArrayList<perTicket>) perTicketRepo.findAll();
        ArrayList<perTicket> list5 = new ArrayList<>();
        for(perTicket pt : list4){
            if(pt.getNumber() == 3000){
                list5.add(pt);
                price+=3000;
            }
        }
        String t = "Total :" + String.valueOf(price) +" VND";
        model.addAttribute("list1", list1);
        session.setAttribute("list1", list1);
        session.setAttribute("list3", list3);
        model.addAttribute("list2", list3);
        model.addAttribute("list3", list5);
        model.addAttribute("price", t);
        return "studentService";
        } catch (ParseException e) {
            e.printStackTrace();
            return "studentService";
        }
        
        
    }
    @GetMapping("/signTicket")
    public String getVeXe(){
        
        return "getVeXe";
    }

    @PostMapping("/signTicket")
    public String signTicket(HttpSession session , HttpServletRequest request , Model model){
        System.out.println(session.getAttribute("student"));
        System.out.println(request.getParameter("Biensoxe"));
        ArrayList<service> sv = (ArrayList<service>) serviceRepo.findAll();
        service Service = null ;
        int dem = 0;
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();
        System.out.println(month);
        for(service s : sv){
            if(s.getService_name().equalsIgnoreCase("Vé tháng")){
                Service = s;
            }
        }

        Optional<Student> stu = studentRepo.findById(String.valueOf(session.getAttribute("student")));
        Student student = stu.get();
        ArrayList<serviceOrder>list1 = (ArrayList<serviceOrder>) orderRepo.findAll();
        for(serviceOrder sv1 : list1){
            if(sv1.getStudent() == student && sv1.getService() == Service 
                   && month == sv1.getBuyAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue()){
                       System.out.println("alo");
                       dem ++;
            }
        }
        if(dem >=2){
            model.addAttribute("message" , "this user can only get 2 ticket");
            if (session.getAttribute("student") == null) {
                return "ChooseStudent";
    
            }
            System.out.println(session.getAttribute("student"));
            ArrayList<service> list = (ArrayList<service>) serviceRepo.findAll();
            model.addAttribute("services", list);
            model.addAttribute("messsage1", "add success");
            return "order";

        }
        serviceOrder svo = new serviceOrder();
        svo.setService(Service);
        svo.setStudent(student);

        orderRepo.save(svo);

        String ticketID = request.getParameter("Biensoxe");

        ticket Ticket = new ticket();

        Ticket.setBienSo(ticketID);
        Ticket.setServiceOrders(svo);

        ticketRepo.save(Ticket);
        return "redirect:/service";
    }

    @PostMapping("/pay")
    public String payAll(HttpServletRequest request, Model model, HttpSession session){
        ArrayList<serviceOrder> list = (ArrayList<serviceOrder>) session.getAttribute("list1");
        ArrayList<Roombill> list1 = (ArrayList<Roombill>) session.getAttribute("list3");
        if(list== null && list1 != null){
            for(Roombill rb : list1){
                rb.setPay(true);
                BillRepo.save(rb);
            }
            model.addAttribute("mess", "payment suceess");
            return "studentService";
        }
        else if(list!= null && list1 == null){
            for(serviceOrder svo : list){
                svo.setStatus(true);
                orderRepo.save(svo);
            }
            model.addAttribute("mess", "payment suceess");
            return "studentService";
        }
        else if(list== null && list1 == null){
            return "redirect:/all";
        }
        else{
            for(Roombill rb : list1){
                rb.setPay(true);
                BillRepo.save(rb);
            }
            for(serviceOrder svo : list){
                svo.setStatus(true);
                orderRepo.save(svo);
            }
            model.addAttribute("mess", "payment suceess");
            return "studentService";
        }
    }
    @GetMapping("/view")
    public String getAllStudentService1(HttpServletRequest request, Model model, HttpSession session) {
        String studentID = (String) session.getAttribute("Student_id");
        session.setAttribute("Student_id", request.getParameter("sv_id"));
        ArrayList<serviceOrder> list = (ArrayList<serviceOrder>) orderRepo.findAll();
        Optional<Student> stu = studentRepo.findById(studentID);
        Student student = stu.get();
        ArrayList<serviceOrder> list1 = new ArrayList<>();
        int price = 0;
        for (serviceOrder svo : list) {
            if (svo.getStudent().getSv_id().equalsIgnoreCase(studentID)) {
                price += svo.getService().getPrice();
                list1.add(svo);
            }
        }
        ArrayList<Roombill> list2 = (ArrayList<Roombill>) BillRepo.findAll();
        ArrayList<Roombill> list3 = new ArrayList<>();
        for (Roombill rb : list2) {
            if (rb.getStudent() == student ) {
                list3.add(rb);
                price += rb.getRoom().getPrice();
            }
        }
        ArrayList<perTicket> list4 = (ArrayList<perTicket>) perTicketRepo.findAll();
        ArrayList<perTicket> list5 = new ArrayList<>();
        for (perTicket pt : list4) {
            if (pt.getNumber() == 3000) {
                list5.add(pt);
                price += 3000;
            }
        }
        String t = "Total :" + String.valueOf(price) + " VND";
        model.addAttribute("list1", list1);
        model.addAttribute("list2", list3);
        model.addAttribute("list3", list5);
        session.setAttribute("list1", list1);
        session.setAttribute("list3", list3);
        model.addAttribute("price", t);
        return "studentService";
    }

   
}
