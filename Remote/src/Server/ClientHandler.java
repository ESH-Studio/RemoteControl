package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.json.JSONObject;

/**
 * 클라이언트와의 세션을 관리하는 핸들러
 * @author ESH
 */
public class ClientHandler implements Runnable {
	
    private Socket socket;
    private String configData;
    
    private String loginData;	// ?
    private String id;			// 아이디
    private String password;	// 비밀번호
    
    
    private int fps;
    private int bitrate;
    private int width;
    private int height;
    private int port;
    
    /**
     * 생성자
     * @param socket 
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Setter
     * 가로 지정
     * @param width 가로
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * Setter
     * 세로 지정
     * @param height 세로
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Setter
     * 프레임 지정
     * @param frameRate 프레임
     */
    public void setFrameRate(int frameRate) {
        this.fps = frameRate;
    }
    
    /**
     * Setter
     * 비트레이트 지정
     * @param videoBitrate 비트레이트
     */
    public void setVideoBitrate(int videoBitrate) {
        this.bitrate = videoBitrate;
    }
    
    
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            // 로그인 처리 (AuthManager 사용)
        	// 추후 변경 예정
            loginData = reader.readLine();
            JSONObject loginJson = new JSONObject(loginData);
            id = loginJson.getString("id");
            password = loginJson.getString("password");

            if (!AuthManager.authenticate(id, password)) {
                System.out.println("로그인 실패: " + id);
                socket.close();
                return;
            }
            System.out.println("로그인 성공: " + id);
            
            
            /*
             * 세션 설정값 수신
             * fps = 프레임
             * bitrate = 비트레이트
             * width = 가로
             * height = 세로
             * port = UDP 수신 포트
             */
            configData = reader.readLine();
            JSONObject configJson = new JSONObject(configData);

            fps = configJson.getInt("fps");
            bitrate = configJson.getInt("bitrate");
            width = configJson.getInt("width");
            height = configJson.getInt("height");
            port = configJson.getInt("port"); 	// UDP 수신 포트

            
            // 세션 시작
            StreamSessionManager.startSession(
                socket.getInetAddress().getHostAddress(),
                fps, bitrate, width, height, port
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
