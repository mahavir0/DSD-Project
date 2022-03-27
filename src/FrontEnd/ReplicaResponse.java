/**
 * 
 */
package FrontEnd;

/**
 * @author Mahavir
 *
 */
public class ReplicaResponse {
	
	private int seqId;
	private String ReplicaNo;
	private String request;
	private String response;
	private String status;
	private String params;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public String getReplicaNo() {
		return ReplicaNo;
	}
	public void setReplicaNo(String replicaNo) {
		ReplicaNo = replicaNo;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	/**
	 * 
	 */
	public ReplicaResponse() {
		// TODO Auto-generated constructor stub
	}

}
