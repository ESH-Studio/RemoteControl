package Server;

import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ScreenCaptureWorker implements Runnable {

    private final BlockingQueue<BufferedImage> frameQueue = new LinkedBlockingQueue<>();
    private final ScreenCapture screenCapture;
    private final Encoder encoder;
    private Thread captureThread;
    private Thread encoderThread;

    public ScreenCaptureWorker(String clientIp, int port) {
        this.screenCapture = new ScreenCapture(frameQueue, 15); // 15 FPS
        this.encoder = new Encoder(frameQueue, clientIp, port);
    }

    @Override
    public void run() {
        captureThread = new Thread(screenCapture);
        encoderThread = new Thread(encoder);

        captureThread.start();
        encoderThread.start();
    }

    public void stop() {
        screenCapture.stop();
        encoder.stop();
        if (captureThread != null) captureThread.interrupt();
        if (encoderThread != null) encoderThread.interrupt();
    }
}
