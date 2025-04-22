package com.getindata.flink.sessionizer.typing;

import com.getindata.flink.sessionizer.model.Session;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

public class SessionsTypeInfoFactory extends ListTypeInfoFactory<Session> {

    @Override
    protected TypeInformation<Session> elementsType() {
        return Types.POJO(Session.class);
    }

}

