package il.ac.tau.cs.databases.atlas.core.progress;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * ProgressBar object for installation and updating the application processes.
 */
public class ProgressUpdater {
    private final JProgressBar progressBar;
    private final JTextArea outputTextArea;
    private final JLabel headerLabel;
    private final AtomicBoolean running;

    public ProgressUpdater(JProgressBar progressBar, JTextArea outputTextArea, JLabel headerLabel) {
        this.progressBar = progressBar;
        this.outputTextArea = outputTextArea;
        this.headerLabel = headerLabel;
        running = new AtomicBoolean(true);
    }

    public void updateProgress(final int progress,final String lowerMessage) throws AtlasServerException {
        checkIfStillRunning();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(progress);
                outputTextArea.setText(lowerMessage);
            }
        });
    }

    public void updateProgress(final int progress,final String lowerMessage, final String header) throws AtlasServerException {
        checkIfStillRunning();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(progress);
                outputTextArea.setText(lowerMessage);
                headerLabel.setText(header);
            }
        });
    }

    public void updateHeader(final String header) throws AtlasServerException {
        checkIfStillRunning();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                headerLabel.setText(header);
            }
        });
    }

    public void resetProgress() throws AtlasServerException {
        checkIfStillRunning();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue(0);
                outputTextArea.setText("");
            }
        });
    }

    public void stopExecution() {
        running.getAndSet(false);
    }

    private void checkIfStillRunning() throws AtlasServerException {
        if (!running.get()) {
            throw new AtlasServerException("Process stopped");
        }
    }


}
