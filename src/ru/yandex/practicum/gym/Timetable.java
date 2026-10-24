package ru.yandex.practicum.gym;

import java.util.*;

public class Timetable {

    private final Map<DayOfWeek, Map<TimeOfDay, List<TrainingSession>>> timetable = new HashMap<>();

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

        Map<TimeOfDay, List<TrainingSession>> sessionByTime = timetable.get(dayOfWeek);
        if (sessionByTime == null) {
            sessionByTime = new TreeMap<>();
            timetable.put(dayOfWeek, sessionByTime);
        }

        List<TrainingSession> sessions = sessionByTime.get(timeOfDay);
        if (sessions == null) {
            sessions = new ArrayList<>();
            sessionByTime.put(timeOfDay, sessions);
        }

        sessions.add(trainingSession);
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
        Map<Coach, Integer> coaches = new HashMap<>();
        for (Map<TimeOfDay, List<TrainingSession>> sessionsByTime : timetable.values()) {
            for (List<TrainingSession> sessions : sessionsByTime.values()) {
                for (TrainingSession session : sessions) {
                    Coach coach = session.getCoach();
                    Integer count = coaches.get(coach);
                    if (count == null) {
                        coaches.put(coach, 1);
                    } else {
                        coaches.put(coach, count + 1);
                    }
                }
            }
        }
        List<CounterOfTrainings> counter = new ArrayList<>();
        for (Map.Entry<Coach, Integer> entry : coaches.entrySet()) {
            counter.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));
        }
        Collections.sort(counter);
        return counter;
    }
}
