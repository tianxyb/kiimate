package com.sinewang.kiimate.model.core.api;

import com.sinewang.kiimate.model.core.dai.mapper.ModelSubscriptionMapper;
import one.kii.kiimate.model.core.api.SubscribeModelApi;
import one.kii.kiimate.model.core.fui.AnSubscribeModelExtractor;
import one.kii.summer.io.context.WriteContext;
import one.kii.summer.io.exception.Conflict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by WangYanJiong on 4/6/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ComponentScan("com.sinewang.kiimate.model.core")
@SpringBootTest(classes = {TestSubscribeModelApi.class})
public class TestSubscribeModelApi {

    @Autowired
    private SubscribeModelApi subscribeModelApi;

    @Autowired
    private AnSubscribeModelExtractor subscribeModelExtractor;


    @Autowired
    private ModelSubscriptionMapper modelSubscriptionMapper;

    private String group = "testSubGroup";

    private String subscriberId = "testSubscriberId";

    private String operatorId = "testOperatorId";

    private String ownerId = "testOwnerId";

    private String requestId = "testRequestId";

    private String pubSetHash = "testPubSetHash";

    private String name = "testSubName";

    private String tree = "testSubTree";

    @Test
    public void test() {
        SubscribeModelApi.Form form = new SubscribeModelApi.Form();

        form.setGroup("testGroup");

        form.setPubSetHash(pubSetHash);


        String id = subscribeModelExtractor.hashId(subscriberId, pubSetHash, group, name, tree);
        modelSubscriptionMapper.deleteById(id);

        form.setGroup(group);
        form.setName(name);

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);


        SubscribeModelApi.Receipt receipt = null;
        try {
            receipt = subscribeModelApi.subscribe(context, form);
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }


        modelSubscriptionMapper.deleteById(id);

    }
}
