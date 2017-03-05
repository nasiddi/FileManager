package logic;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MountSkyship extends Thread{

	public MountSkyship() {

		String volumes[] = { "BackUp", "Video", "Music", "Temp" };

		try {
			for (String vol : volumes) {
				if (checkConnection(vol)) {
					mount(vol);
					checkConnection(vol);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void mount(String vol) throws InterruptedException {
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

	public boolean checkConnection(String vol) {
		File folder = new File("/Volumes/" + vol);
		boolean empty = false;
		if(!folder.exists())
			return true;
		try {
			empty = isDirEmpty(Paths.get(folder.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (empty)
			folder.delete();

		return empty;
	}

	public static boolean isAlive(Process p) {
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
