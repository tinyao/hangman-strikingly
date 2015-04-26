package im.ycz.hangman.api;

import java.util.Map;

/**
 * Created by tinyao on 4/24/15.
 */
public class HangmanResponse {

    public HangmanApi.ACTION requst;
    public String message;
    public String sessionId;
    public Map<String, Object> data;

}
