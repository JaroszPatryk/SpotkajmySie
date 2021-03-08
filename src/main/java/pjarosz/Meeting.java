package pjarosz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Meeting {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getMeetingHours(String json1, String json2, String plannedMeetingDuration) {

        int meetingDurationMinutes = getMeetingDurationMinutes(plannedMeetingDuration);

        Calendar calendar1 = new Calendar();
        Calendar calendar2 = new Calendar();

        try {
            calendar1 = objectMapper.readValue(json1, Calendar.class);
            calendar2 = objectMapper.readValue(json2, Calendar.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<LocalDateTime> startWork = List.of(calendar1.getWorking_hours().start, calendar2.getWorking_hours().start);
        List<LocalDateTime> endWork = List.of(calendar1.getWorking_hours().end, calendar2.getWorking_hours().end);

        LocalDateTime startWorkingDay = Collections.max(startWork);
        LocalDateTime endWorkingDay = Collections.min(endWork);

        Set<Hour> hourSet = new TreeSet<>(calendar1.getPlanned_meeting());

        hourSet.addAll(calendar2.getPlanned_meeting());


        List<Hour> freeTimesPeriods = getFreeTimePeriods(hourSet, startWorkingDay, endWorkingDay);

        List<Hour> freeTimePeriodsIncludingMeetingDuration = getFreeTimePeriodsIncludingMeetingDuration(meetingDurationMinutes, freeTimesPeriods);

        return getOutputString(freeTimePeriodsIncludingMeetingDuration);
    }

    private static List<Hour> getFreeTimePeriods(Set<Hour> hourSet, LocalDateTime startWorkingDay, LocalDateTime endWorkingDay) {
        List<Hour> hourList = new ArrayList<>(hourSet);
        deleteRangeWhenAnotherRangeIncludesIt(hourList);

        List<Hour> freeTimePeriods = new ArrayList<>();

        LocalDateTime startRange = null;
        LocalDateTime endRange = null;


        for (int i = 0; i < hourList.size(); i++) {

            if (i == 0 && startWorkingDay.isBefore(hourList.get(i + 1).getStart()) && hourList.get(i).getEnd().isAfter(hourList.get(i + 1).getEnd())) {
                freeTimePeriods.add(new Hour(startWorkingDay, hourList.get(i).getStart()));
            }
            if (startRange == null) {
                startRange = hourList.get(i).getStart();
                endRange = hourList.get(i).getEnd();
            }
            if (i == hourList.size() - 1 && endRange.isBefore(endWorkingDay)) {
                Hour h = new Hour(endRange, endWorkingDay);
                freeTimePeriods.add(h);
            } else {
                if (endRange.isBefore(hourList.get(i + 1).getStart())) {
                    Hour h = new Hour(hourList.get(i).getEnd(), hourList.get(i + 1).getStart());
                    freeTimePeriods.add(h);
                    startRange = null;
                    endRange = null;
                } else {
                    if (endRange.isBefore(hourList.get(i + 1).getEnd())) {
                        endRange = hourList.get(i + 1).getEnd();
                    }
                }
            }
        }
        return freeTimePeriods;
    }


    private static List<Hour> getFreeTimePeriodsIncludingMeetingDuration(int meetingDurationMinutes, List<Hour> freeTimesPeriods) {
        List<Hour> collect = freeTimesPeriods.stream()
                .filter(hour -> {
                    long freeTimeDuration = Duration.between(hour.start, hour.end).toMinutes();
                    return freeTimeDuration >= meetingDurationMinutes;
                })
                .collect(Collectors.toList());
        return collect;
    }

    private static int getMeetingDurationMinutes(String plannedMeetingDuration) {
        String[] split = plannedMeetingDuration.split(":");
        return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
    }

    private static String getOutputString(List<Hour> collect) {
        StringBuilder outputString = new StringBuilder("[");

        for (int i = 0; i < collect.size(); i++) {
            outputString.append(getOutputStringFormat(collect.get(i)));
            if (i == collect.size() - 1) {
                outputString.append("]");
            } else {
                outputString.append(", ");
            }
        }

        return outputString.toString();
    }

    private static String getOutputStringFormat(Hour h) {
        return "[\"" + addZeroBeforePartOfDate(h.getStart().getHour()) + ":" +
                addZeroBeforePartOfDate(h.getStart().getMinute()) + "\", \"" +
                addZeroBeforePartOfDate(h.getEnd().getHour()) + ":" +
                addZeroBeforePartOfDate(h.getEnd().getMinute()) + "\"]";
    }

    private static String addZeroBeforePartOfDate(int datePart) {
        String datePartString = datePart + "";
        if (datePart < 10) {
            datePartString = "0" + datePartString;
        }
        return datePartString;
    }

    public static void deleteRangeWhenAnotherRangeIncludesIt(List<Hour> hours) {
        for (int i = 0; i < hours.size() - 1; i++) {
            for (int j = i + 1; j < hours.size(); j++) {
                if (hours.get(i).getStart().isBefore(hours.get(j).getStart()) &&
                        hours.get(i).getEnd().isAfter(hours.get(j).getEnd())) {
                    hours.remove(j);
                    break;
                }

            }
        }
    }

}

