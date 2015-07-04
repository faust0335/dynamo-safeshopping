package icap.services;

/**
 * This adapter is based on GreasySpoon service
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import net.serviceautomata.instantiation.SafeShoppingDecision;
import net.serviceautomata.instantiation.SafeShoppingEvent;
import net.serviceautomata.instantiation.SafeShoppingEventFactory;
import tools.logger.Log;
import icap.IcapServer;
import icap.core.AbstractService;
import icap.core.IcapParserException;

/**
 * @author Chen, Yiqun
 * @author Liu, Yi
 * @author Xu, Yinhua
 *
 */
public class SafeShoppingAdapter extends AbstractService {
	private final static int DEFAULT_CLISEAU_PORT = 10001;
	private ServerSocket enforcerSocket;

	private final static int DEFAULT_INTERCEPT_PORT = 10002;
	private Socket eventSocket;
	private InetSocketAddress eventAddress;

	private Socket CliClientSocket;
	// -------------ICAP parser parameters-------------------
	/** Human readable name for service, used mainly for logs */
	static String servicename = ServicesProperties
			.getString("GreasySpoon.servicename");
	// ------------------------------------------------------
	StringBuilder logstr = new StringBuilder();

	/** Set if MIME magic must be used to control server responses MIME types */
	private static boolean mimemagiccheck = false;
	private static boolean trustServiceMimeTypePerDefault = true;

	private static boolean initialized = false;

	/** Define HTTP response mime types supported by the service (in RESPMOD) */
	private static String[] supportedContentTypes;

	/** Define HTTP response codes supported by the service (in RESPMOD) */
	// static int[] supportedResponseCodes = null;

	/**
	 * @return Returns the mimemagiccheck.
	 */
	public static boolean isMimemagiccheck() {
		return mimemagiccheck;
	}

	/**
	 * @return Returns the trustServiceMimeTypePerDefault.
	 */
	public static boolean isTrustServiceMimeTypePerDefault() {
		return trustServiceMimeTypePerDefault;
	}

	/**
	 * @return Returns the supportedContentTypes.
	 */
	public static String[] getSupportedContentTypes() {
		return supportedContentTypes;
	}

	/**
	 * @return Returns the compressanytime.
	 */
	public static boolean isCompressanytime() {
		return compressanytime;
	}

	/**
	 * Directory containing GeeasySpoon configuration (must be relative to
	 * application path)
	 */
	public static String confDirectory = "conf";

	/**
	 * Property file defining specific comments tags for associated languages
	 * (default://)
	 */
	public static Properties languageComments = new Properties();
	private static String languageCommentsFile;
	private static boolean compressanytime = false;
	static String[] compressibleContentTypes = new String[] { "text/",
			"application/xml", "application/x-javascript", "application/json" };

	/**
	 * @return Returns the compressibleContentTypes.
	 */
	public static String[] getCompressibleContentTypes() {
		return compressibleContentTypes;
	}

