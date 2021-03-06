package one.kii.kiimate.model.core.api;

import one.kii.summer.io.context.WriteContext;
import one.kii.summer.io.exception.BadRequest;
import one.kii.summer.io.exception.Conflict;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import one.kii.kiimate.model.core.dai.ExtensionDai;
import one.kii.kiimate.model.core.fui.AnVisibilityValidator;

/**
 * Created by WangYanJiong on 3/24/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ComponentScan("com.sinewang.modelmate.core")
@SpringBootTest(classes = {TestDeclareExtensionApi.class})
public class TestDeclareExtensionApi {

    @Autowired
    private DeclareExtensionApi declareExtensionApi;

    @Autowired
    private ExtensionDai extensionDai;

    private String ownerId = "testOwnerId";

    private String operatorId = "testOperatorId";

    private String requestId = "requestId";

    @Test
    public void testGroupOnly() {
        DeclareExtensionApi.CommitForm commitForm = new DeclareExtensionApi.CommitForm();
        commitForm.setGroup("testGroup");

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);

        DeclareExtensionApi.CommitReceipt response = null;
        try {
            response = declareExtensionApi.commit(context, commitForm);
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }
        Assert.assertNull(response);
    }

    @Test
    public void testNameOnly() {
        DeclareExtensionApi.CommitForm commitForm = new DeclareExtensionApi.CommitForm();
        commitForm.setName("testName");

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);

        DeclareExtensionApi.CommitReceipt response = null;
        try {
            response = declareExtensionApi.commit(context, commitForm);
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }
        Assert.assertNull(response);
    }

    @Test
    public void testVersionOnly() {
        DeclareExtensionApi.CommitForm commitForm = new DeclareExtensionApi.CommitForm();
        commitForm.setTree("testTree");

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);

        DeclareExtensionApi.CommitReceipt response = null;
        try {
            response = declareExtensionApi.commit(context, commitForm);
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }
        Assert.assertNull(response);
    }

    @Test
    public void testVisibilityOnly() {
        DeclareExtensionApi.CommitForm commitForm = new DeclareExtensionApi.CommitForm();
        commitForm.setVisibility("testVisibility");

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);

        DeclareExtensionApi.CommitReceipt response = null;
        try {
            response = declareExtensionApi.commit(context, commitForm);
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }
        Assert.assertNull(response);
    }


    @Test
    public void testInvalidVisibility() {
        DeclareExtensionApi.CommitForm commitForm = new DeclareExtensionApi.CommitForm();
        commitForm.setGroup("testGroup");
        commitForm.setName("testName");
        commitForm.setTree("testTree");
        commitForm.setVisibility("testVisibility");

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);

        DeclareExtensionApi.CommitReceipt response = null;
        try {
            response = declareExtensionApi.commit(context, commitForm);
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }
        Assert.assertNull(response);
    }

    @Test
    public void testSave() {
        String group = "testGroup";
        String name = "testName";
        String tree = "testTree";

        DeclareExtensionApi.CommitForm commitForm = new DeclareExtensionApi.CommitForm();
        commitForm.setGroup(group);
        commitForm.setName(name);
        commitForm.setTree(tree);

        WriteContext context = new WriteContext(requestId, ownerId, operatorId);

        commitForm.setVisibility(AnVisibilityValidator.Visibility.PROTECTED.name());
        DeclareExtensionApi.CommitReceipt response = null;
        try {
            response = declareExtensionApi.commit(context, commitForm);
        } catch (BadRequest badRequest) {
            badRequest.printStackTrace();
        } catch (Conflict conflict) {
            conflict.printStackTrace();
        }
        String id = response.getId();
        Assert.assertNotNull(id);


        ExtensionDai.Extension extension = extensionDai.selectExtensionById(id);

        Assert.assertNotNull(extension);

        extensionDai.deleteExtensionById(id);
    }
}
