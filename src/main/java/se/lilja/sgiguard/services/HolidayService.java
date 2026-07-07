package se.lilja.sgiguard.services;


import de.focus_shift.jollyday.core.HolidayCalendar;
import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class HolidayService {

    private final HolidayManager holidayManager;

    public HolidayService() {
        this.holidayManager = HolidayManager.getInstance(
                ManagerParameters.create(HolidayCalendar.SWEDEN));
    }

    // Check if a certain date is a swedish holiday
    public boolean isHoliday(LocalDate date) {
        return holidayManager.isHoliday(date);
    }
}
