package Server;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * 이미지 출력
 * 
 * @author ESH
 */

class ImageSender implements Runnable {
    private final Socket socket;
    Robot robot;

    public ImageSender(Socket socket) {
        this.socket = socket;
        try {
        	robot = new Robot();
        }
        catch(Exception e) {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(null, "\'InputHandler\' Error Occured");
        }
        
    }

    public void run() {
        try {
            OutputStream out = socket.getOutputStream();
            while (true) {
                BufferedImage screen = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screen, "jpg", baos);
                
                byte[] imageBytes = baos.toByteArray();				
                DataOutputStream dos = new DataOutputStream(out);	
                dos.writeInt(imageBytes.length);					// 길이 먼저 전송
                dos.write(imageBytes);								// 실제 이미지
                dos.flush();                          // 남은 데이터 모두 내보내기

                Thread.sleep(100); // 10fps
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
