package pacr.benchmarker.services;

/**
 * This adapter transforms a JSON format to a GSON format for BenchmarkingResult.
 *
 * @author Pavel Zwerschke
 */
public class JSONToGSONAdapter {

    public String convertJSONToGSON(String str) {
        // check that there is no "error" on first level of JSON
        if (errorOnFirstLevel(str)) { // nothing needs to be changed
            return str;
        }

        StringBuilder sb = new StringBuilder(str);

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

    private boolean errorOnFirstLevel(String str) {
        int indexOfError = str.indexOf("\"error\"");
        if (indexOfError == -1) { // no error at all
            return false;
        }
        int indexOfSecondLevel = str.indexOf("{", str.indexOf("{") + 1);

        return indexOfSecondLevel == -1 | indexOfError < indexOfSecondLevel;
    }

}
