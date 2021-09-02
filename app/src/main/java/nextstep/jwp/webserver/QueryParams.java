package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private final Map<String, String> params;

    public QueryParams(Map<String, String> params) {
        this.params = params;
    }

    public QueryParams(String queryString) {
        this(parseParams(queryString));
    }

    public QueryParams() {
        this(new HashMap<>());
    }

    private static Map<String, String> parseParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String queryEntity : queryString.split("&")) {
            int index = queryEntity.indexOf("=");
            String key = queryEntity.substring(0, index);
            String value = queryEntity.substring(index + 1).trim();
            params.put(key, value);
        }
        return params;
    }

    public String get(String param) {
        return params.get(param);
    }
}
