package il.ac.tau.cs.databases.atlas.connector.command.base;

import il.ac.tau.cs.databases.atlas.ProgressUpdater;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

/**
 * Created by user on 17/06/2015.
 */
public abstract class BaseProgressDBCommand extends BaseDBCommand<Boolean> {
    protected ProgressUpdater progressUpdater = null;

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
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(400, 400);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                String ObjButtons[] = {"Yes","No"};
                int PromptResult = JOptionPane.showOptionDialog(null,
                        "Exiting will not stop the update, if it already started. Are you sure you want to exit?", "Atlas updater",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        ObjButtons,ObjButtons[1]);
                if(PromptResult == 0)
                {
                    mainFrame.dispose();
                }
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("",JLabel.CENTER);

        statusLabel.setSize(350,100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

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
                            setUpdater(new ProgressUpdater(progressBar, outputTextArea));
                            runProgressCmd(con);
                        } catch (AtlasServerException ase) {
                            JOptionPane.showMessageDialog(null, ase.getMessage(), GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
                            mainFrame.dispose();
                        }
                    }
                };
                new Thread(task).start();
            }});

        controlPanel.add(startButton);
        controlPanel.add(progressBar);
        controlPanel.add(scrollPane);
        mainFrame.setVisible(true);

    }

    protected abstract void runProgressCmd(Connection con) throws AtlasServerException;

    protected abstract String getDisplayLabel();
}
