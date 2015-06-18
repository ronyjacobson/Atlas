package il.ac.tau.cs.databases.atlas;

import javax.swing.*;

/**
 * Created by user on 17/06/2015.
 */
public class ProgressUpdater {
    private final JProgressBar progressBar;
    private final JTextArea outputTextArea;
    private final JLabel headerLabel;

    public ProgressUpdater(JProgressBar progressBar, JTextArea outputTextArea, JLabel headerLabel) {
        this.progressBar = progressBar;
        this.outputTextArea = outputTextArea;
        this.headerLabel = headerLabel;
    }

    public void updateProgress(final int progress,final String lowerMessage) {
        updateProgress(progress, lowerMessage, headerLabel.getText());
    }

    public void updateProgress(final int progress,final String lowerMessage, final String header) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(progress);
                outputTextArea.setText(lowerMessage);
                headerLabel.setText(header);
            }
        });
    }

    public void updateHeader(final String header) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                headerLabel.setText(header);
            }
        });
    }

    public void resetProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue(0);
            }
        });
    }


}
