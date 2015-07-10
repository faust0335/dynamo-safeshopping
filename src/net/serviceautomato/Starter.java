package net.serviceautomato;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import net.serviceautomata.instantiation.DHTChordPolicy;
import net.serviceautomata.javacor.Coordinator;
import net.serviceautomata.javacor.CoordinatorAddressing;
import icap.IcapServer;

import org.apache.log4j.Level;

public class Starter {
	private final static int DEFAULT_SERVICE_PORT = 10000;
	private final static int DEFAULT_ICAP_PORT = 11000;
	private final static int DEFAULT_REMOTE_BASE_PORT = 12000;
	private final static String FIRST_CLI_ID = "0";

	//private static Coordinator coordinator;
	private static Thread CliSeAuInstance = new Thread() {
		@Override
		public void run() {
			CoordinatorAddressing ca = new CoordinatorAddressing();
			ca.setLocalEnforcerAddress(new InetSocketAddress(DEFAULT_ICAP_PORT));
			ca.setPrivateAddress(new InetSocketAddress(DEFAULT_REMOTE_BASE_PORT));
			ca.setAddress(FIRST_CLI_ID, new InetSocketAddress(DEFAULT_REMOTE_BASE_PORT));
			try {
				Coordinator coordinator = new Coordinator("test", new ServerSocket(
						DEFAULT_SERVICE_PORT), new ServerSocket(
						DEFAULT_REMOTE_BASE_PORT), ca, new DHTChordPolicy(FIRST_CLI_ID),
						Level.DEBUG);
				coordinator.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] args) {
		Thread t;
		if (args.length != 0) {
			t = new Thread() {
				@Override
				public void run() {
					CoordinatorAddressing ca = new CoordinatorAddressing();
					ca.setLocalEnforcerAddress(new InetSocketAddress(
							DEFAULT_ICAP_PORT));
					ca.setPrivateAddress(new InetSocketAddress(
							DEFAULT_REMOTE_BASE_PORT));
					try {
						Coordinator coordinator = new Coordinator("test", new ServerSocket(
								DEFAULT_SERVICE_PORT - Integer.parseInt(args[0])), new ServerSocket(
								DEFAULT_REMOTE_BASE_PORT + Integer.parseInt(args[0])), ca, new DHTChordPolicy(
								args[0]), Level.DEBUG);
						coordinator.run();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		} else {
			CliSeAuInstance.start();
			IcapServer.main(null);
		}
	}
}
