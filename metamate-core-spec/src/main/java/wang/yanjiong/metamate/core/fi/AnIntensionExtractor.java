package wang.yanjiong.metamate.core.fi;

import lombok.Data;
import wang.yanjiong.metamate.core.api.DeclareIntensionApi;

/**
 * Created by WangYanJiong on 3/23/17.
 */
public interface AnIntensionExtractor {

    Intension parseForm(DeclareIntensionApi.Form form);

    String hashId(String extId, String field);

    @Data
    class Intension {

        private String id;

        private String extId;

        private String field;

        private String visibility;

        private String structure;

        private String refExtId;

        private boolean single;
    }
}
