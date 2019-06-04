package eu.europa.esig.dss.cades.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.jaxb.diagnostic.XmlFoundRevocation;
import eu.europa.esig.dss.jaxb.diagnostic.XmlRevocationRef;
import eu.europa.esig.dss.jaxb.diagnostic.XmlTimestampedObject;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.XmlRevocationOrigin;
import eu.europa.esig.dss.validation.XmlRevocationRefLocation;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.validation.reports.wrapper.DiagnosticData;
import eu.europa.esig.dss.validation.reports.wrapper.SignatureWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.TimestampWrapper;
import eu.europa.esig.dss.x509.TimestampType;

public class DSS1469Test {
	
	@Test
	public void test() {
		DSSDocument dssDocument = new FileDocument("src/test/resources/validation/dss-1469/cadesLTAwithATv2andATv3.sig");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		DSSDocument detachedContent = new FileDocument("src/test/resources/validation/dss-1469/screenshot2.png");
		validator.setDetachedContents(Arrays.asList(detachedContent));
		Reports reports = validator.validateDocument();
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		assertNotNull(signature);
		List<XmlFoundRevocation> foundRevocations = signature.getFoundRevocations();
		assertNotNull(foundRevocations);
		assertEquals(2, foundRevocations.size());
		List<String> timestampRevocationValues = signature.getRevocationIdsByOrigin(XmlRevocationOrigin.INTERNAL_TIMESTAMP_REVOCATION_VALUES);
		assertNotNull(timestampRevocationValues);
		assertEquals(1, timestampRevocationValues.size());
		List<XmlRevocationRef> timestampRevocationRefs = signature.getFoundRevocationRefsByLocation(XmlRevocationRefLocation.TIMESTAMP_REVOCATION_REFS);
		assertNotNull(timestampRevocationRefs);
		assertEquals(1, timestampRevocationRefs.size());
		
		List<TimestampWrapper> timestamps = diagnosticData.getTimestamps();
		assertTrue(Utils.isCollectionNotEmpty(timestamps));
		int archiveTimestampCounter = 0;
		for (TimestampWrapper timestamp : timestamps) {
			if (TimestampType.ARCHIVE_TIMESTAMP.equals(timestamp.getType())) {
				assertEquals(9, timestamp.getTimestampedObjects().size());
				archiveTimestampCounter++;
			}
			assertTrue(timestamp.isMessageImprintDataFound());
			assertTrue(timestamp.isMessageImprintDataIntact());
		}
		assertEquals(1, archiveTimestampCounter);
		
	}

	@Test
	public void testExpired() {
		DSSDocument dssDocument = new FileDocument("src/test/resources/validation/dss-1469/cadesLTAwithATv2expired.p7s");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		Reports reports = validator.validateDocument();
		assertNotNull(reports);
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		assertNotNull(diagnosticData);
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		assertNotNull(signature);
		List<XmlFoundRevocation> foundRevocations = signature.getFoundRevocations();
		assertNotNull(foundRevocations);
		assertEquals(3, foundRevocations.size());
		
		List<String> revocationIds = signature.getRevocationIds();
		assertEquals(3, revocationIds.size());
		
		List<TimestampWrapper> timestamps = diagnosticData.getTimestamps();
		assertTrue(Utils.isCollectionNotEmpty(timestamps));
		int archiveTimestampCounter = 0;
		for (TimestampWrapper timestamp : timestamps) {
			if (TimestampType.ARCHIVE_TIMESTAMP.equals(timestamp.getType())) {
				int foundRevocationsCounter = 0;
				List<XmlTimestampedObject> timestampedObjects = timestamp.getTimestampedObjects();
				for (XmlTimestampedObject timestampedObject : timestampedObjects) {
					if (revocationIds.contains(timestampedObject.getToken().getId())) {
						foundRevocationsCounter++;
					}
				}
				assertEquals(3, foundRevocationsCounter);
				archiveTimestampCounter++;
			}
			assertTrue(timestamp.isMessageImprintDataFound());
			assertTrue(timestamp.isMessageImprintDataIntact());
		}
		assertEquals(1, archiveTimestampCounter);
	}

}
