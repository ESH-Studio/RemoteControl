package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 서버 구동 main
 * 
 * 
 * @author ESH
 */

public class RemoteServer {


	public void start(int port) {

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("클라이언트 연결 대기 중...");

			Socket clientSocket = serverSocket.accept();
			System.out.println("클라이언트 연결됨");

			// 화면 전송 쓰레드 시작
			new Thread(new ImageSender(clientSocket)).start();

			// 압력 명령 처리 쓰레드 시작
			new Thread(new InputHandler(clientSocket)).start();


		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				}
				catch(IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static void main(String[] args) {

		RemoteServer server = new RemoteServer();

		// 서버 포트 번호
		int port = 7000;
		server.start(port);

	}
}
