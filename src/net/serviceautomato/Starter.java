package net.serviceautomato;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import net.serviceautomata.instantiation.DHTChordPolicy;
import net.serviceautomata.javacor.Coordinator;
import net.serviceautomata.javacor.CoordinatorAddressing;
import icap.IcapServer;

import org.apache.log4j.Level;

public class Starter {
	private final static int DEFAULT_EVENT_BASE_PORT = 21000;
	private final static int DEFAULT_ENFORCER_BASE_PORT = 21100;
	private final static int DEFAULT_REMOTE_BASE_PORT = 21200;
	private final static String FIRST_CLI_ID = "2";
	private static final int DEFAULT_CLI_PORT = 21300;

	public static void setCliSeAuAddress(CoordinatorAddressing ca,
			DHTChordPolicy policy) {
		/*
		 * String[] port = { "2", "7", "8", "29", "33", "37", "48", "51", "60",
		 * "63" }; for (String p : port) { ca.setAddress(p, new
		 * InetSocketAddress(DEFAULT_REMOTE_BASE_PORT + Integer.parseInt(p))); }
		 */
		ca.setAddress(FIRST_CLI_ID, new InetSocketAddress(
				DEFAULT_REMOTE_BASE_PORT + Integer.parseInt(FIRST_CLI_ID)));
		try {
			@SuppressWarnings("resource")
			ServerSocket cliListener = new ServerSocket(DEFAULT_CLI_PORT);
			Thread newCliNode = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Socket s = cliListener.accept();
							ObjectInputStream ois = new ObjectInputStream(
									s.getInputStream());
							String id = (String) ois.readObject();
							ca.setAddress(
									id,
									new InetSocketAddress(
											DEFAULT_REMOTE_BASE_PORT
													+ Integer.parseInt(id)));
							policy.getChord().insertNode(Integer.parseInt(id));
						} catch (IOException | NumberFormatException
								| ClassNotFoundException e) {
							e.printStackTrace();
						}

					}
				}
			};
			newCliNode.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Thread CliSeAuInstance = new Thread() {
		@Override
		public void run() {
			CoordinatorAddressing ca = new CoordinatorAddressing();
			ca.setLocalEnforcerAddress(new InetSocketAddress(
					DEFAULT_ENFORCER_BASE_PORT + Integer.parseInt(FIRST_CLI_ID)));
			ca.setPrivateAddress(new InetSocketAddress(DEFAULT_EVENT_BASE_PORT
					+ Integer.parseInt(FIRST_CLI_ID)));
			DHTChordPolicy dhtPolicy = new DHTChordPolicy(FIRST_CLI_ID);
			setCliSeAuAddress(ca, dhtPolicy);
			try {
				Coordinator coordinator = new Coordinator("test",
						new ServerSocket(DEFAULT_EVENT_BASE_PORT
								+ Integer.parseInt(FIRST_CLI_ID)),
						new ServerSocket(DEFAULT_REMOTE_BASE_PORT
								+ Integer.parseInt(FIRST_CLI_ID)), ca,
						dhtPolicy, Level.DEBUG);
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
							DEFAULT_ENFORCER_BASE_PORT
									+ Integer.parseInt(args[0])));
					ca.setPrivateAddress(new InetSocketAddress(
							DEFAULT_REMOTE_BASE_PORT
									+ Integer.parseInt(args[0])));
					// setCliSeAuAddress(ca);
					try {
						Coordinator coordinator = new Coordinator("test",
								new ServerSocket(DEFAULT_EVENT_BASE_PORT
										+ Integer.parseInt(args[0])),
								new ServerSocket(DEFAULT_REMOTE_BASE_PORT
										+ Integer.parseInt(args[0])), ca,
								new DHTChordPolicy(args[0]), Level.DEBUG);
						// notify master about the new joined node
						Socket s = new Socket();
						s.connect(new InetSocketAddress(DEFAULT_CLI_PORT));
						ObjectOutputStream oos = new ObjectOutputStream(
								s.getOutputStream());
						oos.writeObject(new String(args[0]));
						s.close();
						// Any slave should know the master
						ca.setAddress(FIRST_CLI_ID,
								new InetSocketAddress(DEFAULT_REMOTE_BASE_PORT
										+ Integer.parseInt(FIRST_CLI_ID)));
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
