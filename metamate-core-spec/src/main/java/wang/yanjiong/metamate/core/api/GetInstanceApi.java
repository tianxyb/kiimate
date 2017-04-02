package wang.yanjiong.metamate.core.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import one.kii.summer.bound.Response;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by WangYanJiong on 3/27/17.
 */
@RestController
@RequestMapping("/v1")
public interface GetInstanceApi {

    @RequestMapping(value = "/instance/{group}/{name}/{tree:.+}", method = RequestMethod.GET)
    Receipt readInstanceByGroupNameVersionWithOwner(@PathVariable("group") String group,
                                                    @PathVariable("name") String name,
                                                    @PathVariable("tree") String tree,
                                                    @RequestHeader("X-MM-Owner-Id") String ownerId,
                                                    @RequestHeader("X-MM-Operator-Id") String operatorId);

    @Data
    @EqualsAndHashCode(callSuper=false)
    class Receipt extends Response {

        String ownerId;

        Map<String, String> instance;

    }

}
