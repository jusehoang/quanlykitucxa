package student.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import student.perTicket;
import student.ticket;
import student.data.perTickerRepository;
import student.data.ticketRepo;

@Controller
@RequestMapping("/getTicket")
public class addTicketVehiceController {
    private final ticketRepo tk ;
    private final perTickerRepository perRepo;

    public addTicketVehiceController(ticketRepo tk, perTickerRepository perRepo) {
        this.tk = tk;
        this.perRepo = perRepo;
    }

    @GetMapping
    public String getTicketForm(Model model){
        return "getMoto";
    }

    @PostMapping
    public String postTicketForm(HttpServletRequest request){
        String vehiceID = request.getParameter("Biensoxe");
        Boolean available = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ticket Ticket = null;
        ArrayList<ticket> list = (ArrayList<ticket>) tk.findAll();
        for(ticket t : list){
            if(t.getBienSo().equalsIgnoreCase(vehiceID)){
                Ticket = t;
                break;
            }
        }
        if(Ticket== null){
            ticket t = new ticket();
            t.setBienSo(vehiceID);
            tk.save(t);
            perTicket perT = new perTicket();
            perT.setTicket(t);
            perT.setStart(new Date());
            perT.setStatus(false);
            perRepo.save(perT);
        }
        else{
            perTicket perT = null;
            ArrayList<perTicket> list1 = (ArrayList<perTicket>) perRepo.findAll();
            for(perTicket p : list1){
                if(p.getTicket()== Ticket && !p.getStatus()){
                    perT = p;
                    break;
                }
            }
            if(perT == null){
                int dem = 0;
                ArrayList<perTicket> temp = new ArrayList<>();
                for(perTicket p : (ArrayList<perTicket>) perRepo.findAll()){
                    if(p.getTicket() == Ticket){
                        temp.add(p);
                    }
                }
                for(perTicket p : temp){
                    if(sdf.format(p.getStart()).equalsIgnoreCase(sdf.format(new Date()))){
                        dem = dem + 1 ;
                    }
                }
                perT = new perTicket();
                perT.setStart(new Date());
                perT.setStatus(false);
                perT.setTicket(Ticket);
                if(dem >= 2){
                    perT.setNumber(3000);
                }
                else{
                    perT.setNumber(0);
                }
                //System.out.println(sdf.format(new Date()));
                perRepo.save(perT);
            }
            else{
                perT.setEnd(new Date());
                perT.setStatus(true);
                perRepo.save(perT);
            }
        }
        
        return "redirect:/getTicket";
    }
    
}
