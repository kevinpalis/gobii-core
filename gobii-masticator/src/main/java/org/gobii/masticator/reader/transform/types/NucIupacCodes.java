package org.gobii.masticator.reader.transform.types;

/**
 * Modified from gobii-model/types/NucIupacCodes to work wiht masticator by Josh L.S.
 * Created by siva on 18-05-2017.
 * @author jdl232
 */
public enum NucIupacCodes {
    AA,
    TT,
    GG,
    CC,
    NN,
    AT,
    AG,
    AC,
    TG,
    TC,
    GC,
    plusminus,
    plus,
    minus;
    public String getName(){
        if(this.equals(plusminus))return "+-";
        else if(this.equals(plus))return "++";
        else if(this.equals(minus))return "--";
        return this.name();
    }
}