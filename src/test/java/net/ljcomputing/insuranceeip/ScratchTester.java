/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

James G Willmore - LJ Computing - (C) 2023
*/
package net.ljcomputing.insuranceeip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.w3c.tidy.Tidy;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ScratchTester {
    private static final Logger log = LoggerFactory.getLogger(InsuranceEipApplicationTests.class);
    @Autowired private Path outputDirectory;

    @Test
    void createPdf() {
        String url = "https://stackoverflow.com/questions/69302583/how-to-convert-xhtml-to-html-in-java";
        Path documentHtml =
                Path.of(
                        System.getProperty("user.dir"),
                        "src",
                        "test",
                        "resources",
                        "test",
                        "test.html");

        Path outPdf = outputDirectory.resolve("test.pdf");

        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setMakeClean(true);
        tidy.setMakeBare(true);
        tidy.setShowErrors(0);
        tidy.setShowWarnings(false);
        tidy.setForceOutput(true);
        tidy.setPrintBodyOnly(true);
        tidy.setXHTML(true);

        log.debug(
                "================================================================================");

        try (OutputStream os = new FileOutputStream(outPdf.toFile())) {
            Document jsoupDoc = Jsoup.connect(url).get();
            jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); 
            String xhtml = jsoupDoc.html();

            StringReader rdr = new StringReader(xhtml);
            StringWriter wrtr = new StringWriter();

            tidy.parseDOM(rdr, wrtr);

            xhtml = wrtr.getBuffer().toString();

            rdr.close();
            wrtr.close();

            log.debug("xhtml: [ {} ]", xhtml);

            PdfRendererBuilder builder = new PdfRendererBuilder();
            // builder.withFile(documentHtml.toFile());
            // builder.withUri(url);
            builder.withHtmlContent(xhtml, url);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            log.error("ERROR:", e);
        } finally {
            log.debug(
                    "================================================================================");
        }
    }
}
