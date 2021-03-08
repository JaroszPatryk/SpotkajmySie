package pjarosz;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Hour implements Comparable<Hour> {

    LocalDateTime start;
    LocalDateTime end;

    public void setStart(String start) {
        this.start = changeStringToLocalDateTime(start);
    }

    public void setEnd(String end) {
        this.end = changeStringToLocalDateTime(end);
    }

    private LocalDateTime changeStringToLocalDateTime(String start) {
        String[] split = start.split(":");
        return LocalDateTime.of(2021, 3, 7, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public int compareTo(Hour o) {
        if (this.start.isBefore(o.getStart())) {
            return -1;
        }
        if (this.start.isAfter(o.getStart())) {
            return 1;
        } else {
            return 0;
        }
    }
}
