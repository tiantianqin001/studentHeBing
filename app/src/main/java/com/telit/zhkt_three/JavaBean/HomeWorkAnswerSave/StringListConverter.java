package com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * greendao 保存集合
 */
public class StringListConverter implements PropertyConverter<List<String>, String> {

    private final Gson mGson;

    public StringListConverter() {
        mGson = new Gson();
    }

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> list = mGson.fromJson(databaseValue, type);
        return list;
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        String dbString = mGson.toJson(entityProperty);
        return dbString;
    }


}
 