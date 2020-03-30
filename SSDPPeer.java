package edu.udo.cs.rvs.ssdp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * This class is first instantiated on program launch and IF (and only if) it
 * implements Runnable, a {@link Thread} is created and started.
 *
 */
public class SSDPPeer implements Runnable
{
	public Listen listen;
	public Worker worker;
	public User user;

	public SSDPPeer()
	{
		listen = new Listen();
		worker = new Worker();
		user = new User();
	}

	@Override
	public void run() {


		Thread ThreadListen = new Thread(listen, "Listen Thread");
		Thread ThreadWork = new Thread(worker, "Worker Thread");
		Thread ThreadUser = new Thread(user,"User Thread");
		ThreadListen.start();
		ThreadWork.start();
		ThreadUser.start();

		System.out.println("Starting the application");
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	}




}
