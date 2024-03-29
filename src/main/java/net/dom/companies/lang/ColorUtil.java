package net.dom.companies.lang;

import net.dom.companies.database.hibernateUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.sf.ehcache.hibernate.HibernateUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.hibernate.Session;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final String RAW_GRADIENT_HEX_REGEX = "<\\$#[A-Fa-f0-9]{6}>";

    /**
     * Converts a legacy message (one you would read in chat) to a json message.
     *
     * @param legacyString the legacy message to convert
     * @return the message converted to json
     */
    public static String legacyToJson(String legacyString) {
        if (legacyString == null) return "";
        String s = "UPDATE 'gangs' SET 'Active' = 1 WHERE `lyderis` = ? ";
        return ComponentSerializer.toString(TextComponent.fromLegacyText(legacyString));
    }


    /**
     * Converts a json message to a legacy message.
     *
     * @param json the json message to convert
     * @return the legacy message
     */
    public static String jsonToLegacy(String json) {
        if (json == null) return "";
        return BaseComponent.toLegacyText(ComponentSerializer.parse(json));
    }


    /**
     * This method can be used to apply Iridescent's colors and gradients to strings.
     * Depending on where it is used, the String might need to be converted to json.
     *
     * ADDON - CENTER STRING, BY DOM, use tag "<center:xx>"
     *
     * @param legacyMsg the message to parse
     * @return the parsed message
     */
    public static String colorString(String legacyMsg) {
        //GRADIENT
        legacyMsg = gradient(legacyMsg);
        //FORMAT CODES
        List<String> formatCodes = Arrays.asList("&k", "&l", "&m", "&n", "&o", "&r");
        for (String code : formatCodes) {
            legacyMsg = legacyMsg.replaceAll(code, ChatColor.getByChar(code.charAt(1)) + "");
        }
        //HEX VALUES
        legacyMsg = hex(legacyMsg);
        //CENTER
        legacyMsg = center(legacyMsg);
        return legacyMsg;
    }

    private static String center(String legacyMsg) {
        Matcher matcher = Pattern.compile("<(center:)+([0-9]{1,2})>").matcher(legacyMsg);
        if (!matcher.find()) {
            return legacyMsg;
        }
        int size = Integer.parseInt(matcher.group(2));
        legacyMsg = legacyMsg.replace("<center:"+size+">", "");
        legacyMsg = StringUtils.center(legacyMsg, size);
        return legacyMsg;
    }

    private static String hex(String legacyMsg) {
        Matcher matcher = Pattern.compile("<#[A-Fa-f0-9]{6}>").matcher(legacyMsg);
        int hexAmount = 0; //Amount of hexes in message
        while (matcher.find()) {
            matcher.region(matcher.end() - 1, legacyMsg.length());
            hexAmount++;
        }
        int startIndex = 0; //Starting point in the message when looking for hexes.
        for (int hexIndex = 0; hexIndex < hexAmount; hexIndex++) {
            int msgIndex = legacyMsg.indexOf("<#", startIndex);
            String hex = legacyMsg.substring(msgIndex + 1, msgIndex + 8);
            startIndex = msgIndex + 2;
            legacyMsg = legacyMsg.replaceFirst("<" + hex + ">", ChatColor.of(hex) + ""); //Replaces the hex code with the color
        }
        return legacyMsg;
    }


    /**
     * Used by {@link #colorString(String)} to apply
     * gradients to the provided string
     *
     * @param legacyMsg the string to parse
     * @return the parsed string
     */
    private static String gradient(String legacyMsg) {
        List<String> hexes = new ArrayList<>();
        Matcher matcher = Pattern.compile(RAW_GRADIENT_HEX_REGEX).matcher(legacyMsg);
        while (matcher.find()) {
            hexes.add(matcher.group().replace("<$", "").replace(">", ""));
        }
        int hexIndex = 0;
        List<String> texts = new LinkedList<>(Arrays.asList(legacyMsg.split(RAW_GRADIENT_HEX_REGEX)));
        StringBuilder finalMsg = new StringBuilder();
        for (String text : texts) {
            //If the text is the first part of the message (Without colors) append it right away.
            if (texts.get(0).equalsIgnoreCase(text)) {
                finalMsg.append(text);
                continue;
            }
            if (text.length() == 0) continue;
            if (hexIndex + 1 >= hexes.size()) {
                if (!finalMsg.toString().contains(text)) finalMsg.append(text);
                continue;
            }
            String fromHex = hexes.get(hexIndex);
            String toHex = hexes.get(hexIndex + 1);
            finalMsg.append(insertFades(text, fromHex, toHex, text.contains("&l"), text.contains("&o"), text.contains("&n"), text.contains("&m"), text.contains("&k")));
            hexIndex++;
        }
        return finalMsg.toString();
    }


    /**
     * Used by {@link #gradient(String)}
     */
    private static String insertFades(String msg, String fromHex, String toHex, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean magic) {
        msg = msg.replaceAll("&k", "");
        msg = msg.replaceAll("&l", "");
        msg = msg.replaceAll("&m", "");
        msg = msg.replaceAll("&n", "");
        msg = msg.replaceAll("&o", "");
        int length = msg.length();
        Color fromRGB = Color.decode(fromHex);
        Color toRGB = Color.decode(toHex);
        double rStep = Math.abs((double) (fromRGB.getRed() - toRGB.getRed()) / length);
        double gStep = Math.abs((double) (fromRGB.getGreen() - toRGB.getGreen()) / length);
        double bStep = Math.abs((double) (fromRGB.getBlue() - toRGB.getBlue()) / length);
        if (fromRGB.getRed() > toRGB.getRed()) rStep = -rStep; //200, 100
        if (fromRGB.getGreen() > toRGB.getGreen()) gStep = -gStep; //200, 100
        if (fromRGB.getBlue() > toRGB.getBlue()) bStep = -bStep; //200, 100
        Color finalColor = new Color(fromRGB.getRGB());
        msg = msg.replaceAll(RAW_GRADIENT_HEX_REGEX, "");
        msg = msg.replace("", "<$>");
        for (int index = 0; index <= length; index++) {
            int red = (int) Math.round(finalColor.getRed() + rStep);
            int green = (int) Math.round(finalColor.getGreen() + gStep);
            int blue = (int) Math.round(finalColor.getBlue() + bStep);

            if (red > 255) red = 255; if (red < 0) red = 0;
            if (green > 255) green = 255; if (green < 0) green = 0;
            if (blue > 255) blue = 255; if (blue < 0) blue = 0;

            finalColor = new Color(red, green, blue);
            String hex = "#" + Integer.toHexString(finalColor.getRGB()).substring(2);
            String formats = "";
            if (bold) formats += ChatColor.BOLD;
            if (italic) formats += ChatColor.ITALIC;
            if (underlined) formats += ChatColor.UNDERLINE;
            if (strikethrough) formats += ChatColor.STRIKETHROUGH;
            if (magic) formats += ChatColor.MAGIC;
            msg = msg.replaceFirst("<\\$>", "" + ChatColor.of(hex) + formats);
        }
        return msg;
    }

}

