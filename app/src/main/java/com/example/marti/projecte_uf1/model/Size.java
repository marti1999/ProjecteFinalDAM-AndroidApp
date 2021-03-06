
package com.example.marti.projecte_uf1.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Size {

    @SerializedName("$id")
    @Expose
    public String $id;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("size1")
    @Expose
    public String size1;
    @SerializedName("active")
    @Expose
    public Boolean active;


    public Size() {
    }


    public Size(String $id, Integer id, String size1, Boolean active) {
        super();
        this.$id = $id;
        this.id = id;
        this.size1 = size1;
        this.active = active;
    }

    @Override
    public String toString() {
        return size1;
    }

}
