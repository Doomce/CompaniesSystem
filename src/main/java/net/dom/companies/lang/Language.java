//Code from: maxbridgland
package net.dom.companies.lang;

import net.dom.companies.Companies;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Language {

    private Companies plugin;
    protected File defaultLanguageFile;
    protected static FileConfiguration translations;


    public Language(Companies comp) {
        plugin = comp;
        setup();
    }

    private final String defaultFileName = "lt_LT.yml";

    public void sendMsg(Player p, String path, Object... replacements) {
        p.sendMessage(get(path, replacements));
    }

    public static String get(String path, Object... replacements){
        if (!translations.contains(path)) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Negalima surasti rakto kalbos failuose: "+path);
            return "";
        }
        return ColorUtil.colorString(replace(translations.getString(path), replacements));
    }

    public static List<String> getList(String path, Object... replacements) {
        if (!translations.contains(path)) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Negalima surasti rakto kalbos failuose: "+path);
            return Collections.emptyList();
        }
        List<String> strList = new ArrayList<>();
        for (String msg : translations.getStringList(path)) {
            strList.add(ColorUtil.colorString(replace(msg, replacements)));
        }
        return strList;
    }

    private static String replace(String text, Object... replacements) {
        if (replacements == null) {
            return text;
        }
        for(int i = 0; i < replacements.length; i+=2) {
            String key = (String) replacements[i];
            if (replacements[i+1] instanceof String) {
                text = text.replace(key, (String) replacements[i+1]);
            } else if (replacements[i+1] instanceof Player) {
                text = text.replace(key, ((Player) replacements[i+1]).getName());
            } else {
                text = text.replace(key, String.valueOf(replacements[i+1]));
            }
        }
        return text;
    }

    public void setup() {
        defaultLanguageFile = new File(plugin.getDataFolder(), defaultFileName);

        if (!defaultLanguageFile.exists()) {
            plugin.saveResource(defaultFileName, false);
        }

        translations = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), defaultFileName));
    }

}
