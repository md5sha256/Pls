package io.github.md5sha256.pls;

public enum ModerationCategory {

    HATE("hate"),
    HATE_THREATENING("hate/threatening"),
    SELF_HARM("self-harm"),
    SEXUAL("sexual"),
    SEXUAL_MINORS("sexual/minors"),
    VIOLENCE("violence"),
    VIOLENCE_GRAPHIC("violence/graphic");

    private final String apiName;

    ModerationCategory(String apiName) {
        this.apiName = apiName;
    }

    public String apiName() {
        return this.apiName;
    }

}
