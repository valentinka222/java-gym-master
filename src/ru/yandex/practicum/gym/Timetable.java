package ru.yandex.practicum.gym;

import java.util.*;

public class Timetable {

    private final Map<DayOfWeek, Map<TimeOfDay, List<TrainingSession>>> timetable = new HashMap<>();
    private final Map<Coach, Integer> coachesCounter = new HashMap<>();

    public void addNewTrainingSession(TrainingSession trainingSession) {
        if (trainingSession == null
                || trainingSession.getGroup() == null
                || trainingSession.getCoach() == null
                || trainingSession.getDayOfWeek() == null
                || trainingSession.getTimeOfDay() == null) {
            return;
        }
        DayOfWeek dayOfWeek = trainingSession.getDayOfWeek();
        TimeOfDay timeOfDay = trainingSession.getTimeOfDay();

        Map<TimeOfDay, List<TrainingSession>> sessionByTime = timetable.computeIfAbsent(dayOfWeek,
                k -> new TreeMap<>());

        List<TrainingSession> sessions = sessionByTime.computeIfAbsent(timeOfDay, k -> new ArrayList<>());

        sessions.add(trainingSession);
        Coach coach = trainingSession.getCoach();
        coachesCounter.put(coach, coachesCounter.getOrDefault(coach, 0) + 1);
    }

    public Map<TimeOfDay, List<TrainingSession>> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        Map<TimeOfDay, List<TrainingSession>> sessionByTime = timetable.get(dayOfWeek);
        if (sessionByTime == null) {
            return new TreeMap<>();
        }
        return sessionByTime;
    }

    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        if (timeOfDay == null) {
            return new ArrayList<>();
        }
        Map<TimeOfDay, List<TrainingSession>> sessionByTime = timetable.get(dayOfWeek);
        if (sessionByTime == null) {
            return new ArrayList<>();
        }
        List<TrainingSession> sessions = sessionByTime.get(timeOfDay);
        if (sessions == null) {
            return new ArrayList<>();
        }
        return sessions;
    }

    public List<CounterOfTrainings> getCountByCoaches() {
        List<CounterOfTrainings> counter = new ArrayList<>();
        for (Map.Entry<Coach, Integer> entry : coachesCounter.entrySet()) {
            counter.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));
        }
        Collections.sort(counter);
        return counter;
    }
}
