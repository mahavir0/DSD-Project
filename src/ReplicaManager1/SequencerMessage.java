/**
 * 
 */
package ReplicaManager1;

import java.util.Comparator;

/**
 * @author Mahavir
 *
 */
public class SequencerMessage {

	public int sequenceId;
	public String requestMessage;
	/**
	 * 
	 */
	public SequencerMessage(int sequenceId, String requestMessage) {
		// TODO Auto-generated constructor stub
		this.sequenceId = sequenceId;
		this.requestMessage = requestMessage;
	}
	
	public String getrequestMessage() {
		return requestMessage;
	}
	
	public int getsequenceId() {
		return sequenceId;
	}

}

class SequencerMessageComparator implements Comparator<SequencerMessage>{

	@Override
	public int compare(SequencerMessage arg0, SequencerMessage arg1) {
		// TODO Auto-generated method stub
		if(arg0.sequenceId < arg1.sequenceId)
			return 1;
		else if(arg0.sequenceId > arg1.sequenceId)
			return -1;
		return 0;
	} 
	
}