package com.sinewang.metamate.core.fi;

import one.kii.summer.codec.utils.HashTools;
import wang.yanjiong.metamate.core.api.SubscribeModelApi;
import wang.yanjiong.metamate.core.fi.AnSubscribeModelExtractor;

/**
 * Created by WangYanJiong on 4/6/17.
 */
public class DefaultSubscribeModelExtrator implements AnSubscribeModelExtractor {

    private String hashId(String providerId, String extId, String publication, String version, String subscriberId) {
        return HashTools.hashHex(
                providerId, extId, publication, version, subscriberId
        );
    }


    @Override
    public ModelSubscription extract(SubscribeModelApi.Form form, String providerId, String extId, String publication, String version, String subscriberId) {

        ModelSubscription subscription = new ModelSubscription();

        subscription.setExtId(extId);
        subscription.setGroup(form.getGroup());
        subscription.setName(form.getName());
        subscription.setTree(form.getTree());

        subscription.setSubscriberId(subscriberId);
        subscription.setProviderId(providerId);
        subscription.setPublication(publication);
        subscription.setVersion(version);

        String id = hashId(providerId, extId, publication, version, subscriberId);

        subscription.setId(id);
        return subscription;
    }
}