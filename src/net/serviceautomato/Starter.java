package net.serviceautomato;

import icap.IcapServer;

public class Starter {
	private static Thread icapInstance = new Thread() {
		@Override
		public void run() {
			IcapServer.main(null);
		}
	};

	public static void main(String[] args) {
		icapInstance.start();
		try {
			icapInstance.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
