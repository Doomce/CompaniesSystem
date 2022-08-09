package net.dom.companies.database;

import com.google.gson.Gson;
import org.bukkit.Location;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class locationConverter implements AttributeConverter<Location, String> {

    @Override
    public String convertToDatabaseColumn(Location attribute) {
        if (attribute == null) return null;
        return new Gson().toJson(attribute);
    }

    @Override
    public Location convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return new Gson().fromJson(dbData, Location.class);
    }
}
