package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.text.html.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private boolean receiving = true;

    // 소켓과 출력 스트림
    private Socket socket;
    private DataOutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        new Thread(() -> {
            try {
		        // Server에 접속하기 위한 IP, 포트 입력
                socket = new Socket("서버 IP", 5000);  // 추후 서버 IP, 포트 입력
                out = new DataOutputStream(socket.getOutputStream());

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (receiving) {
                    int length = dis.readInt();
                    byte[] imageBytes = new byte[length];
                    dis.readFully(imageBytes);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, length);
                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        
        // 터치 이벤트 처리 (마우스 이동/클릭)
        imageView.setOnTouchListener((v, event) -> {
            if (out == null) return false;

            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();

            try {
                if (action == MotionEvent.ACTION_DOWN) {
                    sendCommand("MOUSE_MOVE " + x + " " + y);
                    sendCommand("MOUSE_CLICK");
                } else if (action == MotionEvent.ACTION_MOVE) {
                    sendCommand("MOUSE_MOVE " + x + " " + y);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        
        
        // 키 이벤트 처리 (키 누르기, 떼기)
        inputField.setOnKeyListener((v, keyCode, event) -> {
            if (outputStream == null) return false;

            if (event.getAction() == KeyEvent.ACTION_DOWN && !repeatTasks.containsKey(keyCode)) {
                sendKeyEvent("KEY_DOWN", keyCode);
                startKeyRepeat(keyCode);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                sendKeyEvent("KEY_UP", keyCode);
                stopKeyRepeat(keyCode);
            }
            return false;
        });
        
    }
    
    /**
     * Server로 보낼 명령어
     * 한 줄 단위로 명령함.
     * @param cmd 보낼 명령어
     * @throws IOException 예외처리
     */
    private void sendCommand(String cmd) throws IOException {
        if (out != null) {
            out.write((cmd + "\n").getBytes());
            out.flush();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiving = false;
    }
}
