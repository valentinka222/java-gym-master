package ru.yandex.practicum.gym;

public class CounterOfTrainings implements Comparable<CounterOfTrainings> {
    private final Coach coach;
    private final int countOfTrainings;

    public CounterOfTrainings(Coach coach, int countOfTrainings) {
        this.coach = coach;
        this.countOfTrainings = countOfTrainings;
    }

    @Override
    public int compareTo(CounterOfTrainings o) {
        return Integer.compare(o.countOfTrainings, this.countOfTrainings);
    }

    public Coach getCoach() {
        return coach;
    }

    public int getCountOfTrainings() {
        return countOfTrainings;
    }
}
