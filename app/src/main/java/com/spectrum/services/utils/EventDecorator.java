package com.spectrum.services.utils;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Created by Abins Shaji on 02/02/18.
 */
public class EventDecorator implements DayViewDecorator {


    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return Utils.getDay(day.getCalendar().getTimeInMillis()).equalsIgnoreCase("Fri");
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.addSpan(new DotSpan(5, color));
        view.setDaysDisabled(true);

    }
}