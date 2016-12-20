package eu.europa.esig.dss.asic.signature.asice;

import java.io.ByteArrayOutputStream;
import java.util.List;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.MimeType;
import eu.europa.esig.dss.asic.ASiCParameters;
import eu.europa.esig.dss.utils.Utils;

public abstract class AbstractDataToSignASiCEWithXAdES {

	private final static String META_INF = "META-INF/";
	private final static String ZIP_ENTRY_ASICE_METAINF_XADES_SIGNATURE = META_INF + "signatures001.xml";

	protected DSSDocument getASiCManifest(List<DSSDocument> documents) {
		ASiCEWithXAdESManifestBuilder manifestBuilder = new ASiCEWithXAdESManifestBuilder(documents);

		DSSDocument manifest = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			DomUtils.writeDocumentTo(manifestBuilder.build(), baos);
			manifest = new InMemoryDocument(baos.toByteArray(), META_INF + "manifest.xml", MimeType.XML);
		} finally {
			Utils.closeQuietly(baos);
		}
		return manifest;
	}

	protected String getSignatureFileName(final ASiCParameters asicParameters, List<DSSDocument> existingSignatures) {
		if (Utils.isStringNotBlank(asicParameters.getSignatureFileName())) {
			return META_INF + asicParameters.getSignatureFileName();
		}
		if (Utils.isCollectionNotEmpty(existingSignatures)) {
			return ZIP_ENTRY_ASICE_METAINF_XADES_SIGNATURE.replace("001", getSignatureNumber(existingSignatures));
		} else {
			return ZIP_ENTRY_ASICE_METAINF_XADES_SIGNATURE;
		}
	}

	private String getSignatureNumber(List<DSSDocument> existingSignatures) {
		int signatureNumbre = existingSignatures.size() + 1;
		String sigNumberStr = String.valueOf(signatureNumbre);
		String zeroPad = "000";
		return zeroPad.substring(sigNumberStr.length()) + sigNumberStr; // 2 -> 002
	}

}