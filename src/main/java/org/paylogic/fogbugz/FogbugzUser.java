package org.paylogic.fogbugz;

import java.util.Objects;

/**
 *
 * @author dirk
 */
public class FogbugzUser {
    public int id;
    public String name;
    public String email;
    public String phone; 
    
    public FogbugzUser(int ix, String name){
        this.id = ix;
        this.name = name;
    }
    
    public FogbugzUser(int ix, String name, String email, String phone){
    	this(ix, name);
    	this.email = email;
    	this.phone = phone;
    }
    
    @Override
    public boolean equals(Object other){
        if (other instanceof FogbugzUser){
            FogbugzUser o = (FogbugzUser) other;
            return o.id == this.id && 
            		Objects.equals(this.name, o.name) &&
            		Objects.equals(this.email, o.email);
        }
        return false;
    }

    @Override
    public String toString() {
    	return String.format("{ id: %d, name: '%s', email: '%s', phone: '%s' }", this.id, this.name, this.email, this.phone);
    }
    
    public int getId() {
		return id;
	}
    
    public String getName() {
		return name;
	}
    
    public String getEmail() {
		return email;
	}
    
    public String getPhone() {
		return phone;
	}
    
}
