package net.dom.companies.commands;

import net.dom.companies.Companies;
import net.dom.companies.database.Company;
import net.dom.companies.database.hibernateUtil;
import net.dom.companies.functions.companyManager;
import net.dom.companies.menus.compCreateMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import javax.annotation.Nonnull;
import java.io.IOException;

public class commandManager
implements CommandExecutor {
    private Companies comp;

    public commandManager(Companies plugin) {
        this.comp = plugin;
    }

    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (label.equalsIgnoreCase("comp")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        new compCreateMenu(p, null).open();
                        return false;
                    }
                    return false;
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        comp.getEmpMng().acceptInvitation(p);
                        return false;
                    }
                    return false;
                } else if (args[0].equalsIgnoreCase("reject")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        comp.getEmpMng().rejectInvitation(p);
                        return false;
                    }
                    return false;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    comp.getLang().setup();
                    return false;
                }


            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("info")) {
                    Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
                        Session session  = hibernateUtil.getSessionFactory().openSession();
                        Company comp = session.get(Company.class, Long.parseLong(args[1]));

                        sender.sendMessage("Informacija: [ID:"+comp.getCompId()+"] [Pav:"+comp.getName()+"]");
                        sender.sendMessage("--- Fondas: "+comp.getBankIban()+"; Jei NULL- Nėra;");
                        sender.sendMessage("--- Forma: ");
                        sender.sendMessage("    --> Daugiausia darb.: "+comp.getBusinessForm().getClassByName().getMaxWorkers());
                        sender.sendMessage("    --> Max kontraktų: "+comp.getBusinessForm().getClassByName().maxInProgressContracts());
                        sender.sendMessage("    --> Daugiausia lic.: "+comp.getBusinessForm().getClassByName().licCount());
                        sender.sendMessage("    --> Minimalus inašas: "+comp.getBusinessForm().getClassByName().initContribution());
                        sender.sendMessage("--- Įkurta: "+comp.getCreationDate());
                        sender.sendMessage("--- Darb skaičius: "+comp.getCE().size());
                        comp.getCE().forEach((employees -> {
                            sender.sendMessage("    --> UUID: "+ employees.getId().getEmployeeId());
                            sender.sendMessage("    --> Pareigos: "+ employees.getDuties());
                            sender.sendMessage("    --> Alga: "+ employees.getSalary());
                        }));

                        sender.sendMessage("--- --- --- --- --- ---");
                        session.close();
                    });

                    return true;
                }


                if (sender instanceof Player) {
                    Player p = (Player) sender;

                    return false;
                }
            } else {
                if (sender instanceof Player) {
                    Player p = (Player)sender;

                    comp.getFuncMng().getCList().runPlayer(p);

                    /*Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
                        Session session  = hibernateUtil.getSessionFactory().openSession();

                        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                        CriteriaQuery<company> criteriaQuery = criteriaBuilder.createQuery(company.class);
                        Root<employee> root = criteriaQuery.from(employee.class);
                        criteriaQuery.select(root.get("company"));
                        criteriaQuery.where(criteriaBuilder.equal(root.get("uuid"), p.getUniqueId()));
                        List<company> results = session.createQuery(criteriaQuery).setCacheable(false).getResultList();

                        results.forEach((c) -> {
                            p.sendMessage("comp: "+c.getName()+", "+c.getBusinessForm());
                            p.sendMessage("scope: "+c.getScope()+";");
                            p.sendMessage("emps: ");
                            c.getEmployees().forEach((employee -> {
                                p.sendMessage("name: "+Bukkit.getPlayer(employee.getUuid())+", "+employee.getDuties());
                                p.sendMessage("salary: "+employee.getSalary());
                            }));
                        });
                        session.close();
                    });*/

                    return true;
                }
                return false;
            }
        }
        return false;
    }
}