package com.sinewang.kiimate.model.core.api;

import one.kii.summer.beans.utils.DataTools;
import one.kii.summer.codec.utils.HashTools;
import one.kii.summer.io.context.WriteContext;
import one.kii.summer.io.exception.BadRequest;
import one.kii.summer.io.exception.Conflict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import one.kii.kiimate.model.core.api.SnapshotModelApi;
import one.kii.kiimate.model.core.dai.ExtensionDai;
import one.kii.kiimate.model.core.dai.IntensionDai;
import one.kii.kiimate.model.core.dai.ModelPublicationDai;
import one.kii.kiimate.model.core.fui.AnExtensionExtractor;
import one.kii.kiimate.model.core.fui.AnPublicationExtractor;
import one.kii.kiimate.model.core.fui.AnPublicationValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by WangYanJiong on 05/04/2017.
 */

@Component
public class DefaultSnapshotModelApi implements SnapshotModelApi {

    @Autowired
    private AnPublicationExtractor publicationExtractor;

    @Autowired
    private IntensionDai intensionDai;

    @Autowired
    private ExtensionDai extensionDai;

    @Autowired
    private ModelPublicationDai modelPublicationDai;

    @Autowired
    private AnExtensionExtractor extensionExtractor;


    public Receipt snapshot(WriteContext context, Form form) throws BadRequest, Conflict {

        List<ExtensionDai.Extension> extensions = extensionDai.selectExtensionsByOwnerGroup(context.getOwnerId(), form.getGroup());
        List<ModelPublicationDai.Publication> publications = new ArrayList<>();
        List<IntensionDai.Intension> allIntensions = new ArrayList<>();

        Date date = new Date();
        List<String> ids = new ArrayList<>();
        for (ExtensionDai.Extension extension : extensions) {
            AnPublicationExtractor.Publication snapshot;
            String extId = extensionExtractor.hashId(context.getOwnerId(), form.getGroup(), extension.getName(), TREE_MASTER, VISIBILITY_PUBLIC);
            try {
                snapshot = publicationExtractor.extractSnapshot(form, extId, context.getOperatorId(), date);
            } catch (AnPublicationExtractor.MissingParamException e) {
                throw new BadRequest(e.getMessage());
            }

            List<IntensionDai.Intension> intensions = intensionDai.selectIntensionsByExtId(extId);
            allIntensions.addAll(intensions);

            String pubExtId = publicationExtractor.hashPubExtId(snapshot.getProviderId(), extId, snapshot.getPublication(), snapshot.getVersion());

            for (IntensionDai.Intension intension : intensions) {

                String id = publicationExtractor.hashId(pubExtId, intension.getId());
                ids.add(id);
                ModelPublicationDai.Publication daiPublication = DataTools.copy(snapshot, ModelPublicationDai.Publication.class);
                daiPublication.setPublication(AnPublicationValidator.Publication.SNAPSHOT.name());
                daiPublication.setIntId(intension.getId());
                daiPublication.setId(id);
                daiPublication.setBeginTime(snapshot.getCreatedAt());
                publications.add(daiPublication);
            }
        }
        String[] idArray = ids.toArray(new String[0]);
        Arrays.sort(idArray);
        String pubSetHash = HashTools.hashHex(idArray);

        try {
            modelPublicationDai.savePublications(pubSetHash, publications);
        } catch (ModelPublicationDai.DuplicatedPublication duplicatedPublication) {
            Receipt receipt = DataTools.copy(duplicatedPublication, Receipt.class);
            receipt.setVersion(form.getVersion());
            receipt.setOwnerId(context.getOwnerId());
            throw new Conflict(pubSetHash);
        }

        Receipt receipt = new Receipt();

        receipt.setVersion(form.getVersion());

        receipt.setCreatedAt(date);

        List<Intension> snapshotIntensions = DataTools.copy(allIntensions, Intension.class);

        receipt.setIntensions(snapshotIntensions);

        receipt.setProviderId(form.getProviderId());

        receipt.setOwnerId(context.getOwnerId());

        receipt.setPubSetHash(pubSetHash);

        return receipt;

    }

}
