package eu.europa.esig.dss.validation;

public enum XmlRevocationRefLocation {
	
	/**
	 * The revocation reference was found in the signature 'complete-revocation-references' attribute (used in CAdES and XAdES)
	 */
	COMPLETE_REVOCATION_REFS,

	/**
	 * The revocation reference was found in the signature 'attribute-revocation-references' attribute (used in CAdES and XAdES)
	 */
	ATTRIBUTE_REVOCATION_REFS,

	/**
	 * The revocation reference was found in a timestamp attribute (used in CAdES)
	 */
	TIMESTAMP_REVOCATION_REFS,

}
