package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MountSkyship extends Thread {
	private final static String volumes[] = { "BackUp", "Video", "Music", "Temp" };

	public MountSkyship() {

		try {
			for (String vol : volumes) {
				if (!checkIfConnected(vol)) {
					mount(vol);
					checkIfConnected(vol);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static boolean skyshipIsConnected() { // NO_UCD (unused code)
		for (String vol : volumes) {
			if (!checkIfConnected(vol)) {
				return false;
			}
		}
		return true;
	}

	private void mount(String vol) throws InterruptedException {
		try {
			Runtime.getRuntime().exec("/bin/mkdir /Volumes/" + vol);

		} catch (IOException e) {
			e.printStackTrace();
		}
		Process p2;
		try {
			p2 = Runtime.getRuntime().exec(new String[] { "/sbin/mount_afp", "-i",
					"afp://admin:Sherlock69@Skyship._afpovertcp._tcp.local/" + vol, "/Volumes/" + vol });
			long finish = System.currentTimeMillis() + 2000;

			while (isAlive(p2) && (System.currentTimeMillis() < finish)) {
				Thread.sleep(10);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean checkIfConnected(String vol) {
		File folder = new File("/Volumes/" + vol);
		boolean empty = false;
		if (!folder.exists())
			return false;
		try {
			empty = isDirEmpty(Paths.get(folder.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (empty)
			folder.delete();

		return !empty;
	}

	private static boolean isAlive(Process p) {
		try {
			p.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	private static boolean isDirEmpty(final Path directory) throws IOException {
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
			return !dirStream.iterator().hasNext();
		}
	}

	public static void main(String[] args) {
		new MountSkyship();
	}
}
