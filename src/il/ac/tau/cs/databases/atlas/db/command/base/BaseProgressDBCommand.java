package il.ac.tau.cs.databases.atlas.db.command.base;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.progress.ProgressUpdater;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.Connection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by user on 17/06/2015.
 */
public abstract class BaseProgressDBCommand extends BaseDBCommand<Boolean> {
    protected ProgressUpdater progressUpdater = null;

    private Thread thread = null;

    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JProgressBar progressBar;
    private Runnable task;
    private JButton startButton;
    private JTextArea outputTextArea;


    @Override
    protected Boolean innerExecute(Connection con) throws AtlasServerException {
        prepareGUI();
        startTask(con);
        return true;
    }

    private void setUpdater(ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }

    private void prepareGUI(){
        mainFrame = new JFrame(getFrameLabel());
        final URL bgURL = getClass().getResource("/map/progress-bg6.png");
        mainFrame.setContentPane(new JLabel(new ImageIcon(bgURL)));
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(600, 250);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(3, 1));

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                String ObjButtons[] = {"Yes", "No"};
                int PromptResult = JOptionPane.showOptionDialog(null,
                        "Exiting may cause unexpected behaviour, if the process already started. Are you sure you want to exit?", "Atlas updater",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        ObjButtons, ObjButtons[1]);
                if (PromptResult == 0) {
                    if (progressUpdater != null) {
                        progressUpdater.stopExecution();
                    }
                    mainFrame.dispose();
                }
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("",JLabel.CENTER);

        statusLabel.setSize(350, 100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setOpaque(false);

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    protected abstract String getFrameLabel();

    private void startTask(final Connection con){
        headerLabel.setText(getDisplayLabel());

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        startButton = new JButton("Start");

        outputTextArea = new JTextArea("",5,20);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setUpdater(new ProgressUpdater(progressBar, outputTextArea, headerLabel));
                            runProgressCmd(con);
                            popFinishDialog();
                            mainFrame.dispose();
                        } catch (AtlasServerException ase) {
                            JOptionPane.showMessageDialog(null, ase.getMessage(), GraphicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
                            mainFrame.dispose();
                        }
                    }
                };
                thread = new Thread(task);
                thread.start();
            }
        });

        controlPanel.add(startButton);
        controlPanel.add(progressBar);
        controlPanel.add(scrollPane);
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width * 2, (progressBar.getPreferredSize().height * 5) / 4));
        scrollPane.setPreferredSize(new Dimension((scrollPane.getPreferredSize().width * 3) / 2, (scrollPane.getPreferredSize().height * 2) / 4));
        mainFrame.setVisible(true);

    }

    private void popFinishDialog() {
        JOptionPane.showMessageDialog(mainFrame, getSuccessMessage());
    }

    protected abstract String getSuccessMessage();

    protected abstract void runProgressCmd(Connection con) throws AtlasServerException;

    protected abstract String getDisplayLabel();
}
