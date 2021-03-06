package com.sinewang.kiimate.model.core.spi;


import one.kii.kiimate.model.core.spi.CreateExtensionSpi;
import one.kii.summer.io.exception.*;
import one.kii.summer.io.sender.ErestPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Created by WangYanJiong on 4/7/17.
 */
@ConfigurationProperties(prefix = "metamate")
@Component
public class DefaultCreateExtensionSpi implements CreateExtensionSpi {

    private static Logger logger = LoggerFactory.getLogger(DefaultCreateExtensionSpi.class);

    private static String URI = "/{ownerId}/extension";

    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Receipt createMasterPublicExtension(Form form) throws Panic {
        String url = baseUrl + URI;

        ErestPost erest = new ErestPost(form.getOwnerId());

        try {
            return erest.execute(url, form, Receipt.class, form.getOwnerId());
        } catch (Conflict conflict) {
            Receipt receipt = new Receipt();
            receipt.setId(conflict.getKeys()[0]);
            return receipt;
        } catch (NotFound notFound) {
            logger.error("not-found:{}", notFound.getKey());
            throw new Panic();
        } catch (BadRequest | Forbidden | Panic panic) {
            logger.error("", panic);
            throw new Panic();
        }
    }
}
