package it.xtreamdev.gflbe.model.entitylisteners;

import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.ContentLink;
import it.xtreamdev.gflbe.model.RuleSatisfation;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.PostLoad;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class ContentEntityListener {

    @PostLoad
    private void postLoad(Content content) {
        RuleSatisfation ruleSatisfation = RuleSatisfation.builder().build();
        Integer contentCharacterBodyLenght = content.getContentRules().getMaxCharacterBodyLength();
        Integer customerCharacterBodyLeght = content.getCustomer().getContentRules().getMaxCharacterBodyLength();

        Integer characterToCheck = Optional.ofNullable(contentCharacterBodyLenght).orElse(customerCharacterBodyLeght);

        if (Objects.isNull(content.getBody())) {
            ruleSatisfation.setCharacterSatisfied(false);
            ruleSatisfation.setCharacterError("Contenuto articolo vuoto");
        } else if (Objects.nonNull(characterToCheck)) {
            String bodyCleaned = content.getBody().replaceAll("</?[^>]+>", "");
            if (bodyCleaned.length() >= characterToCheck) {
                ruleSatisfation.setCharacterSatisfied(true);
            } else {
                ruleSatisfation.setCharacterSatisfied(false);
                ruleSatisfation.setCharacterError(String.format("Numero caratteri richiesto: almeno %s, caratteri attuali: %s", characterToCheck, bodyCleaned.length()));
            }
        } else {
            ruleSatisfation.setCharacterSatisfied(true);
        }

        ruleSatisfation.setExpirationDateOneDaySatisfied(true);
        ruleSatisfation.setExpirationDatePlusThenOneDaySatisfied(true);

        if (content.getContentStatus() == ContentStatus.WORKING) {
            LocalDate now = LocalDate.now();

            if (now.isEqual(content.getDeliveryDate())) {
                ruleSatisfation.setExpirationDateOneDaySatisfied(false);
                ruleSatisfation.setExpirationDateOneDayError("Il contenuto scade oggi");
            } else if (now.isAfter(content.getDeliveryDate())) {
                ruleSatisfation.setExpirationDatePlusThenOneDaySatisfied(false);
                ruleSatisfation.setExpirationDatePlusThenOneDayError("Il contenuto è scaduto");
            }
        }

        ruleSatisfation.setTitleSatisfied(true);
        ruleSatisfation.setLinkUrlSatisfied(true);
        ruleSatisfation.setLinkTextSatisfied(true);

        String titleRule = StringUtils.isNotBlank(content.getContentRules().getTitle()) ?
                content.getContentRules().getTitle() :
                StringUtils.isNotBlank(content.getCustomer().getContentRules().getTitle()) ?
                        content.getCustomer().getContentRules().getTitle() : null;

        if (Objects.nonNull(titleRule) && !titleRule.equals(content.getTitle())) {
            ruleSatisfation.setTitleSatisfied(false);
            ruleSatisfation.setTitleError("Il titolo non rispetta la regola impostata");
        }

        String linkTextRule = StringUtils.isNotBlank(content.getContentRules().getLinkText()) ? content.getContentRules().getLinkText() : StringUtils.isNotBlank(content.getCustomer().getContentRules().getLinkText()) ? content.getCustomer().getContentRules().getLinkText() : null;
        String linkUrlRule = StringUtils.isNotBlank(content.getContentRules().getLinkUrl()) ? content.getContentRules().getLinkUrl() : StringUtils.isNotBlank(content.getCustomer().getContentRules().getLinkUrl()) ? content.getCustomer().getContentRules().getLinkUrl() : null;

        if (Objects.nonNull(linkTextRule)) {
            if (Objects.isNull(content.getLinks()) || content.getLinks().isEmpty()) {
                ruleSatisfation.setLinkTextSatisfied(false);
                ruleSatisfation.setLinkTextError("Il testo del link non rispetta la regola impostata");
            } else if (!linkTextRule.equals(content.getLinks().get(0).getLinkText())) {
                ruleSatisfation.setLinkTextSatisfied(false);
                ruleSatisfation.setLinkTextError("Il testo del link non rispetta la regola impostata");
            }
        }

        if (Objects.nonNull(linkUrlRule)) {
            if (Objects.isNull(content.getLinks()) || content.getLinks().isEmpty()) {
                ruleSatisfation.setLinkUrlSatisfied(false);
                ruleSatisfation.setLinkUrlError("L'url del link non rispetta la regola impostata");
            } else if (!linkUrlRule.equals(content.getLinks().get(0).getLinkUrl())) {
                ruleSatisfation.setLinkUrlSatisfied(false);
                ruleSatisfation.setLinkUrlError("L'url del link non rispetta la regola impostata");
            }
        }

        content.setRuleSatisfation(ruleSatisfation);
    }

}
