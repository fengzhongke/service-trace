package org.lang.sandbox.io;


import com.google.gson.Gson;

import java.util.Arrays;

/**
 * @author nkhanlang@163.com
 */
public class ObjectSerializer {


    public static String getJsonStrValue(Object obj) {
        return new Gson().toJson(obj);
//        return getStrValue(obj);
    }


    public static String getStrValue(Object obj) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        try {
//            return mapper.writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return JSON.toJSONString(obj);
        //return new Gson().toJson(obj);
//        return JSONObject.valueToString(obj);
        String str = null;
        if(obj != null){
            if(obj.getClass().isArray()){
                String spliter = ",";
                StringBuilder sb = new StringBuilder("[");
                Object[] array = (Object[])obj;
                for(int i=0; i<array.length; i++){
                    sb.append(spliter);
                    spliter = ",";
                    sb.append(getStrValue(array[i]));
                }
                sb.append("]");
                str = sb.toString();
            }else{
                str = String.valueOf(obj);
            }
        }
        return str;
    }
}
