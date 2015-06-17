package il.ac.tau.cs.databases.atlas;

import javax.swing.*;

/**
 * Created by user on 17/06/2015.
 */
public class ProgressUpdater {
    private JProgressBar progressBar;
    private JTextArea outputTextArea;

    public ProgressUpdater(JProgressBar progressBar, JTextArea outputTextArea) {
        this.progressBar = progressBar;
        this.outputTextArea = outputTextArea;
    }

    public void updateProgress(final int progress,final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(progress);
                outputTextArea.setText(message);
            }
        });
    }
}
