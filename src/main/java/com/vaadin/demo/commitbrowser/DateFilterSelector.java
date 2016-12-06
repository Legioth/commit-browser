package com.vaadin.demo.commitbrowser;

import java.time.LocalDate;

import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

public class DateFilterSelector extends HorizontalLayout {
    private final DateField startDate = new DateField();
    private final DateField endDate = new DateField();

    public DateFilterSelector() {
        setSpacing(true);

        startDate.addStyleName(ValoTheme.DATEFIELD_SMALL);
        endDate.addStyleName(ValoTheme.DATEFIELD_SMALL);
        startDate.setWidth(120, Unit.PIXELS);
        endDate.setWidth(120, Unit.PIXELS);

        Label dash = new Label("-");
        dash.setSizeUndefined();

        addComponents(startDate, dash, endDate);

        setComponentAlignment(dash, Alignment.MIDDLE_CENTER);
    }

    public LocalDate getStartDate() {
        return startDate.getValue();
    }

    public LocalDate getEndDate() {
        return endDate.getValue();
    }

    public Registration addValueChangeListener(
            ValueChangeListener<LocalDate> listener) {
        Registration r1 = startDate.addValueChangeListener(listener);
        Registration r2 = endDate.addValueChangeListener(listener);

        return () -> {
            r1.remove();
            r2.remove();
        };
    }
}
