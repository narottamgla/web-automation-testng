package com.web.utils;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class JSON {

  private static JSONObject readFile(final String filepath) throws Exception {
    JSONParser jsonParser = new JSONParser();
    try (FileReader reader = new FileReader(filepath)) {
      Object obj = jsonParser.parse(reader);
      return (JSONObject) obj;
    } catch (ParseException | IOException exception) {
      exception.printStackTrace();
      throw exception;
    }
  }

  private static String readStringFile(final String filepath) throws Exception {
    try {
      String content = FileUtils.readFileToString(new File(filepath));
      return content;
    } catch (IOException exception) {
      exception.printStackTrace();
      throw exception;
    }
  }
  public static JSONObject parseString(final String str) {
    JSONParser parser = new JSONParser();
    JSONObject json = null;
    try {
      json = (JSONObject) parser.parse(str);
      for (Object key : json.keySet()) {
        if (json.get(key) != null && json.get(key).toString().contains("{")) {
          json.put(key, parseString(json.get(key).toString()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return json;
  }

  public static JSONObject parseStringWithArray(final String str) {
    JSONParser parser = new JSONParser();
    JSONObject json = null;
    try {
      json = (JSONObject) parser.parse(str);
      for (Object key : json.keySet()) {
        if (json.get(key).toString().startsWith("{")) {
          json.put(key, parseStringWithArray(json.get(key).toString()));
        } else if (json.get(key).toString().startsWith("[")) {
          json.put(key, parser.parse(json.get(key).toString()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return json;
  }


  private static JSONArray readJsonArrayFromFile(final String filepath) throws Exception {
    FileReader reader = new FileReader(filepath);
    JSONParser jsonParser = new JSONParser();
    Object obj = jsonParser.parse(reader);
    JSONArray arrayData = (JSONArray) obj;
    return arrayData;
  }


  public static <K, V> String buildRequest(final String filepath)
      throws Exception {
    Object object = new JSONParser()
        .parse(new FileReader(filepath));
    if (object instanceof JSONObject) {
      return ((JSONObject) object).toJSONString();
    } else if (object instanceof JSONArray) {
      return ((JSONArray) object).toJSONString();
    }
    return null;
  }
}
