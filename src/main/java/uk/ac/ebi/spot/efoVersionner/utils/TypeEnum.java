package uk.ac.ebi.spot.efoVersionner.utils;

/**
 * Created by catherineleroy on 09/02/2016.
 */
public enum TypeEnum {
    MAJOR ("mj", "major"),
    MINOR ("mn", "minor"),
    MOST_MINOR("mm", "most minor");

    private final String shortname;
    private final String longName;

    TypeEnum(String shortname, String longName){
        this.shortname = shortname;
        this.longName = longName;
    }

    public String getShortname() {
        return shortname;
    }

    public String getLongName() {
        return longName;
    }

    public TypeEnum get(String shortname){
        for(TypeEnum typeEnum : TypeEnum.values()){
            if(typeEnum.shortname.equals(shortname)){
                return typeEnum;
            }
        }
        return null;
    }




}
