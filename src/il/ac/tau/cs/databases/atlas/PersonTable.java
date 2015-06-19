package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.Location;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.swing.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 17/06/2015.
 */

public class PersonTable {

    public static final String[] COLUMNS = {"Name", "Born in", "Date of Birth", "Died in", "Date of Death", "Wiki Link"};
    private Shell shell;
    private Display display;
    private final Table table;

    public PersonTable(Map<String, Result> resultMap) {
        display = Display.getCurrent();
        Shell shell = new Shell(display);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        shell.setLayout(layout);
        final Button editPersonButton = new Button(shell, SWT.PUSH);
        editPersonButton.setText("Edit selected person");
        editPersonButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                final TableItem[] selection = table.getSelection();
                if (selection == null || selection.length == 0) {
                    JOptionPane.showMessageDialog(null, "You must select a person to edit!", GrapicUtils.PROJECT_NAME, JOptionPane.WARNING_MESSAGE);
                } else {
                    System.out.println("you chose: " + selection[0].getText());
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });
        table = new Table(shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 200;
        table.setLayoutData(data);
        for (int i = 0; i < COLUMNS.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(COLUMNS[i]);
        }
        for (Map.Entry<String, Result> idToResultQuery : resultMap.entrySet()) {
            if (idToResultQuery.getKey().startsWith("d")) {
                continue;
            }
            final Result bornResult = idToResultQuery.getValue();
            final Result deathResult = resultMap.get("d" + bornResult.getID());

            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, bornResult.getName()); // Name
            item.setText(1, bornResult.getLocation().getName()); // Location
            item.setText(2, bornResult.getDateToString()); // Birth Date
            item.setText(3, deathResult == null ? " " : deathResult.getLocation().getName()); // Death Location
            item.setText(4, deathResult == null ? " " : deathResult.getDateToString()); // Death Date
            item.setText(5, bornResult.getWikiLink()); // WikiLink
        }

        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumn(i).pack();
        }
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }


    public static void main(String[] args) {

        Display display = new Display();
        Map<String, Result> resultMap = new HashMap<>();
        resultMap.put("b123", new Result("123", "etan", new Location((long)1, "Tel Aviv", 22.2f, 33.3f), new Date(1000,10,1), true, "artist", "www.com", false, "artist"));
        resultMap.put("d123", new Result("123", "etan", new Location((long)1, "Ramat Gan", 22.2f, 33.3f), new Date(1020,10,1), false, "artist", "www.com", false, "artist"));
        resultMap.put("b1234", new Result("1234", "paz", new Location((long)1, "Tel Aviv", 22.2f, 33.3f), new Date(500,10,1), true, "artista", "www.com", false, "artista"));
        resultMap.put("d1234", new Result("1234", "paz", new Location((long)1, "Ramat Gan", 22.2f, 33.3f), new Date(520,10,1), false, "artista", "www.com", false, "artista"));
        new PersonTable(resultMap);
    }
}
