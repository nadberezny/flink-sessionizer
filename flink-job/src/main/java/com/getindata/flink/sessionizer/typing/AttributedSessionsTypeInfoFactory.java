package com.getindata.flink.sessionizer.typing;

import com.getindata.flink.sessionizer.model.AttributedSession;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

public class AttributedSessionsTypeInfoFactory extends ListTypeInfoFactory<AttributedSession> {

    @Override
    protected TypeInformation<AttributedSession> elementsType() {
        return Types.POJO(AttributedSession.class);
    }

}
