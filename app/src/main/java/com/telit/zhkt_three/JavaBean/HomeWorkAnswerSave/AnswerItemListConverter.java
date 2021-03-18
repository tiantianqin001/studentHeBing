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
public class AnswerItemListConverter implements PropertyConverter<List<AnswerItem>, String> {

    private final Gson mGson;

    public AnswerItemListConverter() {
        mGson = new Gson();
    }

    @Override
    public List<AnswerItem> convertToEntityProperty(String databaseValue) {
        Type type = new TypeToken<ArrayList<AnswerItem>>() {
        }.getType();
        ArrayList<AnswerItem> list = mGson.fromJson(databaseValue, type);
        return list;
    }

    @Override
    public String convertToDatabaseValue(List<AnswerItem> entityProperty) {
        String dbString = mGson.toJson(entityProperty);
        return dbString;
    }


}
 