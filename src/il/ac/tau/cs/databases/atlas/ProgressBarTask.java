package il.ac.tau.cs.databases.atlas;


import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.TempCommand;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseProgressDBCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProgressBarTask {

    private final BaseProgressDBCommand cmd;
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JProgressBar progressBar;
    private Runnable task;
    private JButton startButton;
    private JTextArea outputTextArea;

    public ProgressBarTask(BaseProgressDBCommand cmd){
        this.cmd = cmd;
        prepareGUI();
    }

    public static void main(String[] args){
        try {
            DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06", "127.0.0.1", "3305", "DbMysql06");
        } catch (AtlasServerException e) {
            e.printStackTrace();
        }
        ProgressBarTask progressBarTask = new ProgressBarTask(new TempCommand(100));
        progressBarTask.startTask();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Java Swing Examples");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(400, 400);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                    String ObjButtons[] = {"Yes","No"};
                    int PromptResult = JOptionPane.showOptionDialog(null,
                            "Exiting, will not stop the update, if it already started. Are you sure you want to exit?", "Atlas updater",
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

    public void startTask(){
        headerLabel.setText("Atlas: Making your dreams come true");

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
                            cmd.setUpdater(new ProgressUpdater(progressBar, outputTextArea));
                            cmd.execute();
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
}
