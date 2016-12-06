package com.vaadin.demo.commitbrowser;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.DataProvider;
import com.vaadin.server.data.ListDataProvider;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.ItemClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ProgressBarRenderer;
import com.vaadin.ui.themes.ValoTheme;

import elemental.json.JsonValue;

/**
 *
 */
@Theme("tests-valo")
@CDIUI("")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class MyUI extends UI {

    @Inject
    GitRepositoryService gitRepositoryService;
    private Grid<Commit> grid = new Grid<>();

    private static LinkedHashMap<String, String> themeVariants = new LinkedHashMap<String, String>();

    private static class MinimalSizeHtmlRenderer extends HtmlRenderer {
        @Override
        public JsonValue encode(String value) {
            return super.encode(
                    "<div style=';pointer-events: none'><div style='width:10px;overflow-x:visible;pointer-events: none'>"
                            + value + "</div></div>");
        }
    }

    static {
        themeVariants.put("tests-valo", "Default");
        themeVariants.put("tests-valo-blueprint", "Blueprint");
        themeVariants.put("tests-valo-dark", "Dark");
        themeVariants.put("tests-valo-facebook", "Facebook");
        themeVariants.put("tests-valo-flatdark", "Flat dark");
        themeVariants.put("tests-valo-flat", "Flat");
        themeVariants.put("tests-valo-light", "Light");
        themeVariants.put("tests-valo-metro", "Metro");
    }

    private final TextField nameFilter = createFilterTextField();
    private final TextField topicFilter = createFilterTextField();
    private final DateFilterSelector dateFilter = new DateFilterSelector();

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        boolean isEmbedded = "embedded"
                .equals(vaadinRequest.getParameter("type"));
        VerticalLayout layout = new VerticalLayout();
        layout.setId("rootLayout");
        layout.setMargin(!isEmbedded);
        layout.setSpacing(!isEmbedded);

        // Add theme selector
        if (!isEmbedded) {
            Component themeSelector = buildThemeSelector();
            layout.addComponent(themeSelector);
            layout.setComponentAlignment(themeSelector, Alignment.TOP_RIGHT);
        }

        ListDataProvider<Commit> dataProvider = DataProvider
                .create(gitRepositoryService.findAll());

        grid.setDataProvider(dataProvider.setFilter(item -> {
            if (!nameFilter.isEmpty() && !item.getFullName().toLowerCase()
                    .contains(nameFilter.getValue().toLowerCase())) {
                return false;
            }

            if (!topicFilter.isEmpty() && !item.getFullTopic().toLowerCase()
                    .contains(topicFilter.getValue().toLowerCase())) {
                return false;
            }

            LocalDate startDate = dateFilter.getStartDate();
            if (startDate != null
                    && item.getTimestamp().before(localDateToDate(startDate))) {
                return false;
            }

            LocalDate endDate = dateFilter.getEndDate();
            if (endDate != null
                    && item.getTimestamp().after(localDateToDate(endDate))) {
                return false;
            }

            return true;
        }));

        Column<Commit, String> nameColumn = grid
                .addColumn(Commit::getFullName, new MinimalSizeHtmlRenderer())
                .setCaption("Full Name").setExpandRatio(1);
        Column<Commit, String> topicColumn = grid
                .addColumn(Commit::getFullTopic, new MinimalSizeHtmlRenderer())
                .setCaption("Full Topic").setExpandRatio(2);

        grid.addColumn(Commit::getSize, new ProgressBarRenderer())
                .setCaption("Size");

        Column<Commit, String> timeColumn = grid
                .addColumn(commit -> String.valueOf(commit.getTimestamp()))
                .setCaption("Timestamp");

        // grid.getColumn("fullName").setWidth(185);

        // grid.getColumn("fullTopic").setWidth(372);
        // Allow column hiding for all columns
        grid.getColumns().forEach(column -> column.setHidable(true));

        // Allow column reordering
        grid.setColumnReorderingAllowed(true);

        // Create a header row to hold column filters
        HeaderRow filterRow = grid.appendHeaderRow();

        // Set up a filter for author, topic, date and email
        nameFilter.addValueChangeListener(
                event -> grid.getDataProvider().refreshAll());
        filterRow.getCell(nameColumn).setComponent(nameFilter);

        topicFilter.addValueChangeListener(
                event -> grid.getDataProvider().refreshAll());
        filterRow.getCell(topicColumn).setComponent(topicFilter);

        dateFilter.addValueChangeListener(
                event -> grid.getDataProvider().refreshAll());
        filterRow.getCell(timeColumn).setComponent(dateFilter);

        layout.setSizeFull();
        grid.setSizeFull();

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1);

        grid.addItemClickListener(new ItemClickListener<Commit>() {

            @Override
            public void accept(ItemClick<Commit> event) {
                Commit item = event.getItem();
                boolean isVisible = grid.isDetailsVisible(item);
                grid.setDetailsVisible(item, !isVisible);
            }

        });
        grid.setDetailsGenerator(detailsGenerator);
        // grid.addSelectionListener(new SelectionListener() {
        // @Override
        // public void select(SelectionEvent selectionEvent) {
        // for (Object id : selectionEvent.getAdded()) {
        // grid.setDetailsVisible(id, true);
        // }
        // if (selectionEvent.getSelected().isEmpty()) {
        // for (Object id : selectionEvent.getRemoved()) {
        // grid.setDetailsVisible(id, false);
        // }
        // }
        // }
        // });
        // layout.addShortcutListener(new ShortcutListener("Close details row",
        // KeyCode.ESCAPE, null) {
        // @Override
        // public void handleAction(Object sender, Object target) {
        // try {
        // grid.setDetailsVisible(grid.getSelectedRow(), false);
        // } catch (Exception ignore) {
        // }
        // }
        // });

        setContent(layout);

    }

    private TextField createFilterTextField() {
        TextField filterField = new TextField();
        filterField.setWidth(100, Unit.PERCENTAGE);
        filterField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        return filterField;
    }

    @SuppressWarnings("unchecked")
    private Component buildThemeSelector() {
        final NativeSelect<String> ns = new NativeSelect<>();
        ns.setId("themeSelect");
        ns.setItems(themeVariants.keySet());
        ns.setItemCaptionGenerator(themeVariants::get);

        ns.setValue("tests-valo");
        ns.addValueChangeListener(e -> setTheme(e.getValue()));

        return ns;
    }

    private final DetailsGenerator<Commit> detailsGenerator = new DetailsGenerator<Commit>() {
        @Override
        public Component apply(Commit commit) {
            FormLayout layout = new FormLayout();
            layout.setMargin(true);

            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                    DateFormat.MEDIUM, getLocale());

            Label commitDate = new Label();
            commitDate.setCaption("Commit Timestamp");
            commitDate.setValue(df.format(commit.getCommitTime()));
            layout.addComponent(commitDate);

            Label authorDate = new Label();
            authorDate.setCaption("Author Timestamp");
            authorDate.setValue(df.format(commit.getTimestamp()));
            layout.addComponent(authorDate);

            Label msg = new Label();
            msg.setCaption("Commit Message");
            msg.setWidth("100%");
            msg.setContentMode(ContentMode.HTML);
            msg.setStyleName("align-value-top");
            layout.addComponent(msg);
            String text = commit.getFullMessage()
                    .replaceAll("(\\n\\r)|(\\n\\r)", "\\n");
            text = text.replaceAll("\\r", "\\n");
            text = text.replaceAll("\\n", "<br/>");
            msg.setValue(text);

            return layout;
        }
    };

    private static Date localDateToDate(LocalDate localDate) {
        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
