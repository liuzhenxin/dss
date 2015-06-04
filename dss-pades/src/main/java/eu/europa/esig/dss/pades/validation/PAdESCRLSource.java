/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pades.validation;

import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.europa.esig.dss.cades.validation.CAdESSignature;
import eu.europa.esig.dss.pdf.pdfbox.PdfDssDict;
import eu.europa.esig.dss.x509.crl.OfflineCRLSource;

/**
 * CRLSource that will retrieve the CRL from a PAdES Signature
 *
 *
 */

public class PAdESCRLSource extends OfflineCRLSource {

    private final CAdESSignature cadesSignature;
    private final PdfDssDict dssCatalog;

    /**
     * The default constructor for PAdESCRLSource.
     *
     * @param cadesSignature
     * @param dssCatalog
     */
    public PAdESCRLSource(final CAdESSignature cadesSignature, final PdfDssDict dssCatalog) {
        this.cadesSignature = cadesSignature;
        this.dssCatalog = dssCatalog;
        extract();
    }

    private void extract() {
        x509CRLList = new ArrayList<X509CRL>();

        if (cadesSignature != null) {
            final List<X509CRL> cadesCrlSource = cadesSignature.getCRLSource().getContainedX509CRLs();
            x509CRLList.addAll(cadesCrlSource);
        }

        if (dssCatalog == null) {
            return;
        }

        final Set<X509CRL> crlList = dssCatalog.getCrlList();
        for (final X509CRL x509CRL : crlList) {
            if (!x509CRLList.contains(x509CRL)) {
                x509CRLList.add(x509CRL);
            }
        }

    }
}