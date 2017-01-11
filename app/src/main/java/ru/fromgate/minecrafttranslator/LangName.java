package ru.fromgate.minecrafttranslator;

import com.google.gson.annotations.SerializedName;

public class LangName {

    @SerializedName("lang-code")
    public String code;

    @SerializedName("name")
    public String name;

    @SerializedName("native-name")
    public String nativeName;


    public LangName(String langCode, String name, String nativeName) {
        this.code = langCode;
        this.name = name;
        this.nativeName = nativeName;
    }


    public boolean find(String mask) {
        if (code.toLowerCase().startsWith(mask.toLowerCase())) return true;
        if (name.matches("(?i)(^" + mask + ".*)|(^\\w+\\s\\(" + mask + ".*)")) return true;
        if (nativeName.matches("(?i)(^" + mask + ".*)|(^\\w+\\s\\(" + mask + ".*)")) return true;
        return false;
    }
}
