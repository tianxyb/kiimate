package one.kii.kiimate.model.core.api;

import lombok.Data;
import one.kii.summer.io.context.ReadContext;

import java.util.Date;
import java.util.List;

/**
 * Created by WangYanJiong on 02/05/2017.
 */
public interface ExploreModelsApi {


    List<Provider> queryProviders(ReadContext context, QueryProvidersForm form);

    List<Model> queryModels(ReadContext context, QueryModelsForm form);

    @Data
    class Provider {
        String providerId;
    }

    @Data
    class Model {

        int subscriptions;

        String providerId;
        String pubSetHash;
        String rootExtId;

        String group;
        String name;

        String publication;
        String version;
        Date beginTime;

        List<Intension> intensions;
    }

    @Data
    class Intension {

        private String id;

        private String field;

        private boolean single;

        private String structure;

        private String refExtId;

        private String visibility;
    }

    @Data
    class QueryProvidersForm {
        private String query;
    }

    @Data
    class QueryModelsForm {
        private String query;
        private String publication;
        private String version;
    }
}
