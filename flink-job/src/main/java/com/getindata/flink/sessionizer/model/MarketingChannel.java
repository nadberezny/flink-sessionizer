package com.getindata.flink.sessionizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class MarketingChannel {

    private String name;

    public boolean isIntersecting(MarketingChannel other) {
        return Objects.equals(this, other) || Objects.equals(this.name, other.name);
    }
}
