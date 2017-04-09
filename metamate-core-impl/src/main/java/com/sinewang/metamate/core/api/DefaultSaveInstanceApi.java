package com.sinewang.metamate.core.api;

import one.kii.summer.beans.utils.DataTools;
import one.kii.summer.erest.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import wang.yanjiong.metamate.core.api.SaveInstanceApi;
import wang.yanjiong.metamate.core.dai.InstanceDai;
import wang.yanjiong.metamate.core.dai.IntensionDai;
import wang.yanjiong.metamate.core.dai.ModelSubscriptionDai;
import wang.yanjiong.metamate.core.fi.AnInstanceExtractor;
import wang.yanjiong.metamate.core.fi.AnModelRestorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WangYanJiong on 3/27/17.
 */
@RestController
public class DefaultSaveInstanceApi implements SaveInstanceApi {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSaveInstanceApi.class);

    @Autowired
    private InstanceDai instanceDai;

    @Autowired
    private AnInstanceExtractor instanceExtractor;

    @Autowired
    private ModelSubscriptionDai modelSubscriptionDai;

    @Autowired
    private AnModelRestorer modelRestorer;

    @Override
    @RequestMapping(value = "/{ownerId}/instance/{group}/{tree:.+}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Receipt> saveInstanceViaFormUrlEncoded(
            @RequestHeader("X-SUMMER-RequestId") String requestId,
            @RequestHeader("X-MM-OperatorId") String operatorId,
            @PathVariable("ownerId") String ownerId,
            @PathVariable("group") String group,
            @PathVariable("tree") String tree,
            @RequestParam MultiValueMap<String, String> map) {

        String rootExtId = modelSubscriptionDai.getLatestRootExtIdBySubscriberIdGroupNameTree(ownerId, group, NAME_ROOT, tree);

        if (rootExtId == null) {
            Receipt receipt = new Receipt();
            receipt.setGroup(group);
            receipt.setName(NAME_ROOT);
            receipt.setTree(tree);
            return Response.notFound(requestId, receipt);
        }

        Map<String, IntensionDai.Intension> dict = new HashMap<>();
        modelRestorer.restoreAsFieldDict(rootExtId, dict);


        List<AnInstanceExtractor.Instance> instances = instanceExtractor.extract(ownerId, operatorId, map, dict);

        List<InstanceDai.Instances> instances1 = DataTools.copy(instances, InstanceDai.Instances.class);

        try {
            instanceDai.insertInstances(instances1);
        } catch (InstanceDai.InstanceDuplicated instanceDuplicated) {
            logger.error("instanceDuplicated", instanceDuplicated);
        }

        List<InstanceDai.Instance> dbInstances = instanceDai.selectLatestInstanceByOwnerIdExtId(rootExtId, ownerId);

        List<Instance> apiInstances = new ArrayList<>();

        for (InstanceDai.Instance dbInstance : dbInstances) {
            Instance apiInstance = DataTools.copy(dbInstance, Instance.class);
            apiInstance.setValue(new String[]{dbInstance.getValue()});
            apiInstances.add(apiInstance);
        }

        Receipt receipt = new Receipt();
        receipt.setGroup(group);
        receipt.setName(NAME_ROOT);
        receipt.setTree(tree);
        receipt.setInstances(apiInstances);
        return Response.accepted(requestId, receipt, ownerId);
    }

    @Override
    @RequestMapping(value = "/instance/{group}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Receipt> saveInstanceViaFormUrlEncoded(
            @RequestHeader("X-SUMMER-RequestId") String requestId,
            @RequestHeader("X-MM-OwnerId") String ownerId,
            @RequestHeader("X-MM-OperatorId") String operatorId,
            @PathVariable("group") String group,
            @RequestParam MultiValueMap<String, String> map) {

        return saveInstanceViaFormUrlEncoded(requestId, ownerId, operatorId, group, TREE_MASTER, map);

    }
}
