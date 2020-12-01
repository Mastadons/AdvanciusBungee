package net.advancius.statistic;

import lombok.Data;
import net.advancius.person.Person;

import java.util.List;
import java.util.UUID;

@Data
public class Statistic<T> {

    private final StatisticName name;
    private final Class<T> statisticClass;
    private final List<StatisticScore> scores;

    public StatisticScore getScore(UUID id) {
        for (StatisticScore score : scores) if (score.getId().equals(id)) return score;
        return null;
    }

    public StatisticScore getScore(Person person) {
        return getScore(person.getId());
    }

    public boolean hasScore(UUID id) {
        return getScore(id) != null;
    }

    public StatisticScore getScoreOrDefault(UUID id, T score) {
        return hasScore(id) ? getScore(id) : setScore(id, score);
    }

    public StatisticScore setScore(UUID id, T score) {
        StatisticScore statisticScore = new StatisticScore(id, score);
        scores.add(statisticScore);
        return statisticScore;
    }
}
