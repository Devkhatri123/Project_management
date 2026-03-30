package com.project_management.project_management.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TaskUtil {
    public static String formatTaskTime(Instant time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy hh:mm a")
                .withZone(ZoneOffset.UTC);
        return formatter.format(ZonedDateTime.ofInstant(time, ZoneOffset.UTC));
    }
}
