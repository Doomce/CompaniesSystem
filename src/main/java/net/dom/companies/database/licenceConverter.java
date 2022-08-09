package net.dom.companies.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.protobuf.Type;
import net.dom.companies.functions.licencesManager;
import org.bukkit.Location;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Converter
public class licenceConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return new Gson().toJson(attribute);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return new ArrayList<>();
        return new Gson().fromJson(dbData, new TypeToken<ArrayList<Integer>>() {}.getType());
    }
}
