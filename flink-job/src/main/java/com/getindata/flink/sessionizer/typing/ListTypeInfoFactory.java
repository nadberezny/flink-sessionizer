package com.getindata.flink.sessionizer.typing;

import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class ListTypeInfoFactory<T> extends TypeInfoFactory<List<T>> {

    @Override
    public TypeInformation<List<T>> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {
        return Types.LIST(elementsType());
    }

    protected abstract TypeInformation<T> elementsType();
}
