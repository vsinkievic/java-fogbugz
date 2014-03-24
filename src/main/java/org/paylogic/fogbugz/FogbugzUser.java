package org.paylogic.fogbugz;

/**
 *
 * @author dirk
 */
public class FogbugzUser {
    public int ix;
    public String name;
    
    public FogbugzUser(int ix, String name){
        this.ix = ix;
        this.name = name;
    }
    
    @Override
    public boolean equals(Object other){
        if (other instanceof FogbugzUser){
            FogbugzUser o = (FogbugzUser) other;
            return o.ix == this.ix && o.name == this.name;
        }
        return false;
    }

}
