package Server;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * 마우스, 키보드 조작 
 * 
 * 
 * @author ESH
 */


class InputHandler implements Runnable {
    private final Socket socket;
    Robot robot;

    public InputHandler(Socket socket) {
        this.socket = socket;
        try {
        	robot = new Robot();
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
		
    public void run() {
    	// 모바일로부터 받은 명령을 서버(컴퓨터)에 적용
    	// 적용은 로봇으로 행동
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String command;
            while ((command = in.readLine()) != null) {
                String[] movement = command.split(" ");
                switch (movement[0]) {
                	// 마우스 이동
                	// MOUSE_MOVE x y
                    case "MOUSE_MOVE":
                    	int x = Integer.parseInt(movement[1]);
                    	int y = Integer.parseInt(movement[2]);
                        robot.mouseMove(x, y);
                        break;
                        
                    // 마우스 클릭
                    // MOUSE_CLICK
                    case "MOUSE_CLICK":
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        break;
                        
                    // 키 입력 
                    case "KEY_DOWN":
                        robot.keyPress(Integer.parseInt(movement[1]));
                        break;
                    case "KEY_UP":
                        robot.keyRelease(Integer.parseInt(movement[1]));
                        break;
                        
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
