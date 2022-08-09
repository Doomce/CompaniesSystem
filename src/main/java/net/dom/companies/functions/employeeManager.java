package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.database.*;
import net.dom.companies.objects.duty;
import net.dom.companies.prompts.EmpInvitationPrompt;
import net.dom.companies.prompts.EmpSalaryPrompt;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.*;

public class employeeManager {

    private static class Verification {
        final UUID uid;
        int times = 0;
        int action; //1 - promote; 2- demote

        public Verification(UUID uid, int action) {
            this.uid = uid;
            this.action = action;
        }
    }

    private static Companies comp;
    private final double minWage = 300.00;

    public employeeManager(Companies plugin) {
        comp = plugin;
    }

    private final int verificationTimes = 3;
    private final int maxCompaniesWorking = 3;
    private HashMap<Player, Verification> verifications;
    private HashMap<UUID, Long> invitations;
    private boolean isActionVerified(Player p, UUID uid, int action) {
        if (verifications.containsKey(p)) {
            if (verifications.get(p).uid != uid) return false;
            if (verifications.get(p).action != action) {
                verifications.get(p).times = 1;
                verifications.get(p).action = action;
                return false;
            }
            if (verifications.get(p).times >= verificationTimes) {
                verifications.remove(p);
                return true;
            }
            verifications.get(p).times++;
        } else {
            verifications.put(p, new Verification(uid, action));
            Bukkit.getScheduler().runTaskLaterAsynchronously(comp, () -> {
                verifications.remove(p);
            }, 15*20);
        }
        return false;
    }

