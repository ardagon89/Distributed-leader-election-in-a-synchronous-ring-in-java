import java.util.Arrays;

/// Class for simulating links/channels between nodes
public class MessagingSys {
	String[] messagelist;

	public MessagingSys(int length) {
		messagelist = new String[length];
		Arrays.fill(messagelist, "");
	}

	// gets the message from my incoming channel/link
	public String get(int loc) {
		String result = messagelist[loc];
		messagelist[loc] = "";
		return result;
	}

	// places the message in my outgoing channel/link
	public void put(int loc, String Msg) {
		messagelist[(loc + 1) % messagelist.length] = Msg;
		// System.out.println(Arrays.toString(messagelist));
	}
}