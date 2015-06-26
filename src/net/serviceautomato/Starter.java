package net.serviceautomato;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import net.serviceautomata.instantiation.SafeShoppingPolicy;
import net.serviceautomata.javacor.Coordinator;
import net.serviceautomata.javacor.CoordinatorAddressing;
import icap.IcapServer;

import org.apache.log4j.Level;

public class Starter {
	private final static int DEFAULT_ENFORCER_PORT = 10001;
	private final static int DEFAULT_INTERCEPT_PORT = 10002;
	private final static int DEFAULT_REMOTE_PORT = 10003;

	private static Coordinator coordinator;
	private static Thread CliSeAuInstance = new Thread() {
		@Override
		public void run() {
			CoordinatorAddressing ca = new CoordinatorAddressing();
			ca.setLocalEnforcerAddress(new InetSocketAddress("localhost",
					DEFAULT_ENFORCER_PORT));
			ca.setPrivateAddress(new InetSocketAddress("localhost",
					DEFAULT_INTERCEPT_PORT));
			try {
				coordinator = new Coordinator("test", new ServerSocket(
						DEFAULT_INTERCEPT_PORT, 0,
						InetAddress.getByName("localhost")), new ServerSocket(
						DEFAULT_REMOTE_PORT, 0,
						InetAddress.getByName("localhost")), ca,
						new SafeShoppingPolicy("safePolicy"), Level.DEBUG);
				icapInstance.start();
				coordinator.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	private static Thread icapInstance = new Thread() {
		@Override
		public void run() {
			IcapServer.main(null);
		}
	};

	public static void main(String[] args) {
		try {
			CliSeAuInstance.start();
			icapInstance.join();
			CliSeAuInstance.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
