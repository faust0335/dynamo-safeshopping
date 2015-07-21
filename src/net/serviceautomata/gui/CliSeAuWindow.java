package net.serviceautomata.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This class initiates a graphical user interface to manipulate multiple CliSeAu
 * nodes
 * 
 * @author Xu, Yinhua
 *
 */

public class CliSeAuWindow {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				CliSeAuFrame frame = new CliSeAuFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

/**
 * This class establishes a window frame for manipulation
 * 
 * @author Xu, Yinhua
 *
 */
class CliSeAuFrame extends JFrame {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 4808345624015235803L;
	/**
	 * default x-coordinate of the frame location
	 */
	public static final int DEFAULT_FRAME_ABSCISSA = 450;
	/**
	 * default y-coordinate of the frame location
	 */
	public static final int DEFAULT_FRAME_ORDINATE = 350;
	/**
	 * default width of the frame
	 */
	public static final int DEFAULT_FRAME_WIDTH = 320;
	/**
	 * default height of the frame
	 */
	public static final int DEFAULT_FRAME_HEIGHT = 200;
	/**
	 * default inside horizontal gap of the frame
	 */
	public static final int DEFAULT_HORIZONTAL_GAP = 10;
	/**
	 * default inside vertical gap of the frame
	 */
	public static final int DEFAULT_VERTICAL_GAP = 10;
	/**
	 * the main panel composed of grids of labels, buttons and text fields
	 */
	private JPanel panel;
	/**
	 * the label of text description of the task in "address" text field
	 */
	private JLabel addressLabel;
	/**
	 * the label of text description of the task in "identifier" text field
	 */
	private JLabel identifierLabel;
	/**
	 * the text field of "address"
	 */
	private JTextField displayAddress;
	/**
	 * the text field of "identifier"
	 */
	private JTextField displayIdentifier;
	/**
	 * the button initiating the first CliSeAu service with an ICAP server
	 */
	private JButton initCliSeAu;
	/**
	 * the button adding new CliSeAu node(s)
	 */
	private JButton addCliSeAu;
	
	/**
	 * The constructor to initialize a frame with a panel
	 */
	public CliSeAuFrame() {
		setTitle("Add CliSeAu Node");
		setBounds(DEFAULT_FRAME_ABSCISSA, DEFAULT_FRAME_ORDINATE,
				DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
		setLayout(new FlowLayout(FlowLayout.CENTER, DEFAULT_HORIZONTAL_GAP,
				DEFAULT_VERTICAL_GAP));
		CliSeAuPanel panel = new CliSeAuPanel();
		add(panel);
		pack();
	}

	/**
	 * A panel of an 3*2 grid arrangement with text fields of both node address
	 * and node identifier, and the buttons to click to initialize a CliSeAu service and add CliSeAu
	 * nodes
	 */
	class CliSeAuPanel extends JPanel {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = -5849809210385211583L;

		public CliSeAuPanel() {

			setLayout(new BorderLayout());

			panel = new JPanel();
			panel.setLayout(new GridLayout(3, 2, DEFAULT_HORIZONTAL_GAP,
					DEFAULT_VERTICAL_GAP));

			addressLabel = new JLabel("NODE ADDRESS");
			panel.add(addressLabel);
			displayAddress = new JTextField("localhost");
			panel.add(displayAddress);

			identifierLabel = new JLabel("NODE IDENTIFIER (INTEGER)");
			panel.add(identifierLabel);
			displayIdentifier = new JTextField("0");
			panel.add(displayIdentifier);

			initCliSeAu = new JButton("INITIALIZE SERVICE");
			ActionListener initListener = new InitCliSeAuAction();
			initCliSeAu.addActionListener(initListener);
			panel.add(initCliSeAu);

			addCliSeAu = new JButton("ADD CLISEAU NODE");
			ActionListener addListener = new AddCliSeAuAction();
			addCliSeAu.addActionListener(addListener);
			panel.add(addCliSeAu);

			// Cannot add CliSeAu if there's no master node
			addCliSeAu.setEnabled(false);

			add(panel, BorderLayout.CENTER);
		}

		/**
		 * The action starts the CliSeAu service with an ICAP server and one
		 * CliSeAu node
		 * 
		 * @author Chen, Yiqun
		 *
		 */
		private class InitCliSeAuAction implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				try {
					Runtime.getRuntime().exec("java -jar SafeShopping.jar");
					addCliSeAu.setEnabled(true);
					initCliSeAu.setEnabled(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * The action starts one more CliSeAu service on the basis of an
		 * available ICAP server and the other existing CliSeAu node(s).
		 * 
		 * @author Chen, Yiqun
		 *
		 */
		private class AddCliSeAuAction implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				try {
					String cmd = new String("java -jar SafeShopping.jar "
							+ displayAddress.getText() + " "
							+ displayIdentifier.getText());
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}