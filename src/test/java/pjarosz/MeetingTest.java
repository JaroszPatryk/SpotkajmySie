package pjarosz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MeetingTest {

    @Test
    void shouldReturnFreeTimePeriodsIncludingMeetingDuration() {
        //given
        String json1 = "{ \"working_hours\" : { \"start\" : \"09:00\", \"end\" : \"19:55\" }, \"planned_meeting\" : [ { \"start\" : \"09:00\", \"end\" : \"10:30\" }, { \"start\" : \"12:00\", \"end\" : \"13:00\" }, { \"start\" : \"16:00\", \"end\" : \"18:00\" } ] }";
        String json2 = "{ \"working_hours\" : { \"start\" : \"10:00\", \"end\" : \"18:30\" }, \"planned_meeting\" : [ { \"start\" : \"10:00\", \"end\" : \"11:30\" }, { \"start\" : \"12:30\", \"end\" : \"14:30\" }, { \"start\" : \"14:30\", \"end\" : \"15:00\" }, { \"start\" : \"16:00\", \"end\" : \"17:00\" } ] }";
        String meetingDurationString = "00:30";
        String expectingResult = "[[\"11:30\", \"12:00\"], [\"15:00\", \"16:00\"], [\"18:00\", \"18:30\"]]";
        //when
        String meetingHours = Meeting.getMeetingHours(json1, json2, meetingDurationString);
        //then
        Assertions.assertEquals(expectingResult, meetingHours);
    }
}