	public void initCliSeAuSocket() {
		// ---- Initialization of event socket
		eventSocket = new Socket();
		eventAddress = new InetSocketAddress("localhost", DEFAULT_CLISEAU_PORT);
		if (enforcerSocket == null) {
			try {
				enforcerSocket = new ServerSocket();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// <------------------------------------------------------------------------->
	/**
	 * Create a service thread to proceed server connection
	 * 
	 * @param icapserver
	 *            ICAP server managing the service
	 * @param clientsocket
	 *            ICAP client socket
	 */
	public SafeShoppingAdapter(IcapServer icapserver, Socket clientsocket) {
		super(icapserver, clientsocket);
		/* Init the icap server */
		this.server = icapserver;
		if (clientsocket != null)
			this.setSocket(clientsocket);
		try {
			initialized = initialize();
		} catch (Exception e) {
			System.err
					.println("Safe Shopping service initialisation failure - check if language engines are correctly installed");
			e.printStackTrace();
			System.exit(1);
		}
		// Initialize the sockets for CliSeAu
		initCliSeAuSocket();

	}

	// <------------------------------------------------------------------------->

	// <------------------------------------------------------------------------->
	/** set class status to uninitialized */
	public static void cleanup() {
		initialized = false;
	}

	// <------------------------------------------------------------------------->

	// <------------------------------------------------------------------------->
	/**
	 * Return ICAP vectoring points supported by this service
	 * 
	 * @see icap.core.AbstractService#getSupportedModes()
	 */
	public VectoringPoint getSupportedModes() {
		return VectoringPoint.REQRESPMOD;
	}

	// <------------------------------------------------------------------------->

	// <------------------------------------------------------------------------->
	/**
	 * Parse parameters from configuration files
	 */
	private synchronized boolean initialize() {
		if (initialized)
			return initialized;
		ServicesProperties.refresh();
		supportedContentTypes = ServicesProperties.getString(
				"GreasySpoon.mimesupported").split("\\s+");
		compressanytime = ServicesProperties.getString("GreasySpoon.compress") != null
				&& ServicesProperties.getBooleanValue("GreasySpoon.compress");
		if (ServicesProperties
				.getString("GreasySpoon.compressibleContentTypes") != null)
			compressibleContentTypes = ServicesProperties.getString(
					"GreasySpoon.compressibleContentTypes").split("\\s+");
		mimemagiccheck = ServicesProperties.getString("GreasySpoon.mimemagic") != null
				&& ServicesProperties.getBooleanValue("GreasySpoon.mimemagic");
		if (mimemagiccheck) {
			if (Log.config())
				Log.error(Log.CONFIG, "GreasySpoon: MimeMagic enabled");
		}

		languageCommentsFile = GreasySpoon.confDirectory + File.separator
				+ ServicesProperties.getString("GreasySpoon.comments");
		try {
			languageComments.clear();
			languageComments.load(new FileInputStream(languageCommentsFile));
		} catch (Exception e) {
			if (Log.info())
				Log.error(Log.INFO,
						ServicesProperties.getString("GreasySpoon.err1")
								+ languageCommentsFile);
		}
		confDirectory = this.server.serviceconfig.getProperty("confDirectory",
				confDirectory);

		return true;
	}

	// <------------------------------------------------------------------------->
	/**
	 * @see icap.core.AbstractService#getDescription()
	 */
	public String getDescription() {
		return ServicesProperties.getString("GreasySpoon.description");
	}

	// <------------------------------------------------------------------------->

	// <------------------------------------------------------------------------->
	/**
	 * Main AbstractService method implementation ICAP request has been parsed
	 * => generate response
	 * 
	 * @see icap.core.AbstractService#getResponse(java.io.ByteArrayOutputStream)
	 */
	public int getResponse(ByteArrayOutputStream bas) {
		if (Log.isEnable())
			logstr.setLength(0);
		bas.reset();
		long timing = System.nanoTime();
		int returncode = -1;
		try {
			switch (this.getType()) {
			case REQMOD:
				if (Log.isEnable())
					this.logstr.append("HTTP/").append(this.httpmethod)
							.append(" ").append(this.getAbsolutePath());
				returncode = getReqmodResponse(bas);
				break;
			case RESPMOD:
				if (Log.isEnable())
					logstr.append("HTTP/").append(this.rescode).append(" ")
							.append(this.getAbsolutePath());
				returncode = getRespModResponse(bas);
				break;
			default:
				break;
			}
			if (returncode != -1) {
				timing = (System.nanoTime() - timing) / 1000000;
				if (Log.isEnable())
					Log.access(logstr.insert(0, String.format(
							"%1$-4s [%2$-7s] [%3$-10s] ICAP/%4$-3s ", timing,
							type.toString(), servicename, returncode)));
				return returncode;
			}
		} catch (java.io.EOFException eof) {
			try {
				bas.reset();
				bas.write(_400CLIENTERROR);
			} catch (Exception e) {
				// even error return failed => do nothing more
			}
			timing = (System.nanoTime() - timing) / 1000000;
			if (Log.isEnable())
				Log.access(logstr.insert(0, String.format(
						"%1$-4s [%2$-7s] [%3$-10s] ICAP/%4$-3s ", timing,
						type.toString(), servicename, 400)));
			return 400;
		} catch (IcapParserException ipe) {
			try {
				bas.reset();
				bas.write(_400CLIENTERROR);
			} catch (Exception e) {
				// even error return failed => do nothing more
			}
			timing = (System.nanoTime() - timing) / 1000000;
			if (Log.isEnable())
				Log.access(logstr.insert(0, String.format(
						"%1$-4s [%2$-7s] [%3$-10s] ICAP/%4$-3s ", timing,
						type.toString(), servicename, 400)));
			return 400;
		} catch (Throwable e) {
			bas.reset();
			System.gc();
			Log.error(Log.WARNING,
					"Safe Shopping failure while doing security check: - ", e);
		}
		// bug: return a 500 error code
		try {
			bas.reset();
			bas.write(_500SERVERERROR);
		} catch (Exception e) {
			// even error return failed => do nothing more
		}
		timing = (System.nanoTime() - timing) / 1000000;
		if (Log.isEnable())
			Log.access(logstr.insert(0, String.format(
					"%1$-4s [%2$-7s] [%3$-10s] ICAP/%4$-3s ", timing,
					type.toString(), servicename, 500)));
		return 500;
	}

	// <------------------------------------------------------------------------->

	// <------------------------------------------------------------------------->
	/**
	 * Check if provided mime type is supported by current greasyspoon
	 * configuration
	 * 
	 * @param contenttype
	 *            The content type associated to the body
	 * @param mimeTypesToCheck
	 *            mime types associated to GS engine
	 * @return true if content type is declared as supported
	 */
	public static boolean isMimeTypeSupported(String contenttype,
			String[] mimeTypesToCheck) {
		boolean ismimesupported = false;
		for (String ct : mimeTypesToCheck) {
			if (ct.equals("*"))
				return true;
			if (contenttype.contains(ct)) {
				ismimesupported = true;
				break;
			}
		}
		return ismimesupported;
	}

	// <------------------------------------------------------------------------->

	// <------------------------------------------------------------------------->
	/**
	 * AbstractRespmodeService implementation ICAP request has been parsed:
	 * generate a response for RESP MODE
	 * 
	 * @param bas
	 *            The stream in which response will be provided
	 * @return ICAP response code
	 * @throws Exception
	 */
	public synchronized int getRespModResponse(ByteArrayOutputStream bas)
			throws Exception {

		String contenttype = this.getRespHeader("content-type");
		if (contenttype != null) {
			contenttype = contenttype.toLowerCase();
		} else {
			if (this.getContentLength() == 0) {
				contenttype = "text/html";// no body provided: set response to
											// HTML by default
			} else {
				contenttype = "application/octet-stream";// RFC 2616: if no
															// content type is
															// provided,
															// consider it as
															// octet stream
			}
		}
		if (Log.finest())
			Log.trace(Log.FINEST, "Content-type = " + contenttype);
		if (Log.isEnable())
			logstr.append(" [").append(contenttype).append("]");
		// ----------------------------------------------------------
		if (contenttype == null) {// this case should never occur
			if (Log.isEnable())
				logstr.append(" [no mime-type]");
			return earlyResponse(bas);
		}
		// Check supported content types => skip services and return 204
		if (!isMimeTypeSupported(contenttype, supportedContentTypes)) {
			if (Log.isEnable())
				logstr.append(" [unsupported mime-type]");
			return earlyResponse(bas);
		}
		// ----------------------------------------------------------

		return earlyResponse(bas);
	}

	// Comunicate with CliSeAu
	public synchronized SafeShoppingDecision getDecision(SafeShoppingEvent event) {
		while (true) {
			try {
				// Initialize socket
				if (enforcerSocket.isClosed()) {
					enforcerSocket = new ServerSocket(DEFAULT_INTERCEPT_PORT);
				}
				if (!enforcerSocket.isBound()) {
					enforcerSocket.bind(new InetSocketAddress(
							DEFAULT_INTERCEPT_PORT));
				}

				if (eventSocket.isClosed()) {
					eventSocket = new Socket();
				}
				eventSocket.connect(eventAddress);
				// Sending Event
				ObjectOutputStream oos = new ObjectOutputStream(
						eventSocket.getOutputStream());
				oos.writeObject(event);

				// Receiving enforcement
				CliClientSocket = enforcerSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						CliClientSocket.getInputStream());
				SafeShoppingDecision decision = (SafeShoppingDecision) ois
						.readObject();

				CliClientSocket.close();
				eventSocket.close();
				enforcerSocket.close();
				return decision;
			} catch (IOException e1) {
				// e1.printStackTrace();
				try {
					eventSocket.close();
					enforcerSocket.close();
					Thread.sleep(200);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				continue;
				//return this.getDecision(event);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// <------------------------------------------------------------------------->
	/**
	 * AbstractRespmodeService implementation ICAP request has been parsed:
	 * generate a response for REQMOD
	 * 
	 * @param bas
	 *            The stream in which response will be provided
	 * @return ICAP response code
	 * @throws Exception
	 */
	public synchronized int getReqmodResponse(ByteArrayOutputStream bas)
			throws Exception {
		// Ignore normal messages
		/*
		 * if (!this.req_url.contains("token") &&
		 * !this.reqHeader.toString().contains("payerID")) { return 200; }
		 */
		System.out.println("\nURL: " + this.req_url + "\nHeader: "
				+ this.reqHeader.toString());
		if (this.getDecision(SafeShoppingEventFactory
				.createEvent(this.reqHeader.toString())) == SafeShoppingDecision.REJECT) {
			System.out.println("Nein");
		}

		return earlyResponse(bas);
	}

	// <------------------------------------------------------------------------->

	// /////////////////////////
	// /////////////////////////
	// TOOLS Methods
	// /////////////////////////
	// /////////////////////////

	// ---------------------------------------------------------------------------
	/**
	 * Give the application absolute path
	 * 
	 * @return String the application absolute path
	 */
	protected final static String getApplicationPath() {
		String AppPath = new String();
		File current = new File("."); // set to the relative path
		AppPath = current.getAbsolutePath(); // get the real path
		AppPath = AppPath.substring(0, AppPath.length() - 2); // remove the '\.'
		AppPath = AppPath + File.separator;
		return AppPath;
	}// End getApplicationPath
		// ---------------------------------------------------------------------------
}