package il.ac.tau.cs.databases.atlas.ui.map;

import java.awt.Canvas;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("serial")
public final class MapBrowser extends Canvas {

    /**
     * Required for Linux, harmless for other OS.
     * <p>
     * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=161911">SWT Component Not Displayed Bug</a>
     */
    static
    {
        System.setProperty("sun.awt.xembedserver", "true");
    }

    /**
     * SWT browser component reference.
     */
    private final AtomicReference<Browser> browserReference = new AtomicReference<>();
 
    /**
     * SWT event dispatch thread reference.
     */
    private final AtomicReference<SwtThread> swtThreadReference = new AtomicReference<>();
 
    /**
     * Get the native browser instance.
     *
     * @return browser, may be <code>null</code>
     */
    public Browser getBrowser() {
        return browserReference.get();
    }
 
    /**
     * Navigate to a URL.
     *
     * @param url URL
     */
    public void setUrl(final String url) {
        // This action must be executed on the SWT thread
        getBrowser().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                getBrowser().setUrl(url);
            }
        });
    }
 
    /**
     * Create the browser canvas component.
     * This must be called after the parent application Frame is made visible -
     * usually directly after frame.setVisible(true).
     * This method creates the background thread, which in turn creates the SWT components and
     * handles the SWT event dispatch loop.
     * This method will block (for a very short time) until that thread has successfully created
     * the native browser component (or an error occurs).
     *
     * @return true if the browser component was successfully created; false if it was not
     */
    public boolean initialize() {
        CountDownLatch browserCreatedLatch = new CountDownLatch(1);
        SwtThread swtThread = new SwtThread(browserCreatedLatch);
        swtThreadReference.set(swtThread);
        swtThread.start();
        boolean result;
        try {
            browserCreatedLatch.await();
            result = browserReference.get() != null;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
 
    /**
     * Dispose the browser canvas component.
     */
    public void dispose() {
        browserReference.set(null);
        SwtThread swtThread = swtThreadReference.getAndSet(null);
        if (swtThread != null) {
            swtThread.interrupt();
        }
    }
 
    /**
     * Implementation of a thread that creates the browser component and then implements an event
     * dispatch loop for SWT.
     */
    private class SwtThread extends Thread {
 
        /**
         * Initialization latch.
         */
        private final CountDownLatch browserCreatedLatch;
 
        /**
		 * Create a thread.
		 * 
		 * @param browserCreatedLatch
		 *            initialization latch.
		 */
        private SwtThread(CountDownLatch browserCreatedLatch) {
            this.browserCreatedLatch = browserCreatedLatch;
        }
 
        @Override
        public void run() {
            // First prepare the SWT components...
            Display display;
            Shell shell;
            try {
                display = new Display();
                shell = SWT_AWT.new_Shell(display, MapBrowser.this);
                shell.setLayout(new FillLayout());
                browserReference.set(new Browser(shell, SWT.NONE));
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
            finally {
                // Guarantee the count-down so as not to block the caller, even in case of error -
                // there is a theoretical (rare) chance of failure to initialise the SWT components
                browserCreatedLatch.countDown();
            }
            // Execute the SWT event dispatch loop...
            try {
                shell.open();
                while (!isInterrupted() && !shell.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                browserReference.set(null);
                //shell.dispose();
                //display.dispose();
            }
            catch (Exception e) {
                e.printStackTrace();
                interrupt();
            }
        }
    }
    
    public static final class BrowserException extends Exception {
    	public BrowserException() {
			super("Failed to initialise map browser.");
		}
    }
}
