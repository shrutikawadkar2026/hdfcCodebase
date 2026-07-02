package com.hdfc.core.services.impl;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.assembler.client.AssemblerOptionSpec;
import com.adobe.fd.assembler.client.AssemblerResult;
import com.adobe.fd.assembler.service.AssemblerService;
import com.hdfc.core.services.PdfMergeService;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component(service = PdfMergeService.class)
public class PdfMergeServiceImpl implements PdfMergeService {

    @Reference
    AssemblerService assemblerService;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public Document mergePdfs() throws Exception {
        InputStream ddxStream = getClass().getClassLoader().getResourceAsStream("ddx/merge.ddx");
        Document ddxDocument = new Document(ddxStream);
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE,"forms-service");
        ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
        Resource resource1 = resourceResolver.getResource("/content/dam/formsanddocuments/pdf1.pdf");
        Resource resource2 = resourceResolver.getResource("/content/dam/formsanddocuments/pdf2.pdf");
        Asset asset1 = resource1.adaptTo(Asset.class);
        Asset asset2 = resource2.adaptTo(Asset.class);
        InputStream pdf1Stream = asset1.getOriginal().getStream();
        InputStream pdf2Stream = asset2.getOriginal().getStream();
        Document pdf1Document = new Document(pdf1Stream);
        Document pdf2Document = new Document(pdf2Stream);
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("pdf1", pdf1Document);
        inputMap.put("pdf2", pdf2Document);
        AssemblerOptionSpec assemblerOptionSpec = new AssemblerOptionSpec();
        assemblerOptionSpec.setFailOnError(true);
        AssemblerResult assemblerResult = assemblerService.invoke(ddxDocument, inputMap, assemblerOptionSpec);
        Map<String, Document> outputMap = assemblerResult.getDocuments();
        Document mergedPdf = outputMap.get("merged.pdf");
        return mergedPdf;
    }
}
