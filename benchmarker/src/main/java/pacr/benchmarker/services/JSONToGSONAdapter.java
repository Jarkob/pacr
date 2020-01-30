package pacr.benchmarker.services;

/**
 * This adapter transforms a JSON format to a GSON format for BenchmarkingResult.
 *
 * @author Pavel Zwerschke
 */
public class JSONToGSONAdapter {

    /**
     * Converts the provided runner JSON String to a correct GSON format.
     * @param json is the JSON string.
     * @return the correct GSON format.
     */
    public String convertJSONToGSON(String json) {
        // check that there is no "error" on first level of JSON
        if (errorOnFirstLevel(json)) { // nothing needs to be changed
            return json;
        }

        StringBuilder sb = new StringBuilder(json);

        // add "benchmarks": { ... }
        int indexForInsertion = sb.indexOf("{");
        sb.insert(indexForInsertion + 1, "\"benchmarks\":{");
        sb.append("}");

        // insert "properties": { ... } into each "<benchname>": { ... }
        // insert "properties": { when entering third '{' level and insert } when exiting third '}' level
        int level = 0;
        for (int i = 0; i < sb.length(); ++i) {
            char c = sb.charAt(i);
            if (c == '{') {
                ++level;
                if (level == 3) {
                    // insert "properties": {
                    String insert = "\"properties\": {";
                    sb.insert(i + 1, insert);
                    i += insert.length();
                }
            } else if (c == '}') {
                --level;
                if (level == 2) {
                    // insert }
                    String insert = "}";
                    sb.insert(i + 1, insert);
                    i += insert.length();
                }
            }
        }

        return sb.toString();
    }

    /**
     * Checks if there is a error on the first level.
     * Then the JSON string does not need to be modified.
     * @param str is the JSON string.
     * @return true if there is an error on the first level, false if there isn't.
     */
    private boolean errorOnFirstLevel(String str) {
        int indexOfError = str.indexOf("\"error\"");
        if (indexOfError == -1) { // no error at all
            return false;
        }
        int indexOfSecondLevel = str.indexOf("{", str.indexOf("{") + 1);

        return indexOfSecondLevel == -1 | indexOfError < indexOfSecondLevel;
    }

}
