package net.dom.companies.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class debtConverter implements AttributeConverter<List<Double>, String> {

    @Override
    public String convertToDatabaseColumn(List<Double> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return new Gson().toJson(attribute);
    }

    @Override
    public List<Double> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return new ArrayList<>();
        return new Gson().fromJson(dbData, new TypeToken<ArrayList<Double>>() {}.getType());
    }
}
