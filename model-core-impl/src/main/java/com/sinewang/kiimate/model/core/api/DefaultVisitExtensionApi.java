package com.sinewang.kiimate.model.core.api;

import one.kii.summer.io.context.ReadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import one.kii.kiimate.model.core.api.VisitExtensionApi;
import one.kii.kiimate.model.core.fui.AnExtensionExtractor;
import one.kii.kiimate.model.core.fui.AnModelRestorer;

import java.util.Map;

/**
 * Created by WangYanJiong on 4/5/17.
 */

@Component
public class DefaultVisitExtensionApi implements VisitExtensionApi {

    @Autowired
    private AnExtensionExtractor extensionExtractor;

    @Autowired
    private AnModelRestorer modelRestorer;

    @Override
    public Map<String, Object> readExtensionByGroupNameVersion(ReadContext context, Form form) {
        String extId = extensionExtractor.hashId(context.getOwnerId(), form.getGroup(), form.getName(), form.getTree(), VISIBILITY_PUBLIC);
        return modelRestorer.restoreAsMetaData(extId);
    }


    @Override
    public Map<String, Object> readExtensionByGroupNameVersion(ReadContext context, TinyForm form) {

        String extId = extensionExtractor.hashId(context.getOwnerId(), form.getGroup(), NAME_ROOT, TREE_MASTER, VISIBILITY_PUBLIC);
        return modelRestorer.restoreAsMetaData(extId);
    }

    @Override
    public Map<String, Object> readExtensionByGroupNameVersion(ReadContext context, SimpleForm form) {
        String extId = extensionExtractor.hashId(context.getOwnerId(), form.getGroup(), form.getName(), TREE_MASTER, VISIBILITY_PUBLIC);
        return modelRestorer.restoreAsMetaData(extId);
    }
}