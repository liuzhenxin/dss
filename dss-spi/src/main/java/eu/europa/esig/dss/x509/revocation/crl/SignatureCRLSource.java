package eu.europa.esig.dss.x509.revocation.crl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.europa.esig.dss.Digest;
import eu.europa.esig.dss.x509.RevocationOrigin;
import eu.europa.esig.dss.x509.revocation.SignatureRevocationSource;

@SuppressWarnings("serial")
public abstract class SignatureCRLSource extends OfflineCRLSource implements SignatureRevocationSource<CRLToken> {
	
	Map<CRLBinaryIdentifier, List<CRLToken>> crlTokenMap = new HashMap<CRLBinaryIdentifier, List<CRLToken>>();
	
	private List<CRLToken> revocationValuesCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> attributeRevocationValuesCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> timestampRevocationValuesCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> dssDictionaryCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> vriDictionaryCRLs = new ArrayList<CRLToken>();
	
	private List<CRLRef> completeRevocationRefsCRLs = new ArrayList<CRLRef>();
	private List<CRLRef> attributeRevocationRefsCRLs = new ArrayList<CRLRef>();
	private List<CRLRef> timestampRevocationRefsCRLs = new ArrayList<CRLRef>();

	@Override
	public List<CRLToken> getRevocationValuesTokens() {
		return revocationValuesCRLs;
	}

	@Override
	public List<CRLToken> getAttributeRevocationValuesTokens() {
		return attributeRevocationValuesCRLs;
	}

	@Override
	public List<CRLToken> getTimestampRevocationValuesTokens() {
		return timestampRevocationValuesCRLs;
	}

	@Override
	public List<CRLToken> getDSSDictionaryTokens() {
		return dssDictionaryCRLs;
	}

	@Override
	public List<CRLToken> getVRIDictionaryTokens() {
		return vriDictionaryCRLs;
	}

	public List<CRLRef> getCompleteRevocationRefs() {
		return completeRevocationRefsCRLs;
	}

	public List<CRLRef> getAttributeRevocationRefs() {
		return attributeRevocationRefsCRLs;
	}

	public List<CRLRef> getTimestampRevocationRefs() {
		return timestampRevocationRefsCRLs;
	}
	
	public List<CRLRef> getAllCRLReferences() {
		List<CRLRef> crlRefs = new ArrayList<CRLRef>();
		crlRefs.addAll(getCompleteRevocationRefs());
		crlRefs.addAll(getAttributeRevocationRefs());
		crlRefs.addAll(getTimestampRevocationRefs());
		return crlRefs;
	}
	
	public Map<CRLBinaryIdentifier, List<CRLToken>> getCRLTokenMap() {
		return crlTokenMap;
	}
	
	/**
	 * Allows to fill all CRL missing revocation values from the given {@code signatureCRLSource}
	 * @param signatureCRLSource {@link SignatureCRLSource} to populate values from
	 */
	public void populateCRLRevocationValues(SignatureCRLSource signatureCRLSource) {
		for (Entry<CRLBinaryIdentifier, List<CRLToken>> entry : signatureCRLSource.getCRLTokenMap().entrySet()) {
			for (CRLToken crlToken : entry.getValue()) {
				storeCRLToken(entry.getKey(), crlToken);
			}
		}
	}
	
	/**
	 * Allows to add all CRL values from the given {@code signatureCRLSource}
	 * @param signatureCRLSource {@link SignatureCRLSource}
	 */
	protected void addValuesFromInnerSource(SignatureCRLSource signatureCRLSource) {
		populateCRLRevocationValues(signatureCRLSource);

		for (CRLBinaryIdentifier crlBinary : signatureCRLSource.getAllCRLIdentifiers()) {
			for (RevocationOrigin origin : crlBinary.getOrigins()) {
				addCRLBinary(crlBinary, origin);
			}
		}
		for (CRLRef crlRef : signatureCRLSource.getAllCRLReferences()) {
			addReference(crlRef, crlRef.getLocation());
		}
	}
	
	@Override
	protected void storeCRLToken(CRLBinaryIdentifier crlBinary, CRLToken crlToken) {
		if (crlsBinaryMap.containsKey(crlBinary.asXmlId())) {
			List<CRLToken> tokensList = crlTokenMap.get(crlBinary);
			if (tokensList == null) {
				tokensList = new ArrayList<CRLToken>();
				crlTokenMap.put(crlBinary, tokensList);
			}
			tokensList.add(crlToken);
			for (RevocationOrigin origin : crlBinary.getOrigins()) {
				addToRelevantList(crlToken, origin);
			}
		}
	}
	
	private void addToRelevantList(CRLToken crlToken, RevocationOrigin origin) {
		switch (origin) {
			case INTERNAL_REVOCATION_VALUES:
				revocationValuesCRLs.add(crlToken);
				break;
			case INTERNAL_ATTRIBUTE_REVOCATION_VALUES:
				attributeRevocationValuesCRLs.add(crlToken);
				break;
			case INTERNAL_TIMESTAMP_REVOCATION_VALUES:
				timestampRevocationValuesCRLs.add(crlToken);
				break;
			case INTERNAL_DSS:
				dssDictionaryCRLs.add(crlToken);
				break;
			case INTERNAL_VRI:
				vriDictionaryCRLs.add(crlToken);
			default:
				break;
		}
	}
	
	protected void addReference(CRLRef crlRef, RevocationOrigin origin) {
		switch (origin) {
		case COMPLETE_REVOCATION_REFS:
			if (!completeRevocationRefsCRLs.contains(crlRef)) {
				completeRevocationRefsCRLs.add(crlRef);
			}
			break;
		case ATTRIBUTE_REVOCATION_REFS:
			if (!attributeRevocationRefsCRLs.contains(crlRef)) {
				attributeRevocationRefsCRLs.add(crlRef);
			}
		case TIMESTAMP_REVOCATION_REFS:
			if (!timestampRevocationRefsCRLs.contains(crlRef)) {
				timestampRevocationRefsCRLs.add(crlRef);
			}
		default:
			break;
		}
	}
	
	/**
	 * Returns a list of {@link CRLRef}s assigned to the given {@code crlBinary}
	 * @param crlBinary {@link CRLBinaryIdentifier} to get references for
	 * @return list of {@link CRLRef}s
	 */
	public List<CRLRef> getReferencesForCRLIdentifier(CRLBinaryIdentifier crlBinary) {
		List<CRLRef> relatedRefs = new ArrayList<CRLRef>();
		for (CRLRef crlRef : getAllCRLReferences()) {
			byte[] digestValue = crlBinary.getDigestValue(crlRef.getDigestAlgorithm());
			if (Arrays.equals(crlRef.getDigestValue(), digestValue)) {
				relatedRefs.add(crlRef);
			}
		}
		return relatedRefs;
	}
	
	/**
	 * Returns a contained {@link CRLRef} with the given {@code digest}
	 * @param digest {@link Digest} to find a {@link CRLRef} with
	 * @return {@link CRLRef}
	 */
	public CRLRef getCRLRefByDigest(Digest digest) {
		for (CRLRef crlRef : getAllCRLReferences()) {
			if (digest.getAlgorithm().equals(crlRef.getDigestAlgorithm()) && Arrays.equals(digest.getValue(), crlRef.getDigestValue())) {
				return crlRef;
			}
		}
		return null;
	}

}
