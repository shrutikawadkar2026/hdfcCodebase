package com.hdfc.core.servlets;

import com.adobe.aemfd.docmanager.Document;
import com.hdfc.core.services.PdfMergeService;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/mergepdf",
                "sling.servlet.methods=GET"
        }
)
public class MergePdfServlet extends SlingSafeMethodsServlet {

    @Reference
    private PdfMergeService pdfMergeService;

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {

            Document mergedPdf = pdfMergeService.mergePdfs();

            response.setContentType("application/pdf");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=merged.pdf"
            );

            try (InputStream inputStream = mergedPdf.getInputStream()) {

                IOUtils.copy(
                        inputStream,
                        response.getOutputStream()
                );

                response.getOutputStream().flush();
            }

        } catch (Exception e) {

            response.sendError(
                    SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }
}