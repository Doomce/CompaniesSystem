package net.dom.companies;

import net.dom.companies.commands.commandManager;
import net.dom.companies.database.databaseOperations;
import net.dom.companies.functions.companyManager;
import net.dom.companies.functions.functionsHandler;
import net.dom.companies.lang.Language;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Companies extends JavaPlugin {

    private static Companies instance;
    public static Logger log;
    public Permission perms;
    public Economy eco;

    private databaseOperations dbOps;
    private functionsHandler fH;
    private Language lang;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        log = this.getLogger();
        setupEconomy();
        dbOps = new databaseOperations(this);

        commandManager cmdMng = new commandManager(this);
        this.getCommand("comp").setExecutor(cmdMng);

        fH = new functionsHandler(this);
        lang = new Language(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }


    public static Companies getInstance() {
        return instance;
    }

    public databaseOperations getDb() {
        return dbOps;
    }

    public functionsHandler getFH() {
        return fH;
    }

    public companyManager compMng() {
        return fH.compMng;
    }

    public Language getLang() {
        return lang;
    }
}
