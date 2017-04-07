package com.sinewang.statemate.core.spi;

import one.kii.statemate.core.spi.ReadExtensionSpi;
import one.kii.summer.erest.ErestGet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
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
    public String readMasterExtension(GroupForm form) {
        String url = baseUrl + URI;

        ErestGet erestGet = new ErestGet();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-MM-VisitorId", visitorId);

        return erestGet.doGet(url, httpHeaders, String.class, ownerId, form.getGroup(), NAME_ROOT, TREE);

    }

    @Override
    public String readMasterExtension(GroupNameForm form) {
        String url = baseUrl + URI;

        ErestGet erestGet = new ErestGet();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-MM-VisitorId", visitorId);

        return erestGet.doGet(url, httpHeaders, String.class, ownerId, form.getGroup(), form.getName(), TREE);

    }
}