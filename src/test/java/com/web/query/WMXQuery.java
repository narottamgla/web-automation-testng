package com.web.query;

import lombok.Getter;

public enum WMXQuery {

    GET_USER_ADDRESS(
          "select * from user.user_address where  user_id = 'USER_ID' ");

    @Getter
    private String statement;

    WMXQuery(final String statement) {
        this.statement = statement;
    }
}

