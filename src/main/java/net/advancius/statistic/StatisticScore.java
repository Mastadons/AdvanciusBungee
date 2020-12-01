package net.advancius.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.advancius.AdvanciusBungee;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StatisticScore {

    private final UUID id;
    private Object score;

    public <T> T getScore(Class<T> scoreClass) {
        score = AdvanciusBungee.GSON.fromJson(AdvanciusBungee.GSON.toJson(score), scoreClass);
        return (T) score;
    }

    public void setScore(Object score) {
        this.score = score;
    }
}
