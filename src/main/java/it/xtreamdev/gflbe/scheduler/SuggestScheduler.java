package it.xtreamdev.gflbe.scheduler;

import it.xtreamdev.gflbe.repository.SuggestRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@CommonsLog
@Component
public class SuggestScheduler {


    @Value("${gfl.suggestUrl}")
    private String URL_SUGGEST;

    private final static String DELIMITER = ",";

    @Autowired
    private SuggestRepository suggestRepository;

    @Scheduled(cron = "5 * * * * ?")
    public void execute() {

        long start = System.currentTimeMillis();
        log.info("######### Start scheduler");
        suggestRepository.findAll()
                .forEach(rs -> {
                    try {
                        log.info("Keyword --> ".concat(rs.getKeyword()));
                        String suggests = getSuggest(rs.getKeyword().replace(" ", "%20"), DELIMITER);
                        rs.setSuggests(suggests);
                        suggestRepository.save(rs);
                    } catch (Exception e) {
                        log.error("Errore durante il reperimento delle suggest con key word: ".concat(rs.getKeyword()), e);
                    }
                });

        long end = System.currentTimeMillis();
        long time = end - start;
        log.info("######### Finish scheduler in ".concat(String.valueOf(time).concat(" ms")));

    }


    private String getSuggest(String keyWord, String delimiterResponse) throws Exception {

        java.net.URL url = new URL(URL_SUGGEST.replace("{keyWord}", keyWord));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1)));
        doc.getDocumentElement().normalize();
        List<String> result = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("suggestion");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node == null) {
                break;
            }
            String suggestion = node.getAttributes().getNamedItem("data").getTextContent();
            result.add(suggestion);
        }
        return String.join(delimiterResponse, result);
    }


}
