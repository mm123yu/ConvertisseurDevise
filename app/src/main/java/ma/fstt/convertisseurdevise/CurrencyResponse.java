package ma.fstt.convertisseurdevise;

import java.util.List;

public class CurrencyResponse {
    private String result;
    private String documentation;
    private String terms_of_use;
    private List<List<String>> supported_codes;

    public List<List<String>> getSupportedCodes() {
        return supported_codes;
    }
}