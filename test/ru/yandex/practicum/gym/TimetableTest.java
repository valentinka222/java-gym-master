package ru.yandex.practicum.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimetableTest {
    private Timetable timetable;
    private Group childGroup;
    private Coach coach;

    private TrainingSession createSession(DayOfWeek day, int hours, int minutes) {
        return new TrainingSession(childGroup, coach, day, new TimeOfDay(hours, minutes));
    }

    @BeforeEach
    public void BeforeEach() {
        timetable = new Timetable();
        childGroup = new Group("Акробатика для детей", Age.CHILD, 60);
        coach = new Coach("Васильев", "Николай", "Сергеевич");
    }

    @Test
    @DisplayName("Одна тренировка: возвращается за свой день, другой - пуст")
    void testGetTrainingSessionsForDaySingleSession() {
        TrainingSession singleTrainingSession = new TrainingSession(childGroup, coach, DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).size());
    }

    @Test
    @DisplayName("Несколько тренировок: за день возвращаются в порядке времени начала")
    void testGetTrainingSessionsForDayMultipleSessions() {
        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach, DayOfWeek.THURSDAY, new TimeOfDay(20, 0));
        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        TrainingSession mondayChildTrainingSession = createSession(DayOfWeek.MONDAY, 13, 0);
        TrainingSession thursdayChildTrainingSession = createSession(DayOfWeek.THURSDAY, 13, 0);
        TrainingSession saturdayChildTrainingSession = createSession(DayOfWeek.SATURDAY, 10, 0);
        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.SATURDAY).size());

        Map<TimeOfDay, List<TrainingSession>> thursday = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        assertEquals(2, thursday.size());

        List<TimeOfDay> times = new ArrayList<>(thursday.keySet());
        assertEquals(13, times.get(0).getHours());
        assertEquals(0, times.get(0).getMinutes());
        assertEquals(20, times.get(1).getHours());
        assertEquals(0, times.get(1).getMinutes());

        assertEquals(thursdayChildTrainingSession, thursday.get(times.get(0)).getFirst());
        assertEquals(thursdayAdultTrainingSession, thursday.get(times.get(1)).getFirst());

        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).size());
    }

    @Test
    @DisplayName("Поиск по дню и времени: находит тренировку, для другого времени пусто")
    void testGetTrainingSessionsForDayAndTime() {
        TrainingSession singleTrainingSession = createSession(DayOfWeek.MONDAY, 13, 0);
        timetable.addNewTrainingSession(singleTrainingSession);

        TimeOfDay time13 = new TimeOfDay(13, 0);
        assertEquals(1, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, time13).size());
        assertEquals(singleTrainingSession, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, time13).getFirst());

        TimeOfDay time14 = new TimeOfDay(14, 0);
        assertEquals(0, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, time14).size());
    }

    @Test
    @DisplayName("Несколько тренировок в одно время")
    void testGetTrainingSessionsForDayAndTimeMultipleSessionsAtSameTime() {
        TimeOfDay time = new TimeOfDay(18, 0);

        Group stretching = new Group("Растяжка для детей", Age.CHILD, 70);

        TrainingSession first = new TrainingSession(childGroup, coach, DayOfWeek.MONDAY, time);
        TrainingSession second = new TrainingSession(stretching, coach, DayOfWeek.MONDAY, time);
        timetable.addNewTrainingSession(first);
        timetable.addNewTrainingSession(second);

        assertEquals(2, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, time).size());
        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());

        assertEquals(first, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, time).get(0));
        assertEquals(second, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, time).get(1));

    }

    @Test
    @DisplayName("Тренировки сортируются по времени")
    void testTrainingSessionsSortedByTimeWhenAddedInRandomOrder() {
        TrainingSession first = createSession(DayOfWeek.MONDAY, 18, 0);
        TrainingSession second = createSession(DayOfWeek.MONDAY, 9, 0);
        TrainingSession third = createSession(DayOfWeek.MONDAY, 12, 30);
        TrainingSession fourth = createSession(DayOfWeek.MONDAY, 12, 0);

        timetable.addNewTrainingSession(first);
        timetable.addNewTrainingSession(second);
        timetable.addNewTrainingSession(third);
        timetable.addNewTrainingSession(fourth);
        assertEquals(4, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());

        List<TimeOfDay> times = new ArrayList<>(timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).keySet());

        assertEquals(9, times.get(0).getHours());
        assertEquals(0, times.get(0).getMinutes());
        assertEquals(12, times.get(1).getHours());
        assertEquals(0, times.get(1).getMinutes());
        assertEquals(12, times.get(2).getHours());
        assertEquals(30, times.get(2).getMinutes());
        assertEquals(18, times.get(3).getHours());
        assertEquals(0, times.get(3).getMinutes());

    }

    @Test
    @DisplayName("Битая тренировка не добавляется")
    void testInvalidTrainingSessionsAreNotAdded() {
        TrainingSession first = new TrainingSession(childGroup, null, DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        TrainingSession second = new TrainingSession(childGroup, coach, DayOfWeek.MONDAY, null);

        timetable.addNewTrainingSession(first);
        timetable.addNewTrainingSession(second);
        timetable.addNewTrainingSession(null);

        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
    }

    @Test
    @DisplayName("Тренеры отсортированы по количеству тренировок по убыванию")
    void testGetCountByCoachesSortedDescending() {
        Coach coachA = new Coach("A", "A", "A");
        Coach coachB = new Coach("B", "B", "B");
        Coach coachC = new Coach("C", "C", "C");

        TrainingSession t1 = new TrainingSession(childGroup, coachA, DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession t2 = new TrainingSession(childGroup, coachA, DayOfWeek.THURSDAY, new TimeOfDay(14, 0));
        TrainingSession t3 = new TrainingSession(childGroup, coachA, DayOfWeek.TUESDAY, new TimeOfDay(15, 0));

        TrainingSession t4 = new TrainingSession(childGroup, coachB, DayOfWeek.MONDAY, new TimeOfDay(16, 0));
        TrainingSession t5 = new TrainingSession(childGroup, coachB, DayOfWeek.WEDNESDAY, new TimeOfDay(17, 0));

        TrainingSession t6 = new TrainingSession(childGroup, coachC, DayOfWeek.FRIDAY, new TimeOfDay(18, 0));

        timetable.addNewTrainingSession(t3);
        timetable.addNewTrainingSession(t6);
        timetable.addNewTrainingSession(t2);
        timetable.addNewTrainingSession(t5);
        timetable.addNewTrainingSession(t1);
        timetable.addNewTrainingSession(t4);

        List<CounterOfTrainings> result = timetable.getCountByCoaches();

        assertEquals(3, result.get(0).getCountOfTrainings());
        assertEquals(coachA, result.get(0).getCoach());
        assertEquals(2, result.get(1).getCountOfTrainings());
        assertEquals(coachB, result.get(1).getCoach());
        assertEquals(1, result.get(2).getCountOfTrainings());
        assertEquals(coachC, result.get(2).getCoach());

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Тренер с одинаковым ФИО - один человек")
    void testGetCountByCoachesSameCoachCreatedTwice() {
        Group group1 = new Group("Тренировка 1", Age.CHILD, 50);
        Group group2 = new Group("Тренировка 2", Age.ADULT, 60);

        Coach coach1 = new Coach("Васильев", "Николай", "Сергеевич");
        Coach coach2 = new Coach("Васильев", "Николай", "Сергеевич");

        TrainingSession t1 = new TrainingSession(group1, coach1, DayOfWeek.MONDAY, new TimeOfDay(18, 0));
        TrainingSession t2 = new TrainingSession(group2, coach2, DayOfWeek.MONDAY, new TimeOfDay(18, 0));
        timetable.addNewTrainingSession(t1);
        timetable.addNewTrainingSession(t2);

        assertEquals(1, timetable.getCountByCoaches().size());
        assertEquals(2, timetable.getCountByCoaches().getFirst().getCountOfTrainings());
    }

    @Test
    @DisplayName("Учитываются все тренировки тренера во всем расписании")
    void testGetCountByCoachesCountsAllSessionsOfCoach() {
        Group group1 = new Group("Тренировка 1", Age.CHILD, 50);
        Group group2 = new Group("Тренировка 2", Age.ADULT, 60);
        Group group3 = new Group("Тренировка 3", Age.ADULT, 70);
        Group group4 = new Group("Тренировка 4", Age.CHILD, 40);

        TrainingSession t1 = new TrainingSession(group1, coach, DayOfWeek.MONDAY, new TimeOfDay(10, 0));
        TrainingSession t2 = new TrainingSession(group2, coach, DayOfWeek.MONDAY, new TimeOfDay(18, 0));
        TrainingSession t3 = new TrainingSession(group3, coach, DayOfWeek.SUNDAY, new TimeOfDay(10, 0));
        TrainingSession t4 = new TrainingSession(group4, coach, DayOfWeek.SUNDAY, new TimeOfDay(10, 0));
        timetable.addNewTrainingSession(t1);
        timetable.addNewTrainingSession(t2);
        timetable.addNewTrainingSession(t3);
        timetable.addNewTrainingSession(t4);

        assertEquals(1, timetable.getCountByCoaches().size());
        assertEquals(4, timetable.getCountByCoaches().getFirst().getCountOfTrainings());
    }
}
