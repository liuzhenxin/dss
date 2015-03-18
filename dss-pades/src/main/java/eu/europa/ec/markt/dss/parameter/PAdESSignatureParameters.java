package eu.europa.ec.markt.dss.parameter;

public class PAdESSignatureParameters extends SignatureParameters {

	private String reason;
	private String contactInfo;

	/**
	 * This attribute is used to create visible signature in PAdES form
	 */
	private SignatureImageParameters imageParameters;

	/**
	 * @return the reason (used by PAdES)
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set (used by PAdES)
	 */
	public void setReason(final String reason) {
		this.reason = reason;
	}

	/**
	 * @return the contactInfo (used by PAdES)
	 */
	public String getContactInfo() {
		return contactInfo;
	}

	/**
	 * @param contactInfo the contactInfo to set (used by PAdES)
	 */
	public void setContactInfo(final String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public SignatureImageParameters getImageParameters() {
		return imageParameters;
	}

	public void setImageParameters(SignatureImageParameters imageParameters) {
		this.imageParameters = imageParameters;
	}

}
