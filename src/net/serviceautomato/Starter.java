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
	private final static int DEFAULT_EVENT_BASE_PORT = 10000;
	private final static int DEFAULT_ENFORCER_BASE_PORT = 11000;
	private final static int DEFAULT_REMOTE_BASE_PORT = 12000;
	private final static String FIRST_CLI_ID = "2";

	public static void setCliSeAuAddress(CoordinatorAddressing ca) {
		String[] port = { "2", "7", "8", "29", "33", "37", "48", "51", "60",
				"63" };
		for (String p : port) {
			ca.setAddress(p, new InetSocketAddress(DEFAULT_REMOTE_BASE_PORT
					+ Integer.parseInt(p)));
		}
	}

	private static Thread CliSeAuInstance = new Thread() {
		@Override
		public void run() {
			CoordinatorAddressing ca = new CoordinatorAddressing();
			ca.setLocalEnforcerAddress(new InetSocketAddress(DEFAULT_ENFORCER_BASE_PORT
					+ Integer.parseInt(FIRST_CLI_ID)));
			ca.setPrivateAddress(new InetSocketAddress(DEFAULT_EVENT_BASE_PORT
					+ Integer.parseInt(FIRST_CLI_ID)));
			setCliSeAuAddress(ca);
			try {
				Coordinator coordinator = new Coordinator("test",
						new ServerSocket(DEFAULT_EVENT_BASE_PORT
								+ Integer.parseInt(FIRST_CLI_ID)),
						new ServerSocket(DEFAULT_REMOTE_BASE_PORT
								+ Integer.parseInt(FIRST_CLI_ID)), ca,
						new DHTChordPolicy(FIRST_CLI_ID), Level.DEBUG);
				coordinator.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] args) {
		Thread cliThread;
		if (args.length != 0) {
			cliThread = new Thread() {
				@Override
				public void run() {
					CoordinatorAddressing ca = new CoordinatorAddressing();
					ca.setLocalEnforcerAddress(new InetSocketAddress(
							DEFAULT_ENFORCER_BASE_PORT + Integer.parseInt(args[0])));
					ca.setPrivateAddress(new InetSocketAddress(
							DEFAULT_REMOTE_BASE_PORT
									+ Integer.parseInt(args[0])));
					setCliSeAuAddress(ca);
					try {
						Coordinator coordinator = new Coordinator("test",
								new ServerSocket(DEFAULT_EVENT_BASE_PORT
										+ Integer.parseInt(args[0])),
								new ServerSocket(DEFAULT_REMOTE_BASE_PORT
										+ Integer.parseInt(args[0])), ca,
								new DHTChordPolicy(args[0]), Level.DEBUG);
						coordinator.run();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			cliThread.start();
		} else {
			CliSeAuInstance.start();
			IcapServer.main(null);
		}
	}
}
