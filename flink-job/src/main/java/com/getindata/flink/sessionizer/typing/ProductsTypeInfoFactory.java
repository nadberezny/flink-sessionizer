package com.getindata.flink.sessionizer.typing;

import com.getindata.flink.sessionizer.model.event.Product;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

public class ProductsTypeInfoFactory extends ListTypeInfoFactory<Product> {

    @Override
    protected TypeInformation<Product> elementsType() {
        return Types.POJO(Product.class);
    }
}
