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

    public List<JSONObject> getExtras() {
        if (results == null || results.bindings.isEmpty()) return null;
        List<JSONObject> extras = new ArrayList<>(results.bindings);
        extras.remove(0);
        return extras;
    }

    class Results {
        private List<JSONObject> bindings = new ArrayList<>();

        public void setBindings(List<JSONObject> bindings) {
            this.bindings = bindings;
        }
    }
}