    public void promote(Player p, Long compId, CompaniesEmployees employee, boolean isAdmin) {
        if (!p.hasPermission("companies.promote")) return;
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Company comp = session.get(Company.class, compId);
            CompaniesEmployees staff = isEmpWorkingInCompany(p, comp);
            if (staff != null && (!staff.getDuties().equals(duty.OWNER))) {
                p.sendMessage("Rangus gali kelti tik imoniu savininkai.");
                return;
            }
            if (staff == null && !isAdmin) {
                p.sendMessage("Ne administratorius.");
                return;
            }

            switch (employee.getDuties()) {
                case OWNER: {
                    p.sendMessage("Neimanoma pakelti.");
                    break;
                }
                case CO_OWNER: {
                    p.sendMessage("Pasirinkus šią nuostatą, jūs nebeliekate įmonės savininku.");
                    p.sendMessage("Patvirtink šį veiksmą, tai kartodamas 3 kartus per 15 sek.");
                    if (isActionVerified(p, employee.getId().getEmployeeId(), 1)) {
                        comp.getCE().stream()
                                .filter((emp) -> emp.getDuties().equals(duty.OWNER))
                                .findAny().ifPresent(demoteEmp -> demoteEmp.setDuties(duty.CO_OWNER));
                    }
                    break;
                }
                case MANAGER: {
                    p.sendMessage("Pakeltas į pavaduotoją...");
                    comp.getCE().stream()
                            .filter((emp) -> emp.getDuties().equals(duty.CO_OWNER))
                            .findAny().ifPresent(demoteEmp -> demoteEmp.setDuties(duty.MANAGER));
                    employee.setDuties(duty.CO_OWNER);
                    break;
                }
                case WORKER: {
                    p.sendMessage("Pakeltas į Menedžerį...");
                    employee.setDuties(duty.MANAGER);
                    break;
                }
            }

            tx.commit();
            session.close();
            companyManager.openCompanyEmpPanel(p, compId);
        });
    }

    public void demote(Player p, Long compId, CompaniesEmployees employee, boolean isAdmin) {
        if (!p.hasPermission("companies.demote")) return;
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Company comp = session.get(Company.class, compId);
            CompaniesEmployees staff = isEmpWorkingInCompany(p, comp);
            if (staff != null && (!staff.getDuties().equals(duty.OWNER))) {
                p.sendMessage("Rangus gali žeminti tik imoniu savininkai.");
                return;
            }
            if (staff == null && !isAdmin) {
                p.sendMessage("Ne administratorius.");
                return;
            }

            switch (employee.getDuties()) {
                case CO_OWNER: {
                    employee.setDuties(duty.MANAGER);
                    break;
                }
                case MANAGER: {
                    p.sendMessage("Pažemintas į darbuotoją...");
                    employee.setDuties(duty.WORKER);
                    break;
                }
                case WORKER: case OWNER: {
                    p.sendMessage("Neimanoma pažeminti.");
                    break;
                }
            }

            tx.commit();
            session.close();
            companyManager.openCompanyEmpPanel(p, compId);
        });
    }

    public void kick(Player p, Long compId, UUID empUid, boolean isAdmin) {
        if (!p.hasPermission("companies.kick")) return;
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            CompaniesEmployees employee =
                    session.load(CompaniesEmployees.class, new CompaniesEmployeesKeys(empUid, compId));
            CompaniesEmployees staff = isEmpWorkingInCompany(p, employee.getCompany());

            if (staff != null && (!(staff.getDuties().equals(duty.OWNER) || staff.getDuties().equals(duty.CO_OWNER)))) {
                p.sendMessage("Ismesti gali tik imoneje dirbantis savininkas ar pavaduotojas.");
                return;
            }
            if (staff == null && !isAdmin) {
                p.sendMessage("Ne administratorius.");
                return;
            }
            if (p.getUniqueId() == employee.getId().getEmployeeId()) {
                p.sendMessage("Saves neiseis ismesti...");
                return;
            }
            if (employee.getDuties().equals(duty.OWNER)) {
                p.sendMessage("Savininko negalima ismesti.");
                return;
            }
            Transaction tx = session.beginTransaction();
            session.remove(employee);
            tx.commit();
            p.sendMessage(Bukkit.getPlayer(employee.getEmployee().getUuid()).getName()+" pasalintas is kompanijos.");
            session.close();
            companyManager.openCompanyEmpPanel(p, compId);
        });
    }

    public void quit(Player p, Long compId) {
        if (!p.hasPermission("companies.quit")) return;
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            CompaniesEmployees compEmp =
                    session.load(CompaniesEmployees.class, new CompaniesEmployeesKeys(p.getUniqueId(), compId));
            if (compEmp == null) return;
            if (compEmp.getDuties().equals(duty.OWNER)) {
                p.sendMessage("Vadovai negali išeiti iš įmonės. Jei nori? pradėk įmonės uždarymo operacijas.");
                return;
            }
            Transaction tx = session.beginTransaction();
            session.remove(compEmp);
            tx.commit();
            p.sendMessage("Palikai kompanija");
            session.close();
        });
    }

    public void invite(Player p, Player target, Long compId) {
        if (target == null || (!target.isOnline())) return;
        if (p.getName().equals(target.getName())) return;
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {

            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.load(Company.class, compId);
            CompaniesEmployees staff = isEmpWorkingInCompany(p, company);
            if (staff == null || !(staff.getDuties().equals(duty.CO_OWNER) || staff.getDuties().equals(duty.OWNER))) {
                p.sendMessage("Priimti gali tik pavaduotojai ir direktoriai.");
                return;
            }
            Employee emp = session.load(Employee.class, target.getUniqueId());
            if (emp != null && emp.getCE().size() > maxCompaniesWorking) {
                p.sendMessage("Žaidėjas nebegali papildomai dirbti....");
                return;
            }
            if (invitations.containsKey(target.getUniqueId())) {
                p.sendMessage("Žaidėjas jau turi galiojantį kvietimą");
                return;
            }
            if (emp != null && emp.getCE().stream().anyMatch(CE -> CE.getCompany().getCompId().equals(compId))) {
                p.sendMessage("Žaidėjas dirba jau jūsų kompanijoje.");
                return;
            }

            invitations.put(target.getUniqueId(), compId);
            target.sendMessage("Jūs pakviestas į kompaniją: "+company.getName());
            p.sendMessage("Pakvietėte "+target.getName()+" dirbti į savo kompaniją.");

            session.close();
        });
    }

    public void changeWage(Conversable p, UUID targetUid, long compId, double wage, boolean isAdmin) {
        if (((Player) p).getUniqueId().equals(targetUid)) {
            return;
        }
        if ((minWage>wage && wage != 0) && !isAdmin) {

            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {

            Session session = hibernateUtil.getSessionFactory().openSession();
            CompaniesEmployees compEmp =
                    session.load(CompaniesEmployees.class, new CompaniesEmployeesKeys(targetUid, compId));
            if (compEmp == null) {
                return;
            }
            Player target = Bukkit.getPlayer(targetUid);
            if (target == null) {
                return;
            }
            if (!target.isOnline() && !isAdmin) {
                ((Player) p).sendMessage("Zaidejas neprisijunges");
                //TODO MSG
                return;
            }
            Transaction tx = session.beginTransaction();
            if (wage == 0) {
                compEmp.setSalary(null);
                ((Player) p).sendMessage("Nuo siol sis zaidejas skaitomas, kaip nedirbantis.");
            } else {
                compEmp.setSalary(wage);
                ((Player) p).sendMessage("Pakeitei zaidejo alga.");
            }
            tx.commit();
            session.close();
        });
    }

    public void acceptInvitation(Player p) {
        if (!p.hasPermission("companies.invite.accept")) return;
        if (!invitations.containsKey(p.getUniqueId())) {
            p.sendMessage("neturi galiojančių kvietimų.");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.load(Company.class, invitations.get(p.getUniqueId()));
            if (company == null) return;
            if (company.getCE().size()+1 > companyManager.getMaxWorkers(company)) {
                p.sendMessage("Nepriimtas, nes viršijamas darbuotojų limitas.");
                return;
            }
            Employee emp = session.load(Employee.class, p.getUniqueId());
            Transaction tx = session.beginTransaction();
            if (emp == null) {
                emp = new Employee();
                emp.setUuid(p.getUniqueId());
                session.save(emp);
            }
            CompaniesEmployees CE = new CompaniesEmployees();
            CE.setCompany(company);
            CE.setEmployee(emp);
            CE.setDuties(duty.WORKER);
            CE.setWorkingFrom(LocalDate.now());

            company.getCE().add(CE);
            tx.commit();
            p.sendMessage("Įsidarbinai į "+company.getBusinessForm().name()+" "+company.getName()+"!");
            session.close();
            invitations.remove(p.getUniqueId());
        });
    }

    public void rejectInvitation(Player p) {
        if (!invitations.containsKey(p.getUniqueId())) {
            p.sendMessage("neturi galiojančių kvietimų.");
            return;
        }
        invitations.remove(p.getUniqueId());
    }

    private CompaniesEmployees isEmpWorkingInCompany(Player p, Company company) {
        return company.getCE().stream()
                .filter(emp -> emp.getId().getEmployeeId().equals(p.getUniqueId()))
                .findAny()
                .orElse(null);
    }

    public static void invitationPrompt(Player p, Long compId) {
        if (!p.hasPermission("companies.invite")) return;
        p.closeInventory();

        HashMap<Object, Object> data = new HashMap<>();
        data.put("compId", compId);

        ConversationFactory factory = new ConversationFactory(comp);
        factory.withFirstPrompt(new EmpInvitationPrompt(comp)).withTimeout(25)
                .withEscapeSequence("atsaukti").withEscapeSequence("atšaukti").withInitialSessionData(data)
                .withLocalEcho(false).buildConversation(p).begin();
    }

    public static void wagePrompt(Player p, CompaniesEmployees compEmp) {
        if (!p.hasPermission("companies.wage.change")) return;
        p.closeInventory();

        HashMap<Object, Object> data = new HashMap<>();
        data.put("empUid", compEmp.getId().getEmployeeId());
        data.put("compId", compEmp.getId().getCompanyId());

        ConversationFactory factory = new ConversationFactory(comp);
        factory.withFirstPrompt(new EmpSalaryPrompt()).withTimeout(25)
                .withEscapeSequence("atsaukti").withEscapeSequence("atšaukti")
                .withInitialSessionData(data)
                .withLocalEcho(false).buildConversation(p).begin();
    }

}
