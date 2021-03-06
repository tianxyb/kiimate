package com.sinewang.kiimate.model.core.spi;

import one.kii.kiimate.model.core.spi.CreateExtensionSpi;
import one.kii.kiimate.model.core.spi.CreateIntensionSpi;
import one.kii.kiimate.model.core.spi.CreateModelSpi;
import one.kii.summer.io.exception.Panic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WangYanJiong on 4/7/17.
 */
@Component
public class DefaultCreateModelSpi implements CreateModelSpi {

    private static Logger logger = LoggerFactory.getLogger(DefaultCreateModelSpi.class);

    @Autowired
    private CreateExtensionSpi createExtensionSpi;

    @Autowired
    private CreateIntensionSpi createIntensionSpi;

    private <T> Receipt createModel(String ownerId, String group, String name, Class<T> klass) throws Panic {
        if (String.class.getName().equals(klass.getName()) || klass.isPrimitive()) {
            throw new IllegalArgumentException("Class is ILLEGAL:" + klass.getName());
        }
        logger.debug("[begin] group:[{}] createModel:[{}]", klass.getName());
        logger.debug("[before] createExtensionSpi.createMasterPublicExtension:group=[{}],name=[{}]", group, name);
        CreateExtensionSpi.Form extForm = new CreateExtensionSpi.Form();
        extForm.setGroup(group);
        if (name.equals(NAME_ROOT)) {
            extForm.setName(NAME_ROOT);
        } else {
            extForm.setName(name);
        }
        extForm.setOwnerId(ownerId);
        String extId = createExtensionSpi.createMasterPublicExtension(extForm).getId();
        logger.debug("[after] createExtensionSpi.createMasterPublicExtension:extId=[{}]", extId);

        Map<String, Receipt> refs = new HashMap<>();

        List<String> ints = new ArrayList<>();

        for (Field field : klass.getDeclaredFields()) {
            String fieldName = field.getName();
            if (fieldName.startsWith("this$")) {
                continue;
            }
            Class type = field.getType();
            boolean single = type.getComponentType() == null;
            if (!single) {
                type = type.getComponentType();
            }
            if (String.class.getName().equals(type.getName()) || type.isPrimitive()) {
                CreateIntensionSpi.PrimitiveIntensionForm form = new CreateIntensionSpi.PrimitiveIntensionForm();
                form.setSingle(single);
                form.setField(fieldName);
                form.setStructure(type.getSimpleName());
                form.setExtId(extId);
                form.setOwnerId(ownerId);
                String intId = createIntensionSpi.createPublicPrimitiveIntension(form);
                ints.add(intId);
            } else {
                Receipt receipt = createModel(ownerId, group, fieldName, type);
                CreateIntensionSpi.ImportIntensionForm form = new CreateIntensionSpi.ImportIntensionForm();
                form.setSingle(single);
                form.setField(fieldName);
                form.setRefExtId(receipt.getExtId());
                form.setExtId(extId);
                form.setOwnerId(ownerId);
                form.setStructure(type.getSimpleName());
                String intId = createIntensionSpi.createPublicImportIntension(form);
                refs.put(intId, receipt);
            }

        }
        Receipt receipt = new Receipt();
        receipt.setExtId(extId);
        receipt.setInts(ints);
        receipt.setRefs(refs);
        logger.debug("[end] createModel:[{}]", klass.getName());
        return receipt;
    }


    @Override
    public <T> Receipt createModel(Form<T> form) throws Panic {
        return createModel(form.getOwnerId(), form.getGroup(), NAME_ROOT, form.getKlass());
    }

}
