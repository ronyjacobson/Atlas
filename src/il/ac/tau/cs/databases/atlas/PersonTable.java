package il.ac.tau.cs.databases.atlas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Created by user on 17/06/2015.
 */

public class PersonTable {

    public static void main(String[] args) {

        Display display = new Display();
        Shell shell = new Shell(display);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        shell.setLayout(layout);
        final Button displayOnMapButton = new Button(shell, SWT.PUSH);
        displayOnMapButton.setText("Display on Map");
        Table table = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 200;
        table.setLayoutData(data);
        String[] titles = {"Name", "Born In", "Date of Birth", "Died In", "Date of Death"};
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }
        int count = 128;
        for (int i = 0; i < count; i++) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, "Rony Jacobson");
            item.setText(1, "Ramat Gan");
            item.setText(2, "08-07-1989");
            item.setText(3, " ");
            item.setText(4, " ");
        }
        for (int i = 0; i < titles.length; i++) {
            table.getColumn(i).pack();
        }
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }
}
