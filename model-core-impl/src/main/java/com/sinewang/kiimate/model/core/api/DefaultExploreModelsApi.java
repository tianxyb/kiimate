package com.sinewang.kiimate.model.core.api;

import one.kii.summer.beans.utils.DataTools;
import one.kii.summer.io.context.ReadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import one.kii.kiimate.model.core.api.ExploreModelsApi;
import one.kii.kiimate.model.core.dai.ExtensionDai;
import one.kii.kiimate.model.core.dai.IntensionDai;
import one.kii.kiimate.model.core.dai.ModelPublicationDai;
import one.kii.kiimate.model.core.dai.ModelSubscriptionDai;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangYanJiong on 02/05/2017.
 */
@Component
public class DefaultExploreModelsApi implements ExploreModelsApi {


    @Autowired
    private ModelPublicationDai modelPublicationDai;

    @Autowired
    private ModelSubscriptionDai modelSubscriptionDai;

    @Autowired
    private ExtensionDai extensionDai;

    @Autowired
    private IntensionDai intensionDai;

    @Override
    public List<Provider> queryProviders(ReadContext context, QueryProvidersForm form) {
        List<ModelPublicationDai.Provider> providerList = modelPublicationDai.getProviders(form.getQuery());
        return DataTools.copy(providerList, Provider.class);
    }

    @Override
    public List<Model> queryModels(ReadContext context, QueryModelsForm form) {
        List<ModelPublicationDai.Publication> publications = modelPublicationDai.queryPublicationsByGroup(form.getQuery());
        List<Model> models = new ArrayList<>();
        for (ModelPublicationDai.Publication publication : publications) {
            ExtensionDai.Extension extension = extensionDai.selectExtensionById(publication.getExtId());


            List<IntensionDai.Intension> intensionList = intensionDai.selectIntensionsByExtId(extension.getId());

            List<Intension> intensions = DataTools.copy(intensionList, Intension.class  );

            int subscriptions = modelSubscriptionDai.countModelSubscriptions(publication.getPubSetHash());

            Model model = DataTools.copy(publication, Model.class);


            model.setGroup(extension.getGroup());
            model.setName(extension.getName());
            model.setIntensions(intensions);

            model.setSubscriptions(subscriptions);

            models.add(model);
        }
        return models;
    }
}
