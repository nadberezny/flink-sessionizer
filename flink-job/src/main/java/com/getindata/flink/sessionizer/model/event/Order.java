package com.getindata.flink.sessionizer.model.event;

import com.getindata.flink.sessionizer.typing.ProductsTypeInfoFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String id;
    private long timestamp;
    private float total;
    private float shipping;
    private Long returnedTimestamp;
    @TypeInfo(ProductsTypeInfoFactory.class)
    private List<Product> products;
}
