package com.vaadin.demo.commitbrowser;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;

final class DateFilter implements Filter {
    private Date comparisonDate;
    private boolean startDate;

    DateFilter(Date date, boolean startDate) {
        this.startDate = startDate;
        if (date != null) {
            comparisonDate = resetDate(date, startDate);
        } else {
            comparisonDate = date;
        }
    }

    private Date resetDate(Date date, boolean startDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (startDate) {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        } else {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MILLISECOND, 999);
        }
        return c.getTime();
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException {

        if (comparisonDate == null) {
            return true;
        }

        Date current = ((Commit) itemId).getTimestamp();
        boolean after;
        if (current.after(comparisonDate)) {
            after = true;
        } else {
            after = false;
        }
        if (after && startDate) {
            return true;
        }
        if (!after && !startDate) {
            return true;
        }
        return false;

    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        if (propertyId.equals("timestamp")) {
            return true;
        } else {
            return false;
        }
    }
}