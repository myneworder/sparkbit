package org.sparkbit.jsonrpc.autogen;

/**
 * DO NOT EDIT THIS FILE!
 * 
 * Generated by Barrister Idl2Java: https://github.com/coopernurse/barrister-java
 * 
 * Generated at: Fri Nov 07 12:02:54 PST 2014
 */
public class ListWallet implements com.bitmechanic.barrister.BStruct {


    public static class Builder {
        private String _id;
        public Builder id(String id) { this._id = id; return this; }
        private String _description;
        public Builder description(String description) { this._description = description; return this; }
        public ListWallet build() {
            return new ListWallet(this._id, this._description);
        }

        public Builder() { }
        public Builder(ListWallet obj) {
            this._id = obj.getId();
            this._description = obj.getDescription();
        }
    }

    private String id;
    private String description;

    public ListWallet() {
        super();
    }

    public ListWallet(java.util.Map _map) throws com.bitmechanic.barrister.RpcException {
        this(
            (String)com.bitmechanic.barrister.StringTypeConverter.unmarshal(_map.get("id"), false),
            (String)com.bitmechanic.barrister.StringTypeConverter.unmarshal(_map.get("description"), false)
        );
    }

    @org.codehaus.jackson.annotate.JsonCreator
    public ListWallet(
            @org.codehaus.jackson.annotate.JsonProperty("id") String id, 
            @org.codehaus.jackson.annotate.JsonProperty("description") String description) {
        super();
        this.id = id;
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ListWallet:");
        sb.append(" id=").append(id);
        sb.append(" description=").append(description);
        return sb.toString();
    }

    @Override
    public boolean equals(Object _other) {
        if (this == _other) { return true; }
        if (_other == null) { return false; }
        if (!(_other instanceof ListWallet)) { return false; }
        ListWallet _o = (ListWallet)_other;
        if (id == null && _o.id != null) { return false; }
        else if (id != null && !id.equals(_o.id)) { return false; }
        if (description == null && _o.description != null) { return false; }
        else if (description != null && !description.equals(_o.description)) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int _hash = 1;
        _hash = _hash * 31 + (id == null ? 0 : id.hashCode());
        _hash = _hash * 31 + (description == null ? 0 : description.hashCode());
        return _hash;
    }
}
