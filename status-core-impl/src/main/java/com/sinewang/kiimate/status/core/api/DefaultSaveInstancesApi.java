package com.sinewang.kiimate.status.core.api;

import one.kii.kiimate.model.core.dai.IntensionDai;
import one.kii.kiimate.model.core.dai.ModelSubscriptionDai;
import one.kii.kiimate.model.core.fui.AnModelRestorer;
import one.kii.kiimate.status.core.api.SaveInstancesApi;
import one.kii.kiimate.status.core.dai.InstanceDai;
import one.kii.kiimate.status.core.fui.AnInstanceExtractor;
import one.kii.summer.beans.utils.DataTools;
import one.kii.summer.io.context.WriteContext;
import one.kii.summer.io.exception.Conflict;
import one.kii.summer.io.exception.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by WangYanJiong on 3/27/17.
 */

@Component
public class DefaultSaveInstancesApi implements SaveInstancesApi {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSaveInstancesApi.class);

    @Autowired
    private InstanceDai instanceDai;

    @Autowired
    private AnInstanceExtractor instanceExtractor;

    @Autowired
    private ModelSubscriptionDai modelSubscriptionDai;

    @Autowired
    private AnModelRestorer modelRestorer;

    @Override
    public Receipt saveInstance(WriteContext context, Form form) throws NotFound, Conflict {

        String rootExtId = modelSubscriptionDai.getLatestRootExtIdBySubscriberIdGroupNameTree(
                context.getOwnerId(), form.getGroup(), form.getName(), form.getTree());

        if (rootExtId == null) {
            throw new NotFound(new String[]{context.getOwnerId(), form.getGroup(), form.getName(), form.getTree()});
        }

        Map<String, IntensionDai.Intension> dict = modelRestorer.restoreAsIntensionDict(rootExtId);

        String subId = modelSubscriptionDai.getLatestSubIdBySubscriberIdGroupNameTree(
                context.getOwnerId(), form.getGroup(), form.getName(), form.getTree()
        );

        List<AnInstanceExtractor.Instance> instances = instanceExtractor.extract(context, subId, form.getMap(), dict);

        List<InstanceDai.Instances> instances1 = DataTools.copy(instances, InstanceDai.Instances.class);

        try {
            instanceDai.insertInstances(instances1);
        } catch (InstanceDai.InstanceDuplicated instanceDuplicated) {
            throw new Conflict(subId);
        }


        List<InstanceDai.Instance> dbInstances = instanceDai.selectLatestInstanceBySubId(subId);

        List<Instance> apiInstances = new ArrayList<>();

        for (InstanceDai.Instance dbInstance : dbInstances) {
            Instance apiInstance = DataTools.copy(dbInstance, Instance.class);
            apiInstance.setValue(new String[]{dbInstance.getValue()});
            apiInstances.add(apiInstance);
        }

        Receipt receipt = DataTools.copy(form, Receipt.class);
        receipt.setInstances(apiInstances);
        return receipt;
    }

}
