package tk.foodpedia.android.model.meta;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServerResponse {
    private Results results;

    public void setResults(Results results) {
        this.results = results;
    }

    public String getPayload() {
        return (results == null || results.bindings.isEmpty())
                ? null
                : results.bindings.get(0).toJSONString();
    }

    class Results {
        private List<JSONObject> bindings = new ArrayList<>();

        public void setBindings(List<JSONObject> bindings) {
            this.bindings = bindings;
        }
    }
}
