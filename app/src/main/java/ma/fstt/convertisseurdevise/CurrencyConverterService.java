package ma.fstt.convertisseurdevise;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CurrencyConverterService {// représente les points de terminaison DE LAPI
    @GET("codes")
    Call<CurrencyResponse> getCurrencyCodes(@Query("apikey") String apiKey);

    @GET("pair/{fromCurrency}/{toCurrency}/{amount}")
    Call<ConversionResponse> convertCurrency(
            @Path("amount") double amount,
            @Path("fromCurrency") String fromCurrency,
            @Path("toCurrency") String toCurrency
    );
}
