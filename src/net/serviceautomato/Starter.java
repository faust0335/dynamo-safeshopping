package net.serviceautomato;

import icap.IcapServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import net.serviceautomata.instantiation.DHTChordPolicy;
import net.serviceautomata.javacor.Coordinator;
import net.serviceautomata.javacor.CoordinatorAddressing;

import org.apache.log4j.Level;

/**
 * The static starter class that initialize icap server and CliSeAu instances.
 * This is controlled by arguments passed from commandline. The first argument
 * indicates the address of master CliSeAu instance. Second one indicates its
 * port
 * 
 * @author Chen, Yiqun
 *
 */
public class Starter {
	private final static int DEFAULT_EVENT_BASE_PORT = 21000;
	private final static int DEFAULT_ENFORCER_BASE_PORT = 21100;
	private final static int DEFAULT_REMOTE_BASE_PORT = 21200;
	private final static String FIRST_CLI_ID = "2";
	private final static int DEFAULT_CLI_PORT = 21300;

	private static Thread CliSeAuInstance;

	private static String identifier;

	private static void setCliSeAuAddress(CoordinatorAddressing ca,
			DHTChordPolicy policy) {
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
									new InetSocketAddress(s.getInetAddress(),
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

	private static void setupCli(String masterAddr, String port) {
		CliSeAuInstance = new Thread() {
			@Override
			public void run() {
				CoordinatorAddressing ca = new CoordinatorAddressing();
				// We deal with the decision manually in Icap service instead of
				// waiting for a suppresser. So simply redirect the decision to 
				// the first CliSeAu node. 
				ca.setLocalEnforcerAddress(new InetSocketAddress(
						DEFAULT_ENFORCER_BASE_PORT
								+ Integer.parseInt(FIRST_CLI_ID)));
				ca.setPrivateAddress(new InetSocketAddress(
						DEFAULT_EVENT_BASE_PORT + Integer.parseInt(port)));
				DHTChordPolicy dhtPolicy = new DHTChordPolicy(port);

				if (masterAddr == null) {
					setCliSeAuAddress(ca, dhtPolicy);
				} else {
					try {
						ca.setAddress(
								port,
								new InetSocketAddress(InetAddress
										.getByName(masterAddr),
										DEFAULT_REMOTE_BASE_PORT
												+ Integer.parseInt(port)));
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}

				try {
					Coordinator coordinator = new Coordinator(port,
							new ServerSocket(DEFAULT_EVENT_BASE_PORT
									+ Integer.parseInt(port)),
							new ServerSocket(DEFAULT_REMOTE_BASE_PORT
									+ Integer.parseInt(port)), ca, dhtPolicy,
							Level.DEBUG);

					// notify master about the new joined node
					if (masterAddr != null) {
						Socket s = new Socket();
						s.connect(new InetSocketAddress(InetAddress
								.getByName(masterAddr), DEFAULT_CLI_PORT));
						ObjectOutputStream oos = new ObjectOutputStream(
								s.getOutputStream());
						oos.writeObject(port);
						s.close();
					}

					coordinator.run();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	public static String getPort() {
		if (identifier == null) {
			return FIRST_CLI_ID;
		}
		return identifier;
	}

	public static void main(String[] args) {
		if (args.length == 2) {
			setupCli(args[0], args[1]);
			CliSeAuInstance.start();
		} else if (args.length == 0) {
			setupCli(null, FIRST_CLI_ID);
			CliSeAuInstance.start();
			IcapServer.main(null);
		} else {
			System.err.println("Incorrect arguments format:\n"
					+ "Usage: java -jar SafeShopping.jar [address] [port]");
		}
	}
}
