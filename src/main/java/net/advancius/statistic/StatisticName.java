package net.advancius.statistic;

import lombok.Data;

@Data
public class StatisticName {

    private final String namespace;
    private final String name;

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}
