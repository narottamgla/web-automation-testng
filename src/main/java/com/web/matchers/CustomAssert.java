package com.web.matchers;

import com.web.executiondata.GlobalData;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;

import javax.script.ScriptException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CustomAssert {

    public static void validateResponsePath(final Matcher matcher, final String key, String value, final JsonPath finalJsonPath) {
        try {
            switch (matcher) {
                case equalTo:
                    assertThat(key, finalJsonPath.get(key), equalTo(parseValue(value)));
                    break;
                case notNull:
                    assertThat(key, finalJsonPath.get(key), is(notNullValue()));
                    break;
                case contains:
                    assertThat(key, finalJsonPath.get(key), contains(parseValue(value)));
                    break;
                case hasSize:
                    assertThat(
                            key, finalJsonPath.get(key), hasSize(Integer.parseInt(value)));
                    break;
                case hasItem:
                    assertThat(key, finalJsonPath.get(key), hasItem(parseValue(value)));
                    break;
                case isBlank:
                    assertThat(key, finalJsonPath.get(key), is(blankString()));
                    break;
                case containsInAnyOrder:
                    assertThat(
                            key,
                            finalJsonPath.get(key),
                            containsInAnyOrder(((ArrayList) parseValue(value)).toArray()));
                    break;
                case containsString:
                    assertThat(
                            key,
                            finalJsonPath.getString(key),
                            containsString(value));
                    break;
                case anyOf:
                    assertThat(key, finalJsonPath.get(key), AnyOf.anyOf(
                            Arrays.stream(value.split(",")).map(Matchers::is)
                                    .collect(Collectors.toList())));
                    break;
                default:
                    break;
            }
        } catch (ScriptException sce) {
            throw new RuntimeException(sce.getMessage());
        }
    }

    private static Object parseValue(final String value) throws ScriptException {
        if (value == null) {
            return null;
        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        } else if (value.contains("STR:")){
            return value.split(":")[1];
        }
        else if (Integer.valueOf(value) instanceof Integer) {
            return Integer.parseInt(value);
        } else if (Long.valueOf(value) instanceof Long) {
            return Long.parseLong(value);
        } else if (Float.valueOf(value) instanceof Float) {
            return Float.parseFloat(value);
        } else if (Double.valueOf(value) instanceof Double) {
            return Double.parseDouble(value);
        } else if (String.valueOf(value) instanceof String) {
            return value;
        }
        return value;
    }
}
