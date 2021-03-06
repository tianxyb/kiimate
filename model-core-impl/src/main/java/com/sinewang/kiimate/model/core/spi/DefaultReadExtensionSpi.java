package com.sinewang.kiimate.model.core.spi;

import one.kii.kiimate.model.core.spi.ReadExtensionSpi;
import one.kii.summer.io.exception.BadRequest;
import one.kii.summer.io.exception.NotFound;
import one.kii.summer.io.exception.Panic;
import one.kii.summer.io.sender.ErestGetBasic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by WangYanJiong on 4/7/17.
 */
@ConfigurationProperties(prefix = "metamate")
@Component
public class DefaultReadExtensionSpi implements ReadExtensionSpi {


    private static String URI = "/{ownerId}/extension/{group}/{name}/{tree}";
    private static String TREE = "master";
    private String ownerId;
    private String visitorId;
    private String baseUrl;

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    @Override
    public String readMasterExtension(GroupForm form) throws Panic {
        String url = baseUrl + URI;

        ErestGetBasic erest = new ErestGetBasic(visitorId);
        try {
            return erest.execute(url, String.class, ownerId, form.getGroup(), NAME_ROOT, TREE);
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Panic panic) {
            panic.printStackTrace();
        }
        throw new Panic();
    }

    @Override
    public String readMasterExtension(GroupNameForm form) throws Panic {
        String url = baseUrl + URI;

        ErestGetBasic erest = new ErestGetBasic(visitorId);

        try {
            return erest.execute(url, String.class, ownerId, form.getGroup(), form.getName(), TREE);
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Panic panic) {
            panic.printStackTrace();
        }
        throw new Panic();
    }
}
