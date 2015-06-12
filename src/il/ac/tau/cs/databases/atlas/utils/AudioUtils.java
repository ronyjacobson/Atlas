package il.ac.tau.cs.databases.atlas.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioUtils {

	public static final String AUDIO_FILE_NAME = "glimpse_melody.wav";
    private final int BUFFER_SIZE = 128000;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;
    public static boolean stopSound = false;

    /**
     * Plays a wav file
     * @param filename the name of the file that is going to be played
     */
    public void playSound(){

        try {
        	String audioPath = GrapicUtils.RESOURCES_FOLDER + AudioUtils.AUDIO_FILE_NAME;
            final InputStream resourceAsStream = getClass().getResourceAsStream(audioPath);
            InputStream stream = new BufferedInputStream(resourceAsStream);
            audioStream = AudioSystem.getAudioInputStream(stream);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1 && !stopSound) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
    
    /**
     * Stops the sound
     */
    public void stopSound(){
    	stopSound = true;
    }
    
    public static class AudioToggleActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!stopSound){
				stopSound = true;
			} else {
				stopSound = false;
				// Play audio
				Runnable r = new Runnable() {
					public void run() {
						new AudioUtils().playSound();
					}
				};
				new Thread(r).start();
			}
		}
    }
}