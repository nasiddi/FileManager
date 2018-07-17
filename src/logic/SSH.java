package logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import com.jcraft.jsch.*;

public class SSH {

	private static Session session;

	public static void startSession() {
		String remoteHostUserName = "admin";
		String remoteHostpassword = "Sherlock69";
		String remoteHostName = "skyship.synology.me";

		JSch jsch = new JSch();
		try {
			session = jsch.getSession(remoteHostUserName, remoteHostName, 22);
			session.setPassword(remoteHostpassword);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void terminateSession() {
		session.disconnect();
	}

	public static void sendCommand(String command) {
		try {
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			channel.setCommand(command + ";");

			channel.connect();

			String msg = null;
			while ((msg = in.readLine()) != null) {
				System.out.println(msg);
			}
			waitForChannelClosure(channel, 100000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static void waitForChannelClosure(ChannelExec ce, long maxwaitMs) {

	    final long until = System.currentTimeMillis() + maxwaitMs;

	    try {
	        while (!ce.isClosed()) { 
	            Thread.sleep(250);
	        }

	    } catch (InterruptedException e) {
	        throw new RuntimeException("Interrupted", e);
	    }

	    if (!ce.isClosed()) {
	        throw new RuntimeException("Channel not closed in timely manner!");
	    }

	};
}
