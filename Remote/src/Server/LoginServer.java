package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class LoginServer {
	
	public static int loginPort = 20805;	// 로그인 포트 번호
	public static int streamUDPPort = 50806;

	public static void main(String[] args) {

		/*
		 * 로그인 서버 가동
		 */
		try (ServerSocket serverSocket = new ServerSocket(loginPort)) {
			System.out.println("로그인 서버 시작됨...");

			while (true) {
				Socket client = serverSocket.accept();
				new Thread(() -> handleClient(client)).start();
			}

		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 클라이언트로 부터 사용자 정보 조정
	 * @param client
	 */
	private static void handleClient(Socket client) {
		try (
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
		) 
		{
			// 로그인 요청 받기
			String line = reader.readLine(); // 예: user1:pass123
			System.out.println("클라이언트 요청: " + line);
			
			String[] parts = line.split(":");	// 사용자 정보 받기 아이디:비밀번호
			
			// 잘못된 입력
			if (parts.length != 2) {
				writer.write("INVALID\n");
				writer.flush();
				client.close();
				return;
			}
			
			String userId = parts[0];
			String password = parts[1];
			
			
			/*
			 * 사용자 인증 후 스트리밍 시작
			 */
			if (AuthManager.authenticate(userId, password)) {
				writer.write("OK\n");
				writer.flush();

				// UDP 스트리밍 시작
				String clientIp = client.getInetAddress().getHostAddress();
				System.out.println("인증 성공, 스트리밍 시작: " + clientIp);
				
				// 스트리밍에 필요한 클래스 실행
				StreamSessionManager.startSession(clientIp, streamUDPPort);
	
				
			} else {
				writer.write("FAIL\n");
				writer.flush();
				System.out.println("인증 실패");
				client.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
