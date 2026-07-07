package se.lilja.sgiguard.services;


import de.focus_shift.jollyday.core.HolidayCalendar;
import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;
import org.springframework.stereotype.Service;

@Service
public class HolidayService {

    private final HolidayManager holidayManager;

    public HolidayService() {
        this.holidayManager = HolidayManager.getInstance(
                ManagerParameters.create(HolidayCalendar.SWEDEN));
    }
}
