package wang.yanjiong.metamate.core.fi;

import lombok.Data;
import wang.yanjiong.metamate.core.api.CreateExtensionApi;

/**
 * Created by WangYanJiong on 3/23/17.
 */
public interface AnExtensionExtractor {

    Extension extract(CreateExtensionApi.Form form) throws MissingParamException;

    String hashId(String group, String name, String version);

    enum DataStructures {
        COMPLEX,
        STRING,
        NATURE,
        REAL,
        FLOAT,
        TIMESTAMP
    }

    @Data
    class Extension {

        private String id;

        private String group;

        private String name;

        private String version;

        private String visibility;

        private String structure;
    }

    class MissingParamException extends Exception {
        public MissingParamException(String message) {
            super(message);
        }
    }

}
