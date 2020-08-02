package com.marketing.web.utils;


import net.sf.jasperreports.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;

@Component
public class PdfGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PdfGenerator.class);
    private final String JASPER_PATH = "/jasper";


    public byte[] generateJasperPDF(String name, List<?> dataSource, Map<String, Object> otherFields) {

        try {
            JRBeanCollectionDataSource tableColDataSource = new JRBeanCollectionDataSource(dataSource);
            JasperReport jasperReport = JasperCompileManager.compileReport(getResource(name));
            otherFields.put("datasource",tableColDataSource);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, otherFields, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            e.printStackTrace();
            return null;
        }
    }

    private InputStream getResource(String name) {
        return getClass().getResourceAsStream(JASPER_PATH+"/"+name);
    }

}
